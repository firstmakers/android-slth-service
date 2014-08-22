package cl.tide.hidusb.client.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import cl.tide.hidusb.R;


/**
 * Created by Edison Delgado on 06-05-2014.
 */
public class CustomMessage extends Toast {

    public final static int ERROR = 0;
    public final static int WARNING = 1;
    public final static int MESSAGE = 2;


    public CustomMessage(Context context, String message , int duration, int type) {
        super(context);
        this.setDuration(duration);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = null;
        if(type == ERROR){
            view = inflater.inflate(R.layout.toast_error, (ViewGroup) ((Activity) context).findViewById(R.id.toast_error));
        }
        else if( type == WARNING){
            view = inflater.inflate(R.layout.toast_warning, (ViewGroup) ((Activity) context).findViewById(R.id.toast_warning));
        }
        else if( type == MESSAGE){
            view = inflater.inflate(R.layout.toast_message, (ViewGroup) ((Activity) context).findViewById(R.id.toast_message));
        }

        this.setView(view);
        TextView tv = (TextView) view.findViewById(R.id.tv_message);
        tv.setText(message);
        //ancho de la actionbar
        int Y = context.getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height);
        //alineado arriba debajo de la actionbar
        this.setGravity(Gravity.TOP| Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK,0,Y);
    }
}


