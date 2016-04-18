package com.centricconsulting.azurestorageexplorer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/10/2016.
 */
public class SpinnerArrayAdapter<T> extends ArrayAdapter<T> {
    private ArrayList<T> dataset;
    private LayoutInflater layoutInflater;

    public SpinnerArrayAdapter(Context context, ArrayList<T> dataset) {
        super(context, android.R.layout.simple_spinner_item, dataset);
        this.dataset = dataset;

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    protected LayoutInflater getLayoutInflater() {
        return layoutInflater;
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
