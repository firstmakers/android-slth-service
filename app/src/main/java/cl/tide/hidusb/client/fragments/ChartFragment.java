package cl.tide.hidusb.client.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import java.util.ArrayList;
import java.util.List;
import cl.tide.hidusb.R;
import cl.tide.hidusb.client.BaseActivity;
import cl.tide.hidusb.service.storage.sqlite.Data;
import cl.tide.hidusb.service.storage.sqlite.Samples;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ChartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";

    private XYPlot chartTemp , chartLight, chartHum;
    private SimpleXYSeries temp , light ,hum;
    Typeface roboto_regular;
    Typeface roboto_thin;
    Typeface bariol;

    private static int  HISTORY_SIZE = 300;

    //private  Samples samples;
    private List<Data> dataSource;
    int colortemp ;
    int colorlight;
    int colorHum;
 int count = 0;
    // TODO: Rename and change types of parameters
    private int mParam1;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartFragment newInstance(int param1) {

        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, param1);

        fragment.setArguments(args);
        return fragment;
    }
    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chart, container, false);
        //chart config
        chartTemp = (XYPlot) v.findViewById(R.id.xyplot_temp);
        chartLight = (XYPlot) v.findViewById(R.id.xyplot_light);
        chartHum = (XYPlot) v.findViewById(R.id.xyplot_hum);


        temp = new SimpleXYSeries(getString(R.string.sensor_name_temp));
        light = new SimpleXYSeries(getString(R.string.sensor_name_light));
        hum = new SimpleXYSeries(getString(R.string.sensor_name_hum));

        final Resources resources = getResources();

        bariol = Typeface.createFromAsset(resources.getAssets(), "fonts/Bariol_Regular.otf");

        colorHum = resources.getColor(R.color.darkblue);
        colorlight = resources.getColor(R.color.darkorange);
        colortemp = resources.getColor(R.color.darkred);
        setChartConfiguration(resources);
        populateChart();

        return v;
    }
    private void setChartConfiguration(Resources resources){
        temp.useImplicitXVals();
        light.useImplicitXVals();
        hum.useImplicitXVals();
        chartTemp.addSeries(temp, new LineAndPointFormatter(colortemp,
                null, null, null));
        chartLight.addSeries(light, new LineAndPointFormatter(colorlight,
                null, null, null));
        chartHum.addSeries(hum, new LineAndPointFormatter(colorHum,
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
        //chartTemp.getTitleWidget().getLabelPaint().setTypeface(bariol);
        //chartTemp.getTitleWidget().getLabelPaint().setFakeBoldText(true);
        //delete border background
        chartTemp.setBorderPaint(null);
        chartTemp.setPlotMargins(0,0,0,0);

        chartLight.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        chartLight.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
        chartLight.getGraphWidget().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
        chartLight.getGraphWidget().getDomainGridLinePaint().setColor(resources.getColor(R.color.orange));
        chartLight.getGraphWidget().getDomainLabelPaint().setColor(colorlight);
        chartLight.getGraphWidget().getRangeLabelPaint().setColor(colorlight);
        chartLight.getBackgroundPaint().setColor(Color.TRANSPARENT);
        //Title color
        //chartLight.getTitleWidget().getLabelPaint().setColor(colorlight);
        //chartLight.getTitleWidget().getLabelPaint().setTypeface(bariol);
        //chartLight.getTitleWidget().getLabelPaint().setFakeBoldText(true);
        //remove legend object
        chartLight.getLayoutManager().remove(chartLight.getLegendWidget());
        chartLight.setBorderPaint(null);
        chartLight.setPlotMargins(0,0,0,0);

        chartHum.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        chartHum.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
        chartHum.getGraphWidget().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
        chartHum.getGraphWidget().getDomainGridLinePaint().setColor(resources.getColor(R.color.blue));
        chartHum.getGraphWidget().getDomainLabelPaint().setColor(colorHum);

        chartHum.getGraphWidget().getRangeLabelPaint().setColor(colorHum);

        //Title color
        //chartHum.getTitleWidget().getLabelPaint().setColor(colorHum);

        //chartHum.getTitleWidget().getLabelPaint().setTypeface(bariol);
        //chartHum.getTitleWidget().getLabelPaint().setFakeBoldText(true);

        //remove legend object

        chartHum.getLayoutManager().remove(chartHum.getLegendWidget());

        chartHum.getBackgroundPaint().setColor(Color.TRANSPARENT);
        chartHum.setBorderPaint(null);
        chartHum.setPlotMargins(0,0,0,0);

        /*chart.getGraphWidget().setSize(new SizeMetrics(
                0, SizeLayoutType.FILL,
                0, SizeLayoutType.FILL));*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((BaseActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
    private void setPlotSteps(){
        //chartTemp.setRangeBoundaries(-10,150,BoundaryMode.FIXED);
        //chartTemp.setRangeStep(XYStepMode.SUBDIVIDE, 2);
        chartTemp.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 40);
        chartTemp.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);

        //chartLight.setRangeBoundaries(0,10000,BoundaryMode.GROW);
        //chartLight.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1000);
        chartLight.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 40);
        chartLight.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);

        chartHum.setRangeBoundaries(0, 10, BoundaryMode.FIXED);
        chartHum.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
        chartHum.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 40);
        chartHum.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
    }

    public void setSample(Samples s){
        //this.samples = s;
        if(s!=null)
            this.dataSource = s.getData();
        else
            this.dataSource = new ArrayList<Data>();
    }

    private void populateChart(){
        Log.i("CHARTVIEW ", " datasource lenght " + dataSource.size());
        for(Data d: dataSource){

            temp.addLast( null, d.getTemperature());
            light.addLast(null, d.getLight());
            hum.addLast(null, d.getHumidity());
        }
        chartTemp.redraw();
        chartLight.redraw();
        chartHum.redraw();
    }
    public void updateChart(double t, double l, int h){

        temp.addLast(null,t);
        light.addLast(null,l);
        hum.addLast(null, h);

        if(temp.size() > HISTORY_SIZE){
            temp.removeFirst();
            light.removeFirst();
            hum.removeFirst();
        }
        chartTemp.redraw();
        chartLight.redraw();
        chartHum.redraw();
    }


}
