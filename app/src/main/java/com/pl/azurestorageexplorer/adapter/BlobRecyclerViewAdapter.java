package com.pl.azurestorageexplorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.interfaces.IRecyclerViewAdapterClickListener;
import com.pl.azurestorageexplorer.adapter.interfaces.IViewHolderClickListener;
import com.pl.azurestorageexplorer.adapter.viewholder.BlobItemViewHolder;
import com.pl.azurestorageexplorer.util.Helpers;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class BlobRecyclerViewAdapter extends LinearRecyclerViewAdapter<ListBlobItem, BlobItemViewHolder> implements IViewHolderClickListener {

    private IRecyclerViewAdapterClickListener<ListBlobItem> recyclerViewAdapterClickListener;

    public BlobRecyclerViewAdapter(ArrayList<ListBlobItem> dataset, IRecyclerViewAdapterClickListener adapterClickListener) {
        super(dataset);
        this.recyclerViewAdapterClickListener = adapterClickListener;
    }

    @Override
    public BlobItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.blob_item_layout, parent, false);
        //TODO: set the view's size, margins, paddings and layout parameters
        BlobItemViewHolder vh = new BlobItemViewHolder(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(BlobItemViewHolder holder, int position) {
        ListBlobItem blobItem = getDataset().get(position);
        try {
            boolean isFolder = !(blobItem instanceof CloudBlob);
            String name = (!isFolder) ? ((CloudBlob) blobItem).getName() : ((CloudBlobDirectory) blobItem).getPrefix();
            TextView text1 = holder.mText1;
            text1.setText(name);
            holder.mImageView.setImageResource(Helpers.getDrawableResourceForBlobType(isFolder, blobItem));
            holder.mLayout2.setVisibility(isFolder ? View.GONE : View.VISIBLE);
        } catch (URISyntaxException e) {
            //TODO:
        }
    }

    @Override
    public void onClick(View view, int adapterPosition) {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            recyclerViewAdapterClickListener.onClick(view, adapterPosition, this.getDataset().get(adapterPosition));
        }
    }
}