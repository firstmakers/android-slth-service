package cl.tide.hidusb.client;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ActivityManager.RunningServiceInfo;

import cl.tide.hidusb.client.fragments.AboutFragment;
import cl.tide.hidusb.client.fragments.ChartFragment;
import cl.tide.hidusb.client.fragments.SensorFragment;
import cl.tide.hidusb.client.fragments.SettingsFragment;
import cl.tide.hidusb.client.util.NavigationDrawerFragment;
import cl.tide.hidusb.service.HIDUSBService;
import cl.tide.hidusb.R;
import cl.tide.hidusb.service.storage.sqlite.Samples;

public abstract class BaseActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    public HIDUSBService mService;
    private Intent mIntentService;
    public boolean bounded;
    public boolean device = false;
    public static boolean MONITORING = false;
    protected NavigationDrawerFragment mNavigationDrawerFragment;
    public SensorFragment sensorView;
    public ChartFragment chartView;
    private CharSequence mTitle;
    private String[] titles;



    protected ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bounded = true;
            HIDUSBService.LocalBinder mLocalBinder = (HIDUSBService.LocalBinder)service;
            mService = mLocalBinder.getServerInstance();

            mService.startForeground(mService.NOTIFICATION, mService.getNotification());
            device = mService.isDeviceConnected();
            MONITORING = mService.isMonitoring();
            onBindService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bounded = false;
            device = false;
            onUnBindService();
        }
    };

    public boolean isServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void bindUSBService(){
        bindService(mIntentService, mConnection, BIND_AUTO_CREATE);
    }

    public void unbindUSBService(){
        if(mConnection!= null && bounded){
            unbindService(mConnection);
        }
    }

    public void startUSBService(){
        startService(mIntentService);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        mIntentService = new Intent(this, HIDUSBService.class);
        //register broadcast from slth
        if (slthEvents != null) {

            registerReceiver(slthEvents, new IntentFilter(HIDUSBService.ACTION_USB_CONNECTED));
            registerReceiver(slthEvents, new IntentFilter(HIDUSBService.ACTION_USB_DISCONNECTED));
            registerReceiver(slthEvents, new IntentFilter(HIDUSBService.ACTION_USB_NEW_DATA));
            registerReceiver(slthEvents, new IntentFilter(HIDUSBService.ACTION_SAMPLE_STOP));

        }

        if (isServiceRunning(HIDUSBService.class)) {
            Intent intent = getIntent();
            if (intent != null) {
                System.out.println(intent);
                String action = intent.getAction();
                if (action != null && action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                    finish();
                    return;
                }
            }
        }

        mTitle = getTitle();
        titles = getResources().getStringArray(R.array.nav_string);
        //custom action bar
       /* final ActionBar actionBar = getSupportActionBar();
        BitmapDrawable background = new BitmapDrawable (BitmapFactory.decodeResource(getResources(), R.drawable.actionbar_repeat));
        background.setTileModeX(android.graphics.Shader.TileMode.REPEAT);
        actionBar.setBackgroundDrawable(background);*/

    }

    public void startMonitor(int i, int m){
        MONITORING = true;
        mService.startMonitoring(i,m);
    }
    public void stopMonitor(){
        mService.stopMonitoring();

    };

    public Samples getLastSample(){
        return mService.mStorageManager.getLastSample();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isServiceRunning(HIDUSBService.class)){
            bindUSBService();
        }
        else{
            bindUSBService();
            startUSBService();
        }

    }

    @Override
    protected void onDestroy() {
        unbindUSBService();
        //unregister slth events
        unregisterReceiver(slthEvents);
        super.onDestroy();
    }


    private final BroadcastReceiver slthEvents = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if(HIDUSBService.ACTION_USB_CONNECTED.equals(action)){
                device = true;
                onConnect();
                //

            }
            else if (HIDUSBService.ACTION_USB_DISCONNECTED.equals(action)){
                onDisconnect();
                device = false;
                //
            }
            else if(HIDUSBService.ACTION_USB_NEW_DATA.equals(action)){
                Bundle extras = intent.getExtras();
                onNewSample(
                        extras.getDouble(HIDUSBService.TEMPERATURE),
                        extras.getDouble(HIDUSBService.LIGHT),
                        extras.getInt(HIDUSBService.HUMIDITY)
                        );
            }
            else if(HIDUSBService.ACTION_SAMPLE_STOP.equals(action)){
                MONITORING = false;
                onStopMonitor();
            }
        }
    };



    // Navegación barra lateral
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position){

            case 0:
                sensorView = SensorFragment.newInstance(position, false);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, sensorView)
                        .commit();

                break;
            case 1://graficos
                chartView = ChartFragment.newInstance(position);
                fragmentManager.beginTransaction().addToBackStack("chartview")
                        .replace(R.id.container, chartView)
                        .commit();
                    chartView.setSample(getLastSample());
                break;

            case 2://datosAlmacenados
                startActivity(new Intent(this, DataManagerActivity.class));
                break;
            case 3: //config
                startActivity(new Intent(this , SettingActivity.class));
                break;
            case 4:
                fragmentManager.beginTransaction().addToBackStack("aboutview")
                        .replace(R.id.container, AboutFragment.newInstance(position)).commit();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void onSectionAttached(int number) {
        if(titles == null) {
            titles = getResources().getStringArray(R.array.nav_string);
        }else {
            Log.e("main ", " titles " + titles.length);
            mTitle = titles[number];
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    // creación del menu del actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment!= null && !mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            if(mTitle != null && mTitle.equals(titles[0])){
                getMenuInflater().inflate(R.menu.main, menu);
                restoreActionBar();
                return true;
            }

        }
        return super.onCreateOptionsMenu(menu);
    }

    // Selección del menú del actionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /** Protected Methods*/
    protected void onNewSample(double t, double l, int h){}
    protected void onConnect(){}
    protected void onDisconnect(){}
    protected void onBindService(){}
    protected void onUnBindService(){}
    protected void onStopMonitor(){}
    /**End protected methods*/
}
