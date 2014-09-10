package cl.tide.hidusb.client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cl.tide.hidusb.R;
import cl.tide.hidusb.client.fragments.SensorFragment;
import cl.tide.hidusb.client.model.ValueItem;
import cl.tide.hidusb.client.util.NavigationDrawerFragment;

/**
 * Created by eDelgado on 22-08-14.
 */

public class HomeActivity extends BaseActivity implements SensorFragment.OnFragmentClickListener{

    Button btnStart;

    /*TextView indInterval;
    TextView indSample;
    SharedPreferences sharedPreferences;
    View footer;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        /*footer = View.inflate(this, R.layout.footer_main,null);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        indInterval = (TextView)findViewById(R.id.ind_interval);
        indInterval.setText(sharedPreferences.getInt("pref_sample_interval",0)+ " "+getString(R.string.ind_interval));
        indSample = (TextView)findViewById(R.id.ind_samples);
        indSample.setText(sharedPreferences.getInt("pref_sample_number",0)+" "+ getString(R.string.ind_sample));*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onConnect() {
        super.onConnect();
        Toast.makeText(this, getString(R.string.on_attach), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDisconnect() {
        super.onDisconnect();
        Toast.makeText(this, getString(R.string.on_detach), Toast.LENGTH_LONG).show();
    }

    private void startMonitor() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int interval = sp.getInt("pref_sample_interval", 1);
        int samples = sp.getInt("pref_sample_number", 60);
        Log.i("HOME ACTIVITY ", " Starting monitor, interval " + interval + " samples " + samples);
        startMonitor(interval, samples);
    }


    @Override
    protected void onNewSample(double t, double l, int h) {
        if (sensorView != null) {
            sensorView.setTextTemperature(new ValueItem(t));
            sensorView.setTextLight(new ValueItem(l));
            sensorView.setTextHumidity(new ValueItem(h));
        }
        if (chartView != null) {
            chartView.updateChart(t, l, h);
        }
    }

    @Override
    protected void onStopMonitor() {
        if(btnStart!= null)
            btnStart.setText(R.string.btn_start);
    }

    @Override
    public void onFragmentInteraction(View v) {

        btnStart = (Button) v;
        String text = (String) btnStart.getText();
        if (mService.isDeviceConnected() &&
                text.equals(getString(R.string.btn_start))) {
            btnStart.setText(R.string.btn_stop);
            startMonitor();
        } else if (mService.isDeviceConnected() &&
                text.equals(getString(R.string.btn_stop))) {
            btnStart.setText(R.string.btn_start);
            stopMonitor();
        } else {
            Toast.makeText(this, getString(R.string.no_device), Toast.LENGTH_LONG).show();
        }
    }
    /*
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       if(key.equals("pref_sample_interval")){
           indInterval.setText(sharedPreferences.getInt("pref_sample_interval",0)+ " "+getString(R.string.ind_interval));
        } else if(key.equals("pref_sample_number")){
           indSample.setText(sharedPreferences.getInt("pref_sample_number",0)+" "+ getString(R.string.ind_sample));
        }
    }*/
}
