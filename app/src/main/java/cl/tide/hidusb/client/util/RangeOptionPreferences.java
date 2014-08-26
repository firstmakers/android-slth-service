package cl.tide.hidusb.client.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.edmodo.rangebar.RangeBar;

import cl.tide.hidusb.R;


/**
 * Created by eDelgado on 26-08-14.
 */

public class RangeOptionPreferences extends DialogPreference {

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

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RangeOptionPreferences, 0, 0);

        try
        {
            setTempMin(scaleRangeTemp(a.getInteger
                    (R.styleable.RangeOptionPreferences_tempMin, DEFAULT_MIN_VALUE)));
            setTempMax(scaleRangeTemp(a.getInteger
                    (R.styleable.RangeOptionPreferences_tempMax, DEFAULT_MAX_VALUE)));
            setLightMin(a.getInteger(R.styleable.RangeOptionPreferences_lightMin, DEFAULT_MIN_VALUE));
            setLightMax(a.getInteger(R.styleable.RangeOptionPreferences_lightMax, DEFAULT_MAX_VALUE));
            setHumMin(a.getInteger(R.styleable.RangeOptionPreferences_humMin, DEFAULT_MIN_VALUE));
            setHumMax(a.getInteger(R.styleable.RangeOptionPreferences_humMax, DEFAULT_MAX_VALUE));
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

        tvHumMax.setText(humMax+"");
        tvHumMin.setText(humMin+"");
        tvTempMin.setText(tempMin+"");
        tvTempMax.setText(tempMax+"");
        tvLightMin.setText(lightMin+"");
        tvLightMax.setText(lightMax+"");

        rbTemp = (RangeBar) view.findViewById(R.id.rangebar_temp);
        rbLight = (RangeBar) view.findViewById(R.id.rangebar_light);
        rbHum = (RangeBar) view.findViewById(R.id.rangebar_hum);

        rbTemp.setThumbIndices(getScaleRangeTemp(tempMin), getScaleRangeTemp(tempMax));

        rbLight.setThumbIndices(lightMin/100, lightMax/100);

        rbHum.setThumbIndices(humMin, humMax);

        rbTemp.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                tvTempMax.setText(scaleRangeTemp(rightThumbIndex)+"");
                tvTempMin.setText(scaleRangeTemp(leftThumbIndex) +"");
            }
        });
        rbLight.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                tvLightMax.setText(rightThumbIndex * 100 +"");
                tvLightMin.setText(leftThumbIndex * 100 +"");
            }
        });

        rbHum.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                tvHumMax.setText(rightThumbIndex+"");
                tvHumMin.setText(leftThumbIndex+"");
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
        }
    }

    private int scaleRangeTemp(int range) {
        System.out.print(" scale temp : " + range + " to " + (((range )*10) - 50) );
        return ((range)*10) - 50;
    }

    private int getScaleRangeTemp(int range){
        System.out.print(" scale temp : " + range + " to " + ((range +50)/10) );
        return  ((range +50)/10);
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
        this.lightMax = lightMax * 100;
    }

    public int getLightMin() {
        return lightMin;
    }

    public void setLightMin(int lightMin) {
        this.lightMin = lightMin * 100;
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
