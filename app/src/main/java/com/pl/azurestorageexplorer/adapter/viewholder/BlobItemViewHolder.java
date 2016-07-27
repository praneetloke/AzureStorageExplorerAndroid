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
    public TextView mText1;
    public ImageView mImageView;
    public LinearLayout mLayout2;
    private IViewHolderClickListener mListener;

    public BlobItemViewHolder(View itemView, IViewHolderClickListener viewHolderClickListener) {
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
        mListener.onClick(v, getAdapterPosition());
    }
}