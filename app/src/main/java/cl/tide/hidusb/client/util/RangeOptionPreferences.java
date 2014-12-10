package cl.tide.hidusb.client.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.edmodo.rangebar.RangeBar;

import cl.tide.hidusb.R;


/**
 * Created by eDelgado on 26-08-14.
 */

public class RangeOptionPreferences extends DialogPreference{

    public static int DEFAULT_MIN_VALUE = 1;
    public static  int DEFAULT_MAX_VALUE = 5;

    public static String KEY_TEMP_MIN = "temp_min_value";
    public static String KEY_TEMP_MAX = "temp_max_value";
    public static String KEY_LIGHT_MIN = "light_min_value";
    public static String KEY_LIGHT_MAX = "light_max_value";
    public static String KEY_HUM_MIN = "hum_min_value";
    public static String KEY_HUM_MAX = "hum_max_value";

    private RangeBar rbTemp;
    private RangeBar rbLight;
    private RangeBar rbHum;

    private TextView tvTempMin;
    private TextView tvTempMax;
    private TextView tvLightMin;
    private TextView tvLightMax;
    private TextView tvHumMin;
    private TextView tvHumMax;

    private int tempMin;
    private int tempMax;

    private int lightMin;
    private int lightMax;

    private int humMin;
    private int humMax;

    SharedPreferences sharedPref;

    ////// Constructors //////////
    public RangeOptionPreferences(Context context){
        this(context,null);
    }

    public RangeOptionPreferences(Context context, AttributeSet attrs) {
        super(context, attrs);

        sharedPref =  PreferenceManager.getDefaultSharedPreferences(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RangeOptionPreferences, 0, 0);

        try
        {
            setTempMin(sharedPref.getInt(KEY_TEMP_MIN, scaleTemp(a.getInteger
                    (R.styleable.RangeOptionPreferences_tempMin, DEFAULT_MIN_VALUE))));
            setTempMax(sharedPref.getInt(KEY_TEMP_MAX, scaleTemp(a.getInteger
                    (R.styleable.RangeOptionPreferences_tempMax, DEFAULT_MAX_VALUE))));
            setLightMin(sharedPref.getInt(KEY_LIGHT_MIN, scaleLight(a.getInteger
                    (R.styleable.RangeOptionPreferences_lightMin, DEFAULT_MIN_VALUE))));
            setLightMax(sharedPref.getInt(KEY_LIGHT_MAX, scaleTemp(a.getInteger
                    (R.styleable.RangeOptionPreferences_lightMax, DEFAULT_MAX_VALUE))));
            setHumMin(sharedPref.getInt(KEY_HUM_MIN, scaleHumidity(a.getInteger
                    (R.styleable.RangeOptionPreferences_humMin, DEFAULT_MIN_VALUE))));
            setHumMax(sharedPref.getInt(KEY_HUM_MAX, scaleHumidity(a.getInteger
                    (R.styleable.RangeOptionPreferences_humMax, DEFAULT_MAX_VALUE))));
        }
        finally
        {
            a.recycle();
        }

        setDialogLayoutResource(R.layout.rangebar_pref);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        tvTempMax = (TextView) view.findViewById(R.id.rangebar_temp_max);
        tvTempMin = (TextView) view.findViewById(R.id.rangebar_temp_min);
        tvLightMax = (TextView) view.findViewById(R.id.rangebar_light_max);
        tvLightMin = (TextView) view.findViewById(R.id.rangebar_light_min);
        tvHumMax = (TextView) view.findViewById(R.id.rangebar_hum_max);
        tvHumMin = (TextView) view.findViewById(R.id.rangebar_hum_min);

        tvHumMax.setText(humMax+ " hs");
        tvHumMin.setText(humMin+ " hs");
        tvTempMin.setText(tempMin+ " ºC");
        tvTempMax.setText(tempMax+ " ºC");
        tvLightMin.setText(lightMin+ " lux");
        tvLightMax.setText(lightMax+ " lux");

        rbTemp = (RangeBar) view.findViewById(R.id.rangebar_temp);
        rbLight = (RangeBar) view.findViewById(R.id.rangebar_light);
        rbHum = (RangeBar) view.findViewById(R.id.rangebar_hum);

        rbTemp.setThumbIndices(getScaleTemp(tempMin), getScaleTemp(tempMax));

        rbLight.setThumbIndices(getScaleLight(lightMin), getScaleLight(lightMax));

        rbHum.setThumbIndices(getScaleHumidity(humMin), getScaleHumidity(humMax));

        rbTemp.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                int r = scaleTemp(rightThumbIndex);
                int l = scaleTemp(leftThumbIndex);
                tvTempMax.setText(r +" ºC");
                tvTempMin.setText(l +" ºC");
                setTempMin(l);
                setTempMax(r);

            }
        });
        rbLight.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                int l = scaleLight(leftThumbIndex);
                int r = scaleLight(rightThumbIndex);
                tvLightMax.setText(r+" lux");
                tvLightMin.setText(l +" lux");
                setLightMax(r);
                setLightMin(l);
            }
        });

        rbHum.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                int l = scaleHumidity(leftThumbIndex);
                int r = scaleHumidity(rightThumbIndex);
                tvHumMax.setText(r+" hs");
                tvHumMin.setText(l+" hs");
                setHumMax(r);
                setHumMin(l);
            }
        });

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult){
            //save
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(KEY_TEMP_MAX, tempMax);
            editor.putInt(KEY_TEMP_MIN, tempMin);
            editor.putInt(KEY_LIGHT_MAX, lightMax);
            editor.putInt(KEY_LIGHT_MIN, lightMin);
            editor.putInt(KEY_HUM_MIN, humMin);
            editor.putInt(KEY_HUM_MAX, humMax);
            editor.commit();
            Log.i("Settings ", "Saving Shared Preferences");
        }
    }

    private int scaleTemp(int range) {
        System.out.print(" scale temp : " + range + " to " + (((range )*10) - 50) );
        return ((range)*10) - 50;
    }

    private int getScaleTemp(int range){
        System.out.print(" scale temp : " + range + " to " + ((range +50)/10) );
        return  ((range +50)/10);
    }
    private int scaleLight(int range){
        return range * 100;
    }

    private int getScaleLight(int range){
        return range / 100;
    }

    private int scaleHumidity(int range){
        return range + 1;
    }
    private int getScaleHumidity(int range){
        return range - 1;
    }



    public int getHumMax() {
        return humMax;
    }

    public void setHumMax(int humMax) {
        this.humMax = humMax;
    }

    public int getHumMin() {
        return humMin;
    }

    public void setHumMin(int humMin) {
        this.humMin = humMin ;
    }

    public int getLightMax() {
        return lightMax;
    }

    public void setLightMax(int lightMax) {
        this.lightMax = lightMax;
    }

    public int getLightMin() {
        return lightMin;
    }

    public void setLightMin(int lightMin) {
        this.lightMin = lightMin;
    }

    public int getTempMax() {
        return tempMax;
    }

    public void setTempMax(int tempMax) {
        this.tempMax = tempMax;
    }

    public int getTempMin() {
        return tempMin;
    }

    public void setTempMin(int tempMin) {
        this.tempMin = tempMin;
    }


}
