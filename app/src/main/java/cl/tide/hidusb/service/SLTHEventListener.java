package cl.tide.hidusb.service;

/**
 * Created by eDelgado on 19-08-14.
 */
public interface SLTHEventListener {

    //public void OnNewTemperatureSample(double temperature);
    //public void OnNewLightSample(double light);
    //public void OnNewHumiditySample(int humidity);
    public void OnSensorDetached();
    public void OnNewSample(double temperature, double light, int humidity);
}
