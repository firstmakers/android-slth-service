package cl.tide.hidusb.service;

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
import java.util.HashMap;
import java.util.Iterator;

/**
 * This service provides the communication with the device SLTH,
 * managing events connection and disconnection from the device
 * **/
public class HIDUSBService extends Service {

    /** debug tag **/
    private final String DEBUG_TAG = "HID_USB_SERVICE";
    /** Package name **/

    /** Action permission **/
    public final String ACTION_USB_PERMISSION =  "cl.tide.hidusb.USB_PERMISSION";

    private UsbManager usbManager;
    private NotificationManager nNotification;

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

        UsbDevice device = findDevice();

        if(device!= null){
            try {
                setDevice(device);
            }catch (USBException e){
                e.printStackTrace();
            }

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
        unregisterReceiver(mUsbBroadcastReceiver);
        if(mSLTH!=null) mSLTH.close();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)  {
        System.out.print("onBind");
        return mBinder;
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

    private void setDevice(UsbDevice device) throws USBException{

        if(!isCompatibleDevice(device)){
            throw new USBException("Not compatible device :" + device.getDeviceName());
        }
        /** Get Explicit permission */
        if(!usbManager.hasPermission(device)){
            System.out.println(DEBUG_TAG + ": getting explicit permission");
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this
                    ,0, new Intent(ACTION_USB_PERMISSION), 0);
            usbManager.requestPermission(device, mPermissionIntent);
        }
        else{
            System.out.println(DEBUG_TAG + ": connecting to slth device ");
            mSLTH = new SLTH(device, usbManager);
        }

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
        }else {
            System.out.println("Not access to device");
        }
    }
    public synchronized void stopMonitoring(){
        if(mSLTH != null){
            mSLTH.stopMonitor();
        }
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
                    }
                }
            }else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                System.out.println(" A device is attached " + device.getDeviceName());
                if(mSLTH == null && isCompatibleDevice(device)){
                    try {
                        setDevice(device);
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
                        //onDisconnect();
                    }

                }
            }
        }
    };

}
