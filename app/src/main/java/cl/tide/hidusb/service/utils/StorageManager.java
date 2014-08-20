package cl.tide.hidusb.service.utils;

import android.content.Context;

import cl.tide.hidusb.service.storage.sqlite.AppDataLogger;

/**
 * Created by eDelgado on 20-08-14.
 */
public class StorageManager {
    private AppDataLogger dataLogger;

    public StorageManager(Context context){

        try {
            this.dataLogger = new AppDataLogger(context);
        }catch (Exception e){

        }
    }
}
