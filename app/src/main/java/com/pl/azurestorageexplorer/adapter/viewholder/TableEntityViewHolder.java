package com.pl.azurestorageexplorer.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.interfaces.IViewHolderClickListener;

/**
 * Created by Praneet Loke on 4/26/2016.
 */
public class TableEntityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView partitionKey;
    public TextView rowKey;
    public LinearLayout layout2;
    private IViewHolderClickListener listener;

    public TableEntityViewHolder(View itemView, IViewHolderClickListener viewHolderClickListener) {
        super(itemView);
        listener = viewHolderClickListener;
        LinearLayout layout1 = (LinearLayout) itemView.findViewById(R.id.layout1);
        partitionKey = (TextView) layout1.findViewById(R.id.partitionKey);
        rowKey = (TextView) layout1.findViewById(R.id.rowKey);
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