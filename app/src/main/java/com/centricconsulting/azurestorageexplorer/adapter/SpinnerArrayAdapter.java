package com.centricconsulting.azurestorageexplorer.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/10/2016.
 */
public class SpinnerArrayAdapter<T> extends ArrayAdapter<T> implements Serializable {
    private ArrayList<T> dataset;

    public SpinnerArrayAdapter(Context context, int layoutResId, ArrayList<T> dataset) {
        super(context, layoutResId, dataset);
        this.dataset = dataset;
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
