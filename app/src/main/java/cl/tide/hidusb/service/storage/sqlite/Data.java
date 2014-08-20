package cl.tide.hidusb.service.storage.sqlite;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.util.Date;

/**
 * Created by eDelgado on 11-08-14.
 */
@Table(name = "data")
public class Data extends Entity {

    @TableField(name = "temperature", datatype = DATATYPE_DOUBLE)
    private double temperature;
    @TableField(name = "light", datatype = DATATYPE_DOUBLE)
    private double light;
    @TableField(name = "humidity", datatype = DATATYPE_DOUBLE)
    private double humidity;
    @TableField(name = "date", datatype = DATATYPE_STRING)
    private String date;
    @TableField(name = "id_sample", datatype = DATATYPE_ENTITY_LINK)
    private Samples sample;


    public Data(double t, double l,double h){
        this.temperature = t;
        this.light = l;
        this.humidity = h;
    }

    public Data (){
    }
}
