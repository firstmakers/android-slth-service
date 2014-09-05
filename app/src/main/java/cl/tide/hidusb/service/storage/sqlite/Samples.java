package cl.tide.hidusb.service.storage.sqlite;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by eDelgado on 11-08-14.
 */

@Table(name = "samples")
public class Samples extends Entity {



    @TableField(name = "date", datatype = DATATYPE_STRING ,required = true)
    private String date;

    @TableField(name = "type", datatype = DATATYPE_STRING ,required = true)
    private String type;

    @TableField(name = "number", datatype = DATATYPE_INTEGER )
    private int number;

    @TableField(name = "interval", datatype = DATATYPE_INTEGER )
    private int interval;

    @TableField(name = "latitude", datatype = DATATYPE_DOUBLE )
    private double latitude;

    @TableField(name = "longitude", datatype = DATATYPE_DOUBLE)
    private double longitude;

    @TableField(name = "datos",datatype = DATATYPE_ENTITY)
    private List<Data>  data;

    /**
    * *Constructors
    * */
    public Samples(){
        date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        data = new ArrayList<Data>();

    }
    public Samples(int interval, int samples){
        date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        this.interval = interval;
        this.number = samples;
        data = new ArrayList<Data>();
    }

    /**
     * Getter and Setter
     */


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }
}
