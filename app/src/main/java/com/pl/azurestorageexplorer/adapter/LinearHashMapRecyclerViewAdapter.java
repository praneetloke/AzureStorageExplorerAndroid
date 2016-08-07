package com.pl.azurestorageexplorer.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.HashMap;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public abstract class LinearHashMapRecyclerViewAdapter<K, V, T2 extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T2> {
    private K[] dataset;
    private HashMap<K, V> values;

    public LinearHashMapRecyclerViewAdapter(HashMap<K, V> dataset) {
        this.dataset = (K[]) dataset.keySet().toArray();
        this.values = dataset;
    }

    @Override
    public int getItemCount() {
        return dataset == null ? 0 : dataset.length;
    }

    public void replaceDataset(HashMap<K, V> newDataset) {
        this.dataset = (K[]) newDataset.keySet().toArray();
        this.values = newDataset;
        notifyDataSetChanged();
    }

    public K getKey(int position) {
        return this.dataset[position];
    }

    public V getValue(String key) {
        if (key == null || this.values == null || !this.values.containsKey(key)) {
            return null;
        }

        return this.values.get(key);
    }
}
