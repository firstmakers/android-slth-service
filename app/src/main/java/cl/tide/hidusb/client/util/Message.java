package cl.tide.hidusb.client.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Edison Delgado on 06-05-2014.
 */
public class Message {

    public static void show(Context activityContext, String message, int type) {
        CustomMessage toast = new CustomMessage(activityContext, message, Toast.LENGTH_LONG, type);
        toast.show();
    }
}
