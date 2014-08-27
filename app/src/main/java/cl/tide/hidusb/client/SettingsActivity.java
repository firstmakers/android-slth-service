package cl.tide.hidusb.client;


import android.os.Bundle;
import android.preference.PreferenceActivity;

import cl.tide.hidusb.R;


public class SettingsActivity extends PreferenceActivity {
    public static String PREFERENCES = "cl.tide.hidusb.prefences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
    }
}
