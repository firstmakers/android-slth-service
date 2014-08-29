package cl.tide.hidusb.client.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.triggertrap.seekarc.SeekArc;
import cl.tide.hidusb.R;
import cl.tide.hidusb.client.BaseActivity;
import cl.tide.hidusb.client.model.ValueItem;
import cl.tide.hidusb.client.util.CustomMessage;
import cl.tide.hidusb.client.util.Message;
import cl.tide.hidusb.client.util.RangeOptionPreferences;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SensorFragment.OnFragmentClickListener} interface
 * to handle interaction events.
 * Use the {@link SensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorFragment extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_PARAM2 = "param2";
    private ViewGroup viewGroupSensor;
    private final static String TAG = "SENSOR FRAGMENT";

    private Button btnIniciar;
    private TextView tempValue;
    private TextView humValue;
    private TextView lightValue;
    private TextView tempDecimal;
    private TextView lightDecimal;
    private TextView humDecimal;

    private TextView statusTemp;
    private TextView statusLight;
    private TextView statusHum;

    private SeekArc seekbarTemperature;
    private SeekArc seekbarHumidity;
    private SeekArc seekbarLight;

    private int tempMin;
    private int tempMax;
    private int lightMin;
    private int ligthMax;
    private int humMin;
    private int humMax;

    private int color_low;
    private int color_normal;
    private int color_high;

    private SharedPreferences sharedPreferences;

    // TODO: Rename and change types of parameters
    private int mParam1;
    private boolean monitoring = false;

    private OnFragmentClickListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SensorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SensorFragment newInstance(int param1, boolean monitoring) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, param1);
        args.putBoolean(ARG_PARAM2, monitoring);
        fragment.setArguments(args);
        return fragment;
    }

    public SensorFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_SECTION_NUMBER);
            monitoring = getArguments().getBoolean(ARG_PARAM2);
        }
        Resources resources = getResources();
        color_low = resources.getColor(R.color.darkorange);
        color_high = resources.getColor(R.color.darkred);
        color_normal = resources.getColor(R.color.darkgreen);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        tempMin = sharedPreferences.getInt(RangeOptionPreferences.KEY_TEMP_MIN, RangeOptionPreferences.DEFAULT_MIN_VALUE);
        tempMax = sharedPreferences.getInt(RangeOptionPreferences.KEY_TEMP_MAX, RangeOptionPreferences.DEFAULT_MIN_VALUE);
        humMax = sharedPreferences.getInt(RangeOptionPreferences.KEY_HUM_MAX, RangeOptionPreferences.DEFAULT_MAX_VALUE);
        humMin = sharedPreferences.getInt(RangeOptionPreferences.KEY_HUM_MIN, RangeOptionPreferences.DEFAULT_MIN_VALUE);
        lightMin = sharedPreferences.getInt(RangeOptionPreferences.KEY_LIGHT_MIN, RangeOptionPreferences.DEFAULT_MIN_VALUE);
        ligthMax = sharedPreferences.getInt(RangeOptionPreferences.KEY_LIGHT_MAX, RangeOptionPreferences.DEFAULT_MAX_VALUE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sensor, container, false);

        viewGroupSensor = (ViewGroup) v.findViewById(R.id.container_sensor);
        btnIniciar = (Button) v.findViewById(R.id.btn_iniciar);

        if(BaseActivity.MONITORING) btnIniciar.setText(R.string.btn_stop);
        createSensorView();
        return v;
    }

    //crea la vista de los sensores
    private void createSensorView() {

        //vista del sensor de temperatura
        final ViewGroup temperature = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                R.layout.sensor_layout_temp, viewGroupSensor, false);


        tempValue = (TextView) temperature.findViewById(R.id.temp_value);
        seekbarTemperature = (SeekArc) temperature.findViewById(R.id.seekbar_temp);
        statusTemp = (TextView) temperature.findViewById(R.id.temperature_status_txt);

        tempDecimal = (TextView) temperature.findViewById(R.id.temp_decimal);
        temperature.findViewById(R.id.sensor_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGroupSensor.removeView(temperature);
                if (viewGroupSensor.getChildCount() == 1) {
                    showMessage("No hay sensores Seleccionados", CustomMessage.WARNING);
                    btnIniciar.setVisibility(View.GONE);
                    //temperature.findViewById(R.id.container_empty).setVisibility(View.VISIBLE);
                }
            }
        });
        //vista del sensor de luz
        final ViewGroup light = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                R.layout.sensor_layout_light, viewGroupSensor, false);
        lightValue = (TextView) light.findViewById(R.id.lux_value);
        lightDecimal = (TextView) light.findViewById(R.id.lux_decimal);
        seekbarLight = (SeekArc) light.findViewById(R.id.seekbar_light);
        statusLight = (TextView) light.findViewById(R.id.light_status_txt);

        light.findViewById(R.id.sensor_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGroupSensor.removeView(light);
                if (viewGroupSensor.getChildCount() == 1) {
                    showMessage("No hay sensores Seleccionados", CustomMessage.WARNING);
                    btnIniciar.setVisibility(View.GONE);
                    //temperature.findViewById(R.id.container_empty).setVisibility(View.VISIBLE);
                }
            }
        });
        //vista del sensor de humedad
        final ViewGroup humidity = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                R.layout.sensor_layout_hum, viewGroupSensor, false);
        humValue = (TextView) humidity.findViewById(R.id.hum_value);
        humDecimal = (TextView) humidity.findViewById(R.id.hum_decimal);
        seekbarHumidity = (SeekArc) humidity.findViewById(R.id.seekbar_hum);
        statusHum = (TextView) humidity.findViewById(R.id.humidity_status_txt);

        humidity.findViewById(R.id.sensor_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGroupSensor.removeView(humidity);
                if (viewGroupSensor.getChildCount() == 1) {
                    showMessage("No hay sensores Seleccionados", CustomMessage.WARNING);
                    btnIniciar.setVisibility(View.GONE);
                    //temperature.findViewById(R.id.container_empty).setVisibility(View.VISIBLE);
                }
            }
        });

        viewGroupSensor.addView(temperature, 0);
        viewGroupSensor.addView(light, 1);
        viewGroupSensor.addView(humidity, 2);
        btnIniciar.setOnClickListener(this);
    }

    private void setStatusTemp(int st) {
        if (st < tempMin) {
            statusTemp.setText(R.string.sensor_low);
            statusTemp.setTextColor(color_low);
        } else if (st > tempMax) {
            statusTemp.setText(R.string.sensor_high);
            statusTemp.setTextColor(color_high);
        } else {
            statusTemp.setText(R.string.sensor_medium);
            statusTemp.setTextColor(color_normal);
        }
    }

    private void setStatusLight(int sl) {
        if (sl < lightMin) {
            statusLight.setText(R.string.sensor_low);
            statusLight.setTextColor(color_low);
        } else if (sl > ligthMax) {
            statusLight.setText(R.string.sensor_high);
            statusLight.setTextColor(color_high);
        } else {
            statusLight.setText(R.string.sensor_medium);
            statusLight.setTextColor(color_normal);
        }

    }

    private void setStatusHum(int sh) {
        if (sh < humMin) {
            statusHum.setText(R.string.sensor_low);
            statusHum.setTextColor(color_low);
        } else if (sh > humMax) {
            statusHum.setText(R.string.sensor_high);
            statusHum.setTextColor(color_high);
        } else {
            statusHum.setText(R.string.sensor_medium);
            statusHum.setTextColor(color_normal);

        }
    }

    //cambia el valor de la etiqueta temperatura
    public void setTextTemperature(ValueItem t) {
        try{
            this.tempDecimal.setText("." + t.getDecimal());
            this.tempValue.setText(t.getInteger() + "");
            seekbarTemperature.setProgress(t.getInteger());
            setStatusTemp(t.getInteger());
        }catch (IllegalStateException e){
            Log.e(TAG, "error _:" +e.toString());
        }
    }

    // cambia el valor de la etiqueta luminosidad
    public void setTextLight(ValueItem l) {
        try{
            this.lightDecimal.setText("." + l.getDecimal());
            this.lightValue.setText(l.getInteger() + "");
            seekbarLight.setProgress(l.getInteger());
            setStatusLight(l.getInteger());
        }catch (IllegalStateException e){
            Log.e(TAG, "error _:" +e.toString());
        }
    }

    //cambia el valor de la etiqueta humedad
    public void setTextHumidity(ValueItem h) {
        try{
            this.humDecimal.setText("." + h.getDecimal());
            this.humValue.setText(h.getInteger() + "");
            seekbarHumidity.setProgress(h.getInteger());
            setStatusHum(h.getInteger());
        }catch (IllegalStateException e){
            Log.e(TAG, "error _:" +e.toString());
        }

    }


    public void resetView() {
        if (viewGroupSensor.getChildCount() == 1) {
            createSensorView();
            btnIniciar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((BaseActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        try {
            mListener = (OnFragmentClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_iniciar:
                mListener.onFragmentInteraction(v);
                break;
            default:
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        System.out.print("onSharedPreference changed "+ key);
        if (key.equals(RangeOptionPreferences.KEY_TEMP_MAX)) {
            tempMax = sharedPreferences.getInt(RangeOptionPreferences.KEY_TEMP_MAX, 0);
        } else if (key.equals(RangeOptionPreferences.KEY_TEMP_MIN)) {
            tempMin = sharedPreferences.getInt(RangeOptionPreferences.KEY_TEMP_MIN, 0);
        } else if (key.equals(RangeOptionPreferences.KEY_LIGHT_MAX)) {
            ligthMax = sharedPreferences.getInt(RangeOptionPreferences.KEY_LIGHT_MAX, 0);
        } else if (key.equals(RangeOptionPreferences.KEY_LIGHT_MIN)) {
            lightMin = sharedPreferences.getInt(RangeOptionPreferences.KEY_LIGHT_MIN, 0);
        } else if (key.equals(RangeOptionPreferences.KEY_HUM_MAX)) {
            humMax = sharedPreferences.getInt(RangeOptionPreferences.KEY_HUM_MAX, 0);
        } else if (key.equals(RangeOptionPreferences.KEY_TEMP_MIN)) {
            humMin = sharedPreferences.getInt(RangeOptionPreferences.KEY_HUM_MIN, 0);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentClickListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(View v);
    }

    private void showMessage(String m, int t) {
        Message.show(getActivity(), m, t);
    }

}
