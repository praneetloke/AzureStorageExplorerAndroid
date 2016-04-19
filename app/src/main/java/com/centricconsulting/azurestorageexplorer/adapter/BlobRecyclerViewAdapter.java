package com.centricconsulting.azurestorageexplorer.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.centricconsulting.azurestorageexplorer.R;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class BlobRecyclerViewAdapter extends LinearRecyclerViewAdapter<ListBlobItem> {

    public BlobRecyclerViewAdapter(ArrayList<ListBlobItem> dataset) {
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
        ListBlobItem blobItem = getDataset().get(position);
        try {
            boolean isFolder = !(blobItem instanceof CloudBlob);
            String name = (!isFolder) ? ((CloudBlob) blobItem).getName() : ((CloudBlobDirectory) blobItem).getPrefix();
            holder.mTextView.setText(name);
            if (isFolder) {
                holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_folder, 0, 0, 0);
                holder.mTextView.setCompoundDrawablePadding(5);
            }
        } catch (URISyntaxException e) {
            holder.mTextView.setText("Something went wrong :(");
        }
    }
}
