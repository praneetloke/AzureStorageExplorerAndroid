package com.centricconsulting.azurestorageexplorer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/10/2016.
 */
public class SpinnerArrayAdapter<T> extends ArrayAdapter<T> implements Serializable {
    private ArrayList<T> dataset;

    public SpinnerArrayAdapter(Context context, ArrayList<T> dataset) {
        super(context, android.R.layout.simple_spinner_item, dataset);
        this.dataset = dataset;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    protected ArrayList<T> getDataset() {
        return dataset;
    }

    public void replaceDataset(ArrayList<T> newDataset) {
        this.dataset.clear();
        this.dataset.addAll(newDataset);
        notifyDataSetChanged();
    }
}
