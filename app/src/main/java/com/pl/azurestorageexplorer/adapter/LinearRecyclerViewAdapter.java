package com.pl.azurestorageexplorer.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public abstract class LinearRecyclerViewAdapter<T, T2 extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T2> {
    private ArrayList<T> dataset;

    public LinearRecyclerViewAdapter(ArrayList<T> dataset) {
        this.dataset = dataset;
    }

    @Override
    public int getItemCount() {
        return dataset == null ? 0 : dataset.size();
    }

    public void replaceDataset(ArrayList<T> newDataset) {
        this.dataset = newDataset;
        notifyDataSetChanged();
    }

    public ArrayList<T> getDataset() {
        return dataset;
    }

    public void destroy() {
        dataset.clear();
        dataset = null;
    }
}
