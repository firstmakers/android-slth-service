package cl.tide.hidusb.service.utils;

import android.content.Context;
import android.util.Log;

import com.mobandme.ada.Entity;
import com.mobandme.ada.ObjectSet;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.io.File;

import cl.tide.hidusb.service.storage.sqlite.AppDataLogger;
import cl.tide.hidusb.service.storage.sqlite.Data;
import cl.tide.hidusb.service.storage.sqlite.Samples;

/**
 * Created by eDelgado on 20-08-14.
 */
public class StorageManager {
    private final static String TAG = "STORAGE_MANAGER";
    private AppDataLogger dataLogger;
    private Samples mSample;
    private Data mData;


    /**Constructor**/
    public StorageManager(Context context){

        try {
            this.dataLogger = new AppDataLogger(context);
            File dbFile = context.getDatabasePath(AppDataLogger.DATABASE_NAME);
            Log.i(TAG, "DB PATH : "+dbFile.getAbsolutePath());
        }catch (Exception e){
            Log.e(TAG, "Creating persintence object : " + e.toString());
        }
    }

    /** Save a new Sample */
    public void saveSample(){
        if(mSample != null) {
            try {
                dataLogger.samplesDao.add(mSample);
                dataLogger.samplesDao.save();
            } catch (AdaFrameworkException e) {
                Log.e(TAG,"Error on saving sample");
                e.printStackTrace();
            }
        }
    }

    /**Create a new object of Samples*/
    public void createSample(int i, int m){
        mSample = new Samples(i, m);
        mSample.setType("0x87");
        mSample.setStatus(Entity.STATUS_NEW);

        dataLogger.samplesDao.add(mSample);
        try {
            dataLogger.samplesDao.save();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }

    /****/
    public long getSampleID(){
        return mSample.getID();
    }

    /****/
    public void saveData(double t, double l ,int h){
        Data d = new Data(t,l,h);
        d.setStatus(Entity.STATUS_NEW);
        dataLogger.dataDao.add(d);
        //Log.i(TAG, "Actual Sample " + mSample.getID() + " created at "+ mSample.getDate());

        mSample.setData(dataLogger.dataDao);
        mSample.setStatus(Entity.STATUS_UPDATED);

        try {
            dataLogger.samplesDao.save();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }

    public ObjectSet<Samples> getAllSamples(){

        try {
            dataLogger.samplesDao.fill("date");
            return dataLogger.getSamplesDao();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void printData(){
        try {
            dataLogger.printDB();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }
    public Samples getLastSample(){
        return mSample;
    }

}
