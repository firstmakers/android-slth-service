package cl.tide.hidusb.service.storage.sqlite;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by eDelgado on 11-08-14.
 */
@Table(name = "data")
public class Data extends Entity {

    @TableField(name = "datetime", datatype = DATATYPE_STRING, required = true)
    private String datetime;

    @TableField(name = "temperature", datatype = DATATYPE_DOUBLE)
    private double temperature;

    @TableField(name = "light", datatype = DATATYPE_DOUBLE)
    private double light;

    @TableField(name = "humidity", datatype = DATATYPE_DOUBLE)
    private double humidity;




    public Data(double t, double l,double h){
        this.datetime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        this.temperature = t;
        this.light = l;
        this.humidity = h;
    }

    public Data (){
    }


    public void setDate(String date) {
        this.datetime = date;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getLight() {
        return light;
    }

    public void setLight(double light) {
        this.light = light;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
}
