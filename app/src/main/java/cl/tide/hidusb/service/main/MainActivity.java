package cl.tide.hidusb.service.main;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.ActivityManager.RunningServiceInfo;
import cl.tide.hidusb.service.HIDUSBService;
import cl.tide.hidusb_service.R;

public class MainActivity extends ActionBarActivity {

    private TextView textView;
    private Button btnStart;
    private Button btnStop;

    private HIDUSBService mService;
    private Intent mIntentService;
    private boolean bounded;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bounded = true;
            HIDUSBService.LocalBinder mLocalBinder = (HIDUSBService.LocalBinder)service;
            mService = mLocalBinder.getServerInstance();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bounded = false;
        }
    };

    private boolean isServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void bindUSBService(){
        bindService(mIntentService, mConnection, BIND_AUTO_CREATE);
    }

    private void unbindUSBService(){
        if(mConnection!= null){
            unbindService(mConnection);
        }
    }
    private void startUSBService(){
        startService(mIntentService);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.tv);
        btnStart = (Button) findViewById(R.id.button);
        btnStop = (Button) findViewById(R.id.button2);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainActivity","Clicking start button " + bounded);
                if(bounded)
                    mService.startMonitoring(1, 200);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mService!= null)
                    mService.stopMonitoring();
            }
        });

        mIntentService = new Intent(this, HIDUSBService.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        super.onDestroy();
    }
}
