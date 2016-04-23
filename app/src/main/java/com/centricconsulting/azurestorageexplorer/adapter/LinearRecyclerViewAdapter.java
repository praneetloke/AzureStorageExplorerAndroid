package com.centricconsulting.azurestorageexplorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        protected LinearLayout mLayout2;
        protected IViewHolderClickListener mListener;

        public ViewHolder(View itemView, IViewHolderClickListener viewHolderClickListener) {
            super(itemView);
            mListener = viewHolderClickListener;
            LinearLayout layout1 = (LinearLayout) itemView.findViewById(R.id.layout1);
            mText1 = (TextView) layout1.findViewById(R.id.text1);
            mImageView = (ImageView) layout1.findViewById(R.id.icon);
            layout1.setOnClickListener(this);

            mLayout2 = (LinearLayout) itemView.findViewById(R.id.layout2);
            //set the click listener for the "info" layout
            mLayout2.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v.getId(), getAdapterPosition());
        }
    }
}
