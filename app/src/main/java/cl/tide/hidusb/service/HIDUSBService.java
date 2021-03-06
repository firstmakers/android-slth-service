package cl.tide.hidusb.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import java.util.HashMap;
import java.util.Iterator;

import cl.tide.hidusb.client.BaseActivity;
import cl.tide.hidusb.client.HomeActivity;
import cl.tide.hidusb.service.utils.GeoLocation;
import cl.tide.hidusb.service.utils.StorageManager;
import cl.tide.hidusb.R;

/**
 * This service provides the communication with the device SLTH,
 * managing events connection and disconnection from the device,
 * get temperature, light and humidity
 * **/
public class HIDUSBService extends Service implements SLTHEventListener{

    public static final String ACTION_SAMPLE_STOP = "cl.tide.hidusb.STOP_SAMPLE";
    /** debug tag **/
    private final String DEBUG_TAG = "HID_USB_SERVICE";
    /** Package name **/

    /** Action permission **/
    public final String ACTION_USB_PERMISSION =  "cl.tide.hidusb.USB_PERMISSION";

    /** Action for SLTH events */
    public static final String ACTION_USB_CONNECTED = "cl.tide.hidusb.DEVICE_CONNECTED";
    public static final String ACTION_USB_DISCONNECTED = "cl.tide.hidusb.DEVICE_DISCONNECTED";
    public static final String ACTION_USB_NEW_DATA = "cl.tide.hidusb.NEW_DATA";

    /***/
    public static final String TEMPERATURE = "Temperature";
    public static final String LIGHT = "Light";
    public static final String HUMIDITY = "Humidity";

    public static int NOTIFICATION = R.string.local_service_started;
    private UsbManager usbManager;
    private NotificationManager nNotification;

    private NotificationCompat.Builder mBuilder;
    private NotificationCompat.Builder sensorDetached;

    public StorageManager mStorageManager;

    public GeoLocation geoLocation;

    public int clients = 0;

    /** SLTH vendor id and product id */
    private final int VID = (int)0x04D8;
    private final int PID = (int)0x003F;

    IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public HIDUSBService getServerInstance() {
            return HIDUSBService.this;
        }
    }

    private SLTH mSLTH;

    public HIDUSBService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println(DEBUG_TAG + " created");
        nNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), 0);

        /** Register Usb Permission*/
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbBroadcastReceiver, filter);

        /** Register  Attached USB event */
        IntentFilter attachedFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbBroadcastReceiver, attachedFilter);

        /** Register Detached USB event */
        IntentFilter detachedFilter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbBroadcastReceiver, detachedFilter);

        nNotification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.service_started))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        showNotification();
        UsbDevice device = findDevice();

        if(device!= null){
            try {
                setDevice(device);
            }catch (USBException e){
                e.printStackTrace();
            }
        }
        mStorageManager = new StorageManager(getApplicationContext());
        geoLocation = new GeoLocation(getApplicationContext());

        initializeLocation();
    }

    /** Initialize Geolocation**/
    private void initializeLocation(){
        if(!geoLocation.canGetLocation()){
            geoLocation.showSettingsAlert();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.out.println(DEBUG_TAG + " Low memory detected");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nNotification.cancel(NOTIFICATION);
        unregisterReceiver(mUsbBroadcastReceiver);
        if(mSLTH!=null) mSLTH.close();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)  {
        System.out.println("onBind");
        clients++;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        clients--;
        close();
        return super.onUnbind(intent);
    }

    /**Close the service when it is inactive */
    private void close(){
        if(clients == 0 && !isMonitoring()){
            System.out.print("Closing Service");
            geoLocation.stopUsingGPS();
            stopForeground(true);
            stopSelf();
        }
    }

    /** Find connected devices  for Vendor & Product IDs*/
    private UsbDevice findDevice(){
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            /** Find devices by VID & PID */
            if (isCompatibleDevice(device)) {
                return device;
            }
        }
        return null;
    }
    private void showNotification() {
        nNotification.notify(NOTIFICATION, mBuilder.build());
    }

    public Notification getNotification() {
        return mBuilder.build();
    }

    private void setDevice(UsbDevice device) throws USBException{

        if(!isCompatibleDevice(device)){
            throw new USBException("Not compatible device :" + device.getDeviceName());
        }
        /** Get Explicit permission */
        if(!usbManager.hasPermission(device)){
            System.out.println(DEBUG_TAG + ": getting explicit permission");
            getUsbPermission(device);
        }
        else{
            System.out.println(DEBUG_TAG + ": connecting to slth device ");
            mSLTH = new SLTH(device, usbManager);
            mSLTH.setEventListener(this);
        }

    }

    private void getUsbPermission(UsbDevice device){
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this
                ,0, new Intent(ACTION_USB_PERMISSION), 0);

        usbManager.requestPermission(device, mPermissionIntent);
    }

    /***/
    private boolean isCompatibleDevice(UsbDevice d){
        if(d.getVendorId()== VID && d.getProductId()== PID)
            return true;
        return false;
    }

    public void startMonitoring(int interval, int samples){

        if(mSLTH != null){
            mSLTH.startMonitor(interval, samples);
            mStorageManager.createSample(interval ,samples);
            if(!geoLocation.canGetLocation()) {
                geoLocation.showSettingsAlert();
            }
            mStorageManager.updateLocation(geoLocation.getLatitude(),
                    geoLocation.getLongitude());

        }else {
            System.out.println("Not access to device");
        }
    }

    public synchronized void stopMonitoring(){
        if(mSLTH != null){
            mStorageManager.updateLocation(
                    geoLocation.getLatitude(),
                    geoLocation.getLongitude());
            mSLTH.stopMonitor();
            //mStorageManager.printData();
        }
    }
    public boolean isMonitoring(){
        if(mSLTH != null)
            return mSLTH.isMonitoring();
        return  false;
    }

    public boolean isDeviceConnected(){
        return mSLTH != null;
    }

    /** Send disconnection event  via Broadcast receiver **/
    private void onDisconnect(){
        mBuilder.setContentText(getString(R.string.device_detach));
        showNotification();
        sendBroadcast(new Intent().setAction(ACTION_USB_DISCONNECTED));
    }
    /** Send connected event via Broadcast receiver  **/
    private void onConnect(){
        mBuilder.setContentText(getString(R.string.device_attach));
        showNotification();
        sendBroadcast(new Intent().setAction(ACTION_USB_CONNECTED));
    }


    @Override
    public void OnSensorDetached() {
        System.out.println(" OnSensorDetached ");
        if(sensorDetached == null) {

            sensorDetached = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.sensor_detach))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_LOW);
            nNotification.notify(4567, sensorDetached.build());
        }
    }

    @Override
    public void OnStopMonitoring() {
        mBuilder.setContentText(getString(R.string.stop_monitoring));
        showNotification();
        sendBroadcast(new Intent().setAction(ACTION_SAMPLE_STOP));
        close();
    }

    @Override
    public void OnNewSample(double temperature, double light, int humidity) {
        //save data into db
        mStorageManager.saveData(temperature,light,humidity);
        //notify via Intent when new data received
        Intent newData = new Intent();
        newData.putExtra(TEMPERATURE,temperature);
        newData.putExtra(LIGHT,light);
        newData.putExtra(HUMIDITY,humidity);
        newData.setAction(ACTION_USB_NEW_DATA);
        sendBroadcast(newData);
    }

    private final BroadcastReceiver mUsbBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            //
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null && isCompatibleDevice(device)){
                            System.out.println("Permissions granted for device " + device.getDeviceName());

                            try {
                                setDevice(device);
                            } catch (USBException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        System.out.println("Permission denied for device " + device.getDeviceName());
                        getUsbPermission(device);
                    }
                }
            }else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                System.out.println(" A device is attached " + device.getDeviceName());
                if(mSLTH == null && isCompatibleDevice(device)){
                    try {
                        setDevice(device);
                        onConnect();
                    } catch (USBException e) {
                        e.printStackTrace();
                    }
                }

            }else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)){

                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                System.out.println("a device disconnected " + device.getDeviceName()
                        +" PID&VID "+ device.getProductId()+ " "+ device.getVendorId());

                if (mSLTH != null && mSLTH.isCurentDevice(device)) {
                    System.out.println("BroadcastReceiver USB Disconnected");

                    if(mSLTH != null){
                        mSLTH.close();
                        mSLTH = null;
                        onDisconnect();
                    }

                }
            }
        }
    };

}
