package com.centricconsulting.azurestorageexplorer.arrayadapters;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public abstract class LinearRecyclerViewAdapter<T> extends RecyclerView.Adapter<LinearRecyclerViewAdapter.ViewHolder> {
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(TextView itemView) {
            super(itemView);
            mTextView = itemView;
        }
    }
}
