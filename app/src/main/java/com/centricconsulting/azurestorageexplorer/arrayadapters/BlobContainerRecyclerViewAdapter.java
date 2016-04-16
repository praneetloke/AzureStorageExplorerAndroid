package com.centricconsulting.azurestorageexplorer.arrayadapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.azure.storage.blob.CloudBlobContainer;

import java.util.ArrayList;

/**
 * Created by v-prloke on 4/16/2016.
 */
public class BlobContainerRecyclerViewAdapter extends LinearRecyclerViewAdapter<CloudBlobContainer> {

    public BlobContainerRecyclerViewAdapter(ArrayList<CloudBlobContainer> dataset) {
        super(dataset);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        //TODO: set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CloudBlobContainer container = getDataset().get(position);
        holder.mTextView.setText(container.getName());
    }
}
