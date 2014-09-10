package cl.tide.hidusb.client;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.util.List;

import cl.tide.hidusb.R;
import cl.tide.hidusb.service.storage.sqlite.AppDataLogger;
import cl.tide.hidusb.service.storage.sqlite.Data;
import cl.tide.hidusb.service.storage.sqlite.Samples;

public class DetailSampleActivity extends FragmentActivity {

    public static String ID_SAMPLE = "cl.tide.hidusb.IDSAMPLE";
    private long id = 0;
    private GoogleMap map;
    private static String TAG = "DETAIL SAMPLES";
    public  Samples sample;
    private TableLayout tbl;
    private TableRow newRow;
    private TextView date, temp, light, hum;
    private XYPlot chartTemp, chartLight, chartHum;
    private SimpleXYSeries tempSerie, lightSerie, humSerie;
    private int colorHum, colortemp, colorlight;
    private AppDataLogger dataLogger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_sample);

        id = getIntent().getExtras().getLong(ID_SAMPLE);

        Log.i(TAG, "Sample id" + id);
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
    }

    private void loadView() {
        double lat = 0;
        double lon = 0;
        try{
            lat = sample.getLatitude();
            lon = sample.getLongitude();
            if(lat!= 0 && lon !=0){
                setMarker(lat,lon);
            }

        }catch (NullPointerException e){
            e.toString();
            Log.i("DetailSample", " Location null " +e);
        }

        try {
            dataLogger = new AppDataLogger(this);
            sample = dataLogger.getSamplesDao().getElementByID(id);

        } catch (AdaFrameworkException e) {
            e.printStackTrace();
            finish();
        }
        //Async view
        new LoaderCharts().execute();
        new LoaderTableLayout().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clear();
    }
    private void clear(){
        dataLogger = null;
        tbl = null;
        chartHum = null;
        chartTemp = null;
        chartLight = null;
        tempSerie = null;
        lightSerie = null;
        humSerie = null;
        newRow = null;
        System.gc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setMarker(double lat, double lon) {
        LatLng location = new LatLng(lat, lon);
        map.addMarker(new MarkerOptions().position(location).title(sample.getDate()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }

    private class LoaderTableLayout extends AsyncTask<Void, TableRow, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            LayoutInflater inflater = (LayoutInflater) getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            tbl = (TableLayout) findViewById(R.id.data_table);
            /*TableLayout header = (TableLayout) findViewById(R.id.header);
            TableRow row = (TableRow) header.getChildAt(0);*/

            List<Data> data = sample.getData();

            for (Data d : data) {
                newRow = (TableRow) inflater.inflate(R.layout.table_row, null);
                date = (TextView) newRow.findViewById(R.id.cell_date_value);
                temp = (TextView) newRow.findViewById(R.id.cell_temp_value);
                light = (TextView) newRow.findViewById(R.id.cell_light_value);
                hum = (TextView) newRow.findViewById(R.id.cell_hum_value);

                date.setText(d.getDatetime().substring(10));
                temp.setText(d.getTemperature() + "");
                light.setText(d.getLight() + "");
                hum.setText(d.getHumidity() + "");

                publishProgress(newRow);
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(TableRow... values) {
            super.onProgressUpdate(values);
            tbl.addView(values[0]);
        }
    }


    private class LoaderCharts extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            configCharts();
            List<Data> data = sample.getData();
            double actualTemp = 0;
            double actualLight = 0;
            double maxTemp = 0;
            double minTemp = 200;
            double minLight = 65800;
            double maxLight = 0;
            Log.i("CHARTVIEW ", " datasource lenght " + data.size());
            for (Data d : data) {
                actualTemp = d.getTemperature();
                actualLight = d.getLight();
                tempSerie.addLast(null, actualTemp);
                lightSerie.addLast(null, actualLight);
                humSerie.addLast(null, d.getHumidity());
                if(actualTemp > maxTemp)
                    maxTemp = actualTemp;
                if(actualTemp < minTemp)
                    minTemp = actualTemp;
                if(actualLight > maxLight)
                    maxLight = actualLight;
                if(actualLight < minLight)
                    minLight = actualLight;
                publishProgress();
            }
            settingYSerie(chartTemp,minTemp,maxTemp);
            settingYSerie(chartLight,minLight,maxLight);
          

            return null;
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            chartTemp.redraw();
            chartLight.redraw();
            chartHum.redraw();
        }
    }
    private void settingYSerie(XYPlot chart, double min, double max){
        double i = (max-min)/5;
        if( i<1)
            i = 1;
        chart.setRangeBoundaries((min-1),(max+1),BoundaryMode.FIXED);
        chart.setRangeStep(XYStepMode.INCREMENT_BY_VAL, i);
    }

    private void configCharts() {

        chartTemp = (XYPlot) findViewById(R.id.chartTemp);
        chartHum = (XYPlot) findViewById(R.id.chartHum);
        chartLight = (XYPlot) findViewById(R.id.chartLight);
        final Resources resources = getResources();
        colorHum = resources.getColor(R.color.darkblue);
        colorlight = resources.getColor(R.color.darkorange);
        colortemp = resources.getColor(R.color.darkred);

        tempSerie = new SimpleXYSeries(getString(R.string.sensor_name_temp));
        lightSerie = new SimpleXYSeries(getString(R.string.sensor_name_light));
        humSerie = new SimpleXYSeries(getString(R.string.sensor_name_hum));
        tempSerie.useImplicitXVals();
        lightSerie.useImplicitXVals();
        humSerie.useImplicitXVals();

        chartTemp.addSeries(tempSerie, new LineAndPointFormatter(colortemp,
                null, null, null));
        chartLight.addSeries(lightSerie, new LineAndPointFormatter(colorlight,
                null, null, null));
        chartHum.addSeries(humSerie, new LineAndPointFormatter(colorHum,
                null, null, null));

        setPlotSteps();
        //set default color transparent
        chartTemp.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        chartTemp.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
        chartTemp.getGraphWidget().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
        chartTemp.getBackgroundPaint().setColor(Color.TRANSPARENT);
        //grid lines color
        chartTemp.getGraphWidget().getDomainGridLinePaint().setColor(resources.getColor(R.color.red));
        //Color labels x,y
        chartTemp.getGraphWidget().getDomainLabelPaint().setColor(colortemp);
        chartTemp.getGraphWidget().getRangeLabelPaint().setColor(colortemp);
        //Title color
        chartTemp.getTitleWidget().getLabelPaint().setColor(colortemp);
        //remove legend object
        chartTemp.getLayoutManager().remove(chartTemp.getLegendWidget());

        chartTemp.setBorderPaint(null);
        chartTemp.setPlotMargins(0, 0, 0, 0);

        chartLight.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        chartLight.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
        chartLight.getGraphWidget().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
        chartLight.getGraphWidget().getDomainGridLinePaint().setColor(resources.getColor(R.color.orange));
        chartLight.getGraphWidget().getDomainLabelPaint().setColor(colorlight);
        chartLight.getGraphWidget().getRangeLabelPaint().setColor(colorlight);
        chartLight.getBackgroundPaint().setColor(Color.TRANSPARENT);

        chartLight.getLayoutManager().remove(chartLight.getLegendWidget());
        chartLight.setBorderPaint(null);
        chartLight.setPlotMargins(0, 0, 0, 0);

        chartHum.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        chartHum.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
        chartHum.getGraphWidget().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
        chartHum.getGraphWidget().getDomainGridLinePaint().setColor(resources.getColor(R.color.blue));
        chartHum.getGraphWidget().getDomainLabelPaint().setColor(colorHum);

        chartHum.getGraphWidget().getRangeLabelPaint().setColor(colorHum);


        chartHum.getLayoutManager().remove(chartHum.getLegendWidget());

        chartHum.getBackgroundPaint().setColor(Color.TRANSPARENT);
        chartHum.setBorderPaint(null);
        chartHum.setPlotMargins(0, 0, 0, 0);

    }

    private void setPlotSteps() {
        int HISTORY_SIZE = sample.getData().size();
        if(HISTORY_SIZE > 5)
            HISTORY_SIZE = HISTORY_SIZE/5;
        //chartTemp.setRangeBoundaries(-10,150,BoundaryMode.FIXED);
        //chartTemp.setRangeStep(XYStepMode.SUBDIVIDE, 2);
        chartTemp.setDomainStep(XYStepMode.INCREMENT_BY_VAL, HISTORY_SIZE);
        chartTemp.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);

        //chartLight.setRangeBoundaries(0,10000,BoundaryMode.GROW);
        //chartLight.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1000);
        chartLight.setDomainStep(XYStepMode.INCREMENT_BY_VAL, HISTORY_SIZE);
        chartLight.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);

        chartHum.setRangeBoundaries(0, 10, BoundaryMode.FIXED);
        chartHum.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
        chartHum.setDomainStep(XYStepMode.INCREMENT_BY_VAL, HISTORY_SIZE);
        chartHum.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
    }
}
