package com.centricconsulting.azurestorageexplorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.centricconsulting.azurestorageexplorer.R;
import com.centricconsulting.azurestorageexplorer.util.Helpers;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.blob_item_layout, parent, false);
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
            TextView text1 = holder.mText1;
            text1.setText(name);
            holder.mImageView.setImageResource(Helpers.GetDrawableResourceForBlobType(isFolder, blobItem));
        } catch (URISyntaxException e) {
            //TODO:
        }
    }
}
