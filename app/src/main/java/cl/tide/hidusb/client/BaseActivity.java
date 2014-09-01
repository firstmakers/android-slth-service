package cl.tide.hidusb.client;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ActivityManager.RunningServiceInfo;
import android.widget.Button;

import cl.tide.hidusb.client.fragments.AboutFragment;
import cl.tide.hidusb.client.fragments.ChartFragment;
import cl.tide.hidusb.client.fragments.SensorFragment;
import cl.tide.hidusb.client.fragments.StatisticsFragment;
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
    public StatisticsFragment statisticsView;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
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
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bounded = false;
            device = false;
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

    protected void onNewSample(double t, double l, int h){
    }
    protected  void onConnect(){
    }
    protected  void onDisconnect(){
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

    protected abstract void onStopMonitor();



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
                fragmentManager.beginTransaction()
                        .replace(R.id.container, chartView)
                        .commit();
                    chartView.setSample(getLastSample());
                break;
            case 2://estadisticas
                statisticsView = StatisticsFragment.newInstance(position);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, statisticsView)
                        .commit();
                break;
            case 3://datosAlmacenados
                break;
            case 4: //config
                startActivity(new Intent(this ,SettingsActivity.class));
                break;
            case 5:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AboutFragment.newInstance(position)).commit();

        }

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
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (item.getItemId() == R.id.action_example) {
            sensorView.resetView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
