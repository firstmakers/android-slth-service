package cl.tide.hidusb.client.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.triggertrap.seekarc.SeekArc;

import cl.tide.hidusb.R;
import cl.tide.hidusb.client.BaseActivity;
import cl.tide.hidusb.client.model.ValueItem;
import cl.tide.hidusb.client.util.CustomMessage;
import cl.tide.hidusb.client.util.Message;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SensorFragment.OnFragmentClickListener} interface
 * to handle interaction events.
 * Use the {@link SensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SensorFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_PARAM2 = "param2";
    private ViewGroup viewGroupSensor;

    private Button btnIniciar;
    private TextView tempValue;
    private TextView humValue;
    private TextView lightValue;
    private TextView tempDecimal;
    private TextView lightDecimal;
    private TextView humDecimal;

    private SeekArc seekbarTemperature;
    private SeekArc seekbarHumidity;
    private SeekArc seekbarLight;

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;

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
    public static SensorFragment newInstance(int param1, String param2) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SensorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_SECTION_NUMBER);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sensor,container,false);

        viewGroupSensor = (ViewGroup) v.findViewById(R.id.container_sensor);
        btnIniciar = (Button) v.findViewById(R.id.btn_iniciar);

        createSensorView();

        return v;
    }

    //crea la vista de los sensores
    private void createSensorView(){

        //vista del sensor de temperatura
        final ViewGroup temperature = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                R.layout.sensor_layout_temp, viewGroupSensor, false);
        tempValue = (TextView)temperature.findViewById(R.id.temp_value);
        seekbarTemperature = (SeekArc) temperature.findViewById(R.id.seekbar_temp);

        tempDecimal = (TextView)temperature.findViewById(R.id.temp_decimal);
        temperature.findViewById(R.id.sensor_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGroupSensor.removeView(temperature);
                if(viewGroupSensor.getChildCount() == 1 ) {
                    showMessage("No hay sensores Seleccionados", CustomMessage.WARNING);
                    btnIniciar.setVisibility(View.GONE);
                    //temperature.findViewById(R.id.container_empty).setVisibility(View.VISIBLE);
                }
            }
        });
        //vista del sensor de luz
        final ViewGroup light= (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                R.layout.sensor_layout_light, viewGroupSensor, false);
        lightValue = (TextView)light.findViewById(R.id.lux_value);
        lightDecimal = (TextView)light.findViewById(R.id.lux_decimal);
        seekbarLight = (SeekArc) light.findViewById(R.id.seekbar_light);

        light.findViewById(R.id.sensor_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGroupSensor.removeView(light);
                if(viewGroupSensor.getChildCount() == 1 ) {
                    showMessage("No hay sensores Seleccionados", CustomMessage.WARNING);
                    btnIniciar.setVisibility(View.GONE);
                    //temperature.findViewById(R.id.container_empty).setVisibility(View.VISIBLE);
                }
            }
        });
        //vista del sensor de humedad
        final ViewGroup humidity = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                R.layout.sensor_layout_hum, viewGroupSensor, false);
        humValue = (TextView)humidity.findViewById(R.id.hum_value);
        humDecimal = (TextView)humidity.findViewById(R.id.hum_decimal);
        seekbarHumidity = (SeekArc) humidity.findViewById(R.id.seekbar_hum);

        humidity.findViewById(R.id.sensor_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGroupSensor.removeView(humidity);
                if(viewGroupSensor.getChildCount() == 1 ) {
                    showMessage("No hay sensores Seleccionados", CustomMessage.WARNING);
                    btnIniciar.setVisibility(View.GONE);
                    //temperature.findViewById(R.id.container_empty).setVisibility(View.VISIBLE);
                }
            }
        });

        viewGroupSensor.addView(temperature,0);
        viewGroupSensor.addView(light,1);
        viewGroupSensor.addView(humidity,2);
        btnIniciar.setOnClickListener(this);
    }

    private void setSeekArc(){

    }

    //cambia el valor de la etiqueta temperatura
    public void setTextTemperature(ValueItem t){
       // if(!tempDecimal.getText().equals(t.getDecimal())) {
         //   this.tempDecimal.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            this.tempDecimal.setText("." + t.getDecimal());
       // }
        //if(!tempValue.getText().equals(t.getInteger())) {
            this.tempValue.setText(t.getInteger() + "");
           // this.tempValue.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
       // }
        seekbarTemperature.setProgress(t.getInteger());

    }

    // cambia el valor de la etiqueta luminosidad
    public void setTextLight(ValueItem l){
       // if(!lightDecimal.getText().equals(l.getDecimal())) {
          //  this.lightDecimal.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            this.lightDecimal.setText("." + l.getDecimal());
       // }
        //if(!lightValue.getText().equals(l.getInteger()+"")) {
           // this.lightValue.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            this.lightValue.setText(l.getInteger() + "");
        seekbarLight.setProgress(l.getInteger());
        //}

    }

    //cambia el valor de la etiqueta humedad
    public void setTextHumidity(ValueItem h){

//        if(!humDecimal.getText().equals(h.getDecimal()+"")) {
          //  this.humDecimal.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            this.humDecimal.setText("." + h.getDecimal());

       // }
       // if(!humValue.getText().equals(h.getInteger()+"")) {
         //   this.humValue.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            this.humValue.setText(h.getInteger() + "");
       // }
        seekbarHumidity.setProgress(h.getInteger());
    }


    public void resetView(){
        if(viewGroupSensor.getChildCount()== 1) {
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
            mListener = (OnFragmentClickListener)activity;
        }catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_iniciar:
                mListener.onFragmentInteraction(v);
                break;
            default:
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentClickListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(View v);
    }

    private void showMessage(String m, int t){
        Message.show(getActivity(), m, t);
    }

}
