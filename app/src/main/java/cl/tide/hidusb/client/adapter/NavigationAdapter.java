package cl.tide.hidusb.client.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import cl.tide.hidusb.R;
import cl.tide.hidusb.client.model.DrawerItems;


/**
 * Created by eDelgado on 17-06-14.
 */
public class NavigationAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<DrawerItems> arrayItems;

    public NavigationAdapter(Activity a, ArrayList<DrawerItems> items){
        this.activity = a;
        this.arrayItems = items;
    }

    @Override
    public int getCount() {
        return arrayItems.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item view;
        LayoutInflater inflator = activity.getLayoutInflater();
        if(convertView == null){
            view = new Item();

            DrawerItems itm = arrayItems.get(position);
            convertView = inflator.inflate(R.layout.drawer_item, null);
            view.titulo = (TextView)convertView.findViewById(R.id.title);
            view.icono = (ImageView) convertView.findViewById(R.id.icon);
            view.titulo.setText(itm.getTitulo());
            view.icono.setImageResource(itm.getIcono());

            convertView.setTag(view);
        }else{
            view = (Item) convertView.getTag();
        }
        return convertView;
    }

    public static class Item{
        TextView titulo;
        ImageView icono;
    }
}
