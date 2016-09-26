package com.pl.azurestorageexplorer.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.interfaces.IViewHolderClickListener;

/**
 * Created by Praneet Loke on 4/26/2016.
 */
public class BlobItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView text1;
    public TextView blobSizeText;
    public ImageView imageView;
    public LinearLayout layout2;
    private IViewHolderClickListener listener;

    public BlobItemViewHolder(View itemView, IViewHolderClickListener viewHolderClickListener) {
        super(itemView);
        listener = viewHolderClickListener;
        LinearLayout layout1 = (LinearLayout) itemView.findViewById(R.id.layout1);
        text1 = (TextView) layout1.findViewById(R.id.text1);
        blobSizeText = (TextView) layout1.findViewById(R.id.blobSizeText);
        imageView = (ImageView) layout1.findViewById(R.id.icon);
        layout1.setOnClickListener(this);

        layout2 = (LinearLayout) itemView.findViewById(R.id.layout2);
        //set the click listener for the "info" layout
        layout2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition());
    }
}