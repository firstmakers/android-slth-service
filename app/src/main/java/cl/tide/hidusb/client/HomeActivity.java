package cl.tide.hidusb.client;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onConnect() {
        super.onConnect();
        Toast.makeText(this,"se ha conectado una slth", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDisconnect() {
        super.onDisconnect();
        Toast.makeText(this,"se ha desconectado una slth", Toast.LENGTH_LONG).show();
    }

    private void startMonitor(){
        startMonitor(1, 2000);
    }


    @Override
    protected void onNewSample(double t, double l, int h) {
        if(sensorView!= null){
            sensorView.setTextTemperature(new ValueItem(t));
            sensorView.setTextLight(new ValueItem(l));
            sensorView.setTextHumidity(new ValueItem(h));
        }
    }

    @Override
    protected void onStopMonitor() {
        btnStart.setText(R.string.btn_start);
    }

    @Override
    public void onFragmentInteraction(View v) {

        btnStart = (Button) v;
        System.out.println(btnStart.getText());
        if(mService.isDeviceConnected() &&
                btnStart.getText().equals(getString(R.string.btn_start))) {
            btnStart.setText(R.string.btn_stop);
            startMonitor();
        }
        else if(mService.isDeviceConnected() &&
                btnStart.getText().equals(getString(R.string.btn_stop))){
            btnStart.setText(R.string.btn_start);
            stopMonitor();
        }else
        {
            Toast.makeText(this,"no device or permision denied", Toast.LENGTH_LONG).show();
        }
    }
}
