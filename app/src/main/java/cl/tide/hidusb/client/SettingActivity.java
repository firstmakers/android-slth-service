package cl.tide.hidusb.client;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import cl.tide.hidusb.R;
import cl.tide.hidusb.client.fragments.SettingsFragment;

/**
 * Created by eDelgado on 10-12-14.
 */
public class SettingActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_with_actionbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().
                replace(R.id.content_frame, new SettingsFragment()).commit();
    }
}
