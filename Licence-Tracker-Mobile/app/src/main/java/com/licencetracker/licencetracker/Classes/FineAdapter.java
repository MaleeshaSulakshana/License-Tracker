package com.licencetracker.licencetracker.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.licencetracker.licencetracker.R;

import java.util.ArrayList;

public class FineAdapter extends ArrayAdapter<Fine> {

    private Context mContext;
    private int mResource;

    public FineAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Fine> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView location = (TextView) convertView.findViewById(R.id.location);
        TextView amount = (TextView) convertView.findViewById(R.id.amount);

        date.setText(getItem(position).getDate_time());
        location.setText(getItem(position).getLocation());
        amount.setText("Rs." + getItem(position).getAmount());

        return convertView;
    }

}