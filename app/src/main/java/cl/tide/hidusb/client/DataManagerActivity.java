package cl.tide.hidusb.client;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import cl.tide.hidusb.R;
import cl.tide.hidusb.client.adapter.GridViewFileAdapter;
import cl.tide.hidusb.service.storage.sqlite.Samples;
import cl.tide.hidusb.service.utils.StorageManager;

/**
 * Created by eDelgado on 02-09-14.
 */
public class DataManagerActivity extends BaseActivity {

    private List<Samples> dataBase;
    private List<String> data;
    GridViewFileAdapter adapter;
    private GridView gridView;
    public String email = "";
    private TextView txt;
    private static String TAG = "DATA_MANAGER";
    public static String DATACONTEXT ="cl.tide.hidusb.parcelable.data";
    private StorageManager dataLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_datamanager);

        data = new ArrayList<String>();

        gridView = (GridView) findViewById(R.id.file_grid);
        txt = (TextView) findViewById(R.id.text_file);
        txt.setVisibility(View.GONE);
        adapter = new GridViewFileAdapter(this, R.layout.file_layout, data);
        gridView.setAdapter(adapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new MultiChoiceModeListener());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadSample(position);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataLogger = new StorageManager(this);
        new DataLoader().execute();
    }

    /***/
    private void loadSample(int position){

        Intent intent = new Intent(this, DetailSampleActivity.class);
        intent.putExtra(DetailSampleActivity.ID_SAMPLE, dataBase.get(position).getID());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void LoadData() {
        if (dataLogger != null) {
            dataBase = dataLogger.getAllSamples();
            for (Samples sample : dataBase) {
                data.add(sample.getDate());
            }
            Log.i("DATA MANAGER", "database size " + dataBase.size());

        } else {
            dataBase = new ArrayList<Samples>();
            Log.i("DATA MANAGER", "database size 0");
        }
    }

    private void populateGridView() {
        if (dataBase.size() == 0) {
            txt.setVisibility(View.VISIBLE);
        } else {
            adapter.notifyDataSetChanged();
            Log.i("DATA MANAGER", "Notify DataSetChanged ");
        }
    }

    public class DataLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setContentView(R.layout.activity_loading);
        }

        @Override
        protected Void doInBackground(Void... params) {
            long init = System.currentTimeMillis();
            Log.i("DATA MANAGER", "Loading data from service");
            LoadData();
            Log.i("DATA MANAGER", "Executed in " + ((System.currentTimeMillis() - init)) + " MilliSeconds");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            populateGridView();
        }
    }

    public class MultiChoiceModeListener implements
            GridView.MultiChoiceModeListener {


        @Override
        public void onItemCheckedStateChanged(android.view.ActionMode mode, int i, long l, boolean b) {
               int selectCount = gridView.getCheckedItemCount();
                Log.d("ActionMenu", " count " + selectCount +" position " + i);
                dataBase.get(i).setSelected(b);
                Log.d("ActionMenu", " selected " + dataBase.get(i).getID());
                switch (selectCount) {
                    case 1:
                        mode.setSubtitle(R.string.item_selected);
                        break;
                    default:
                        String string = getString(R.string.many_items_selected);
                        mode.setSubtitle("" + selectCount + " "+ string);
                        break;
                }
        }

        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            mode.setTitle(R.string.file_selected);
            mode.setSubtitle(R.string.item_selected);
            mode.getMenuInflater().inflate(R.menu.files, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            int id = item.getItemId();

                 switch (id) {
                    case R.id.action_delete: {
                        List<Samples> deleteItems = new ArrayList<Samples>();
                        for(Samples file : dataBase){
                            if(file.isSelected()){
                                deleteItems.add(file);

                            }
                        }
                        for(Samples delete :deleteItems){
                            dataLogger.delete(delete);
                            data.remove(delete.getDate());
                            Log.i(TAG,"delete "+ delete.getID());
                        }
                        adapter.notifyDataSetChanged();
                        if(data.size() == 0){
                            txt.setVisibility(View.VISIBLE);
                        }
                        //update new database items
                        dataBase = dataLogger.getAllSamples();
                        mode.finish();
                        break;
                    }
                     /*case R.id.action_share: {

                        final Intent ei = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        ei.setType("plain/text");
                        ei.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
                        ei.putExtra(Intent.EXTRA_SUBJECT, "Reporte SLTH");

                        ArrayList<Uri> uris = new ArrayList<Uri>();

                        for(int i = 0; i< myList.size(); i++){
                            if(myList.get(i).isSelected()) {
                                String root_sd = Environment.getExternalStorageDirectory().toString();
                                file = new File(root_sd + ExportToExcel.ROOT_DIRECTORY);

                                Uri u = Uri.parse(file.toURI()+ myList.get(i).getFile());
                                Log.e("Adjuntos ", u.toString());
                                uris.add(u);
                            }
                        }
                        String title = getString(R.string.title_share_mail);
                        ei.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                        startActivityForResult(Intent.createChooser(ei, title), 12345);

                        break;
                    }*/
                    default:
                        return false;
                }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

    }

}
