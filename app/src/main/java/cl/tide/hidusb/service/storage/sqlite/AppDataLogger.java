package cl.tide.hidusb.service.storage.sqlite;

import android.content.Context;

import com.mobandme.ada.ObjectContext;
import com.mobandme.ada.ObjectSet;
import com.mobandme.ada.exceptions.AdaFrameworkException;

/**
 * Created by eDelgado on 11-08-14.
 */
public class AppDataLogger extends ObjectContext {

    public static String DATABASE_NAME = "tideslth";
    public static int DATABASE_VERSION = 1;

    public ObjectSet<Samples> samplesDao;
    public ObjectSet<Data> dataDao;


    public AppDataLogger(Context pContext) throws AdaFrameworkException{
        super(pContext, DATABASE_NAME, DATABASE_VERSION);
        initialize();
    }

    public AppDataLogger(Context pContext, String pDatabaseName) throws  AdaFrameworkException{
        super(pContext, pDatabaseName);
        initialize();
    }

    public AppDataLogger(Context pContext, String pDatabaseName, int pDatabaseVersion) throws  AdaFrameworkException{
        super(pContext, pDatabaseName, pDatabaseVersion);
        initialize();
    }


    // inicializa la persistencia de datos
    private void initialize() throws AdaFrameworkException{
        if(samplesDao == null)
            this.samplesDao = new ObjectSet<Samples>(Samples.class, this);
        if(this.dataDao == null)
            this.dataDao = new ObjectSet<Data>(Data.class , this);
    }

    public ObjectSet<Data> getDataDao() {
        return dataDao;
    }

    public void setDataDao(ObjectSet<Data> dataDao) {
        this.dataDao = dataDao;
    }

    public ObjectSet<Samples> getSamplesDao() {
        return samplesDao;
    }

    public void setSamplesDao(ObjectSet<Samples> samplesDao) {
        this.samplesDao = samplesDao;
    }
}
