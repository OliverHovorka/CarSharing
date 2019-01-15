package at.ac.htlstp.carsharing.app.carsharingapp;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CarCurAdapter extends ArrayAdapter<CarCurrent> {

    public CarCurAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (position % 2 == 1) {
            view.setBackgroundColor(Color.parseColor("#009EE0"));
        } else {
            view.setBackgroundColor(Color.parseColor("#95dff9"));
        }
        return view;
    }
}
