package cl.tide.hidusb.client.fragments;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYPlotZoomPan;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;

import java.util.List;

import cl.tide.hidusb.R;
import cl.tide.hidusb.client.BaseActivity;
import cl.tide.hidusb.client.HomeActivity;
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

    private XYPlot chart;
    private SimpleXYSeries temp , light ,hum;

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
        chart = (XYPlot) v.findViewById(R.id.xyplot);
        temp = new SimpleXYSeries(getString(R.string.sensor_name_temp));
        light = new SimpleXYSeries(getString(R.string.sensor_name_light));
        hum = new SimpleXYSeries(getString(R.string.sensor_name_hum));
        final Resources resources = getResources();
        colorHum = resources.getColor(R.color.darkblue);
        colorlight = resources.getColor(R.color.darkorange);
        colortemp = resources.getColor(R.color.darkred);
        setChartConfiguration();
        populateChart();
        return v;
    }
    private void setChartConfiguration(){
        temp.useImplicitXVals();
        light.useImplicitXVals();
        hum.useImplicitXVals();
        chart.addSeries(temp, new LineAndPointFormatter(colortemp,
                Color.BLACK, null, new PointLabelFormatter(colortemp)));
        chart.addSeries(light ,new LineAndPointFormatter(colorlight,
                Color.BLACK, null, new PointLabelFormatter(colorlight)));
        chart.addSeries(hum ,new LineAndPointFormatter(colorHum,
                Color.BLACK, null, new PointLabelFormatter(colorHum)));

        chart.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        chart.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        chart.getGraphWidget().getRangeGridLinePaint().setColor(Color.BLUE);

        chart.getGraphWidget().setSize(new SizeMetrics(
                0, SizeLayoutType.FILL,
                0, SizeLayoutType.FILL));

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((BaseActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void setSample(Samples s){
        //this.samples = s;
        this.dataSource = s.getData();
    }

    private void populateChart(){
        Log.i("CHARTVIEW ", " datasource lenght " +dataSource.size());
        for(Data d: dataSource){

            temp.addLast( null, d.getTemperature());
            light.addLast(null, d.getLight());
            hum.addLast(null, d.getHumidity());
        }
        chart.redraw();
    }
    public void updateChart(double t, double l, int h){

        temp.addLast(null,t);
        light.addLast(null,l);
        hum.addLast(null, h);
        chart.redraw();
    }


}
