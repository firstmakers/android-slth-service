package cl.tide.hidusb.client;


import android.content.Context;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import cl.tide.hidusb.R;
import cl.tide.hidusb.service.storage.sqlite.Data;
import cl.tide.hidusb.service.storage.sqlite.Samples;

public class DetailSampleActivity extends FragmentActivity {
    public  static String ID_SAMPLE = "cl.tide.hidusb.IDSAMPLE";
    private long id = 0;
    private  GoogleMap map;
    private static String TAG = "DETAIL SAMPLES";
    public static Samples sample;
    private TableLayout tbl;
    private TableRow newRow;
    private TextView date,temp,light,hum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_sample);

        Log.i(TAG, "Sample id" + id);
        map = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();

    }

    private void loadView(){
        setMarker(sample.getLatitude(), sample.getLongitude());
        //Async view
        new LoaderCharts().execute();
        new LoaderTableLayout().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(sample!=null){
            loadView();
        }
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
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setMarker(double lat,double lon){
        LatLng location = new LatLng(lat,lon);
        map.addMarker(new MarkerOptions().position(location).title(sample.getDate()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
    }

    private class LoaderTableLayout extends AsyncTask <Void,TableRow,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LayoutInflater inflater = (LayoutInflater)getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);

                    tbl  = (TableLayout) findViewById(R.id.data_table);
                    TableLayout header = (TableLayout)findViewById(R.id.header);
                    TableRow row =(TableRow) header.getChildAt(0);

                    List<Data> data = sample.getData();

                    for(Data d: data) {
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
                }

            });

            return null;
        }

        @Override
        protected void onProgressUpdate(TableRow... values) {
            super.onProgressUpdate(values);
            tbl.addView(values[0]);
        }
    }

    private class LoaderCharts extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}
