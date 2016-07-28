package com.pl.azurestorageexplorer.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.interfaces.IViewHolderClickListener;

/**
 * Created by Praneet Loke on 4/26/2016.
 */
public class SubscriptionItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView subscriptionId;
    public CheckBox isSubscriptionSelected;
    private IViewHolderClickListener mListener;

    public SubscriptionItemViewHolder(View itemView, IViewHolderClickListener viewHolderClickListener) {
        super(itemView);
        mListener = viewHolderClickListener;

        subscriptionId = (TextView) itemView.findViewById(R.id.subscriptionId);
        isSubscriptionSelected = (CheckBox) itemView.findViewById(R.id.isSubscriptionSelected);
        isSubscriptionSelected.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onClick(v, getAdapterPosition());
    }
}