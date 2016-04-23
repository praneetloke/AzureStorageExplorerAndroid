package com.centricconsulting.azurestorageexplorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.centricconsulting.azurestorageexplorer.R;
import com.centricconsulting.azurestorageexplorer.adapter.interfaces.IViewHolderClickListener;

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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        protected TextView mText1;
        protected ImageView mImageView;
        protected IViewHolderClickListener mListener;

        public ViewHolder(View itemView, IViewHolderClickListener viewHolderClickListener) {
            super(itemView);
            mText1 = (TextView) itemView.findViewById(R.id.text1);
            mImageView = (ImageView) itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(this);
            mListener = viewHolderClickListener;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(getAdapterPosition());
        }
    }
}
