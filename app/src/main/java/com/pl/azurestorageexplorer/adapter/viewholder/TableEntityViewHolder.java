package com.pl.azurestorageexplorer.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.interfaces.IViewHolderClickListener;

/**
 * Created by Praneet Loke on 4/26/2016.
 */
public class TableEntityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView entityPropertyName;
    public TextView entityPropertyValue;
    private IViewHolderClickListener listener;

    public TableEntityViewHolder(View itemView, IViewHolderClickListener viewHolderClickListener) {
        super(itemView);
        listener = viewHolderClickListener;
        entityPropertyName = (TextView) itemView.findViewById(R.id.entityPropertyName);
        entityPropertyValue = (TextView) itemView.findViewById(R.id.entityPropertyValue);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition());
    }
}