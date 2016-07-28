package com.pl.azurestorageexplorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.interfaces.IRecyclerViewAdapterClickListener;
import com.pl.azurestorageexplorer.adapter.interfaces.IViewHolderClickListener;
import com.pl.azurestorageexplorer.adapter.viewholder.SubscriptionItemViewHolder;
import com.pl.azurestorageexplorer.storage.models.AzureSubscriptionFilter;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 7/24/2016.
 */
public class SubscriptionsRecyclerViewAdapter extends LinearRecyclerViewAdapter<AzureSubscriptionFilter, SubscriptionItemViewHolder> implements IViewHolderClickListener {
    private IRecyclerViewAdapterClickListener<AzureSubscriptionFilter> recyclerViewAdapterClickListener;

    public SubscriptionsRecyclerViewAdapter(ArrayList<AzureSubscriptionFilter> dataset, IRecyclerViewAdapterClickListener<AzureSubscriptionFilter> recyclerViewAdapterClickListener) {
        super(dataset);
        this.recyclerViewAdapterClickListener = recyclerViewAdapterClickListener;
    }

    @Override
    public SubscriptionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_item_layout, parent, false);
        //TODO: set the view's size, margins, paddings and layout parameters
        SubscriptionItemViewHolder vh = new SubscriptionItemViewHolder(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(SubscriptionItemViewHolder holder, int position) {
        AzureSubscriptionFilter subscription = getDataset().get(position);

        TextView text1 = holder.subscriptionId;
        text1.setText(subscription.getSubscriptionId());

        CheckBox checkBox = holder.isSubscriptionSelected;
        checkBox.setChecked(subscription.isSelected());
        checkBox.setText(subscription.getName());
    }

    @Override
    public void onClick(View view, int adapterPosition) {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            //update the isSelected in the adapter
            this.getDataset().get(adapterPosition).setSelected(((CheckBox) view).isChecked());
            //tell the owning fragment
            recyclerViewAdapterClickListener.onClick(view, adapterPosition, this.getDataset().get(adapterPosition));
        }
    }
}
