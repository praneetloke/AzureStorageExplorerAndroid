package com.pl.azurestorageexplorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.interfaces.IRecyclerViewAdapterClickListener;
import com.pl.azurestorageexplorer.adapter.interfaces.IViewHolderClickListener;
import com.pl.azurestorageexplorer.adapter.viewholder.TableEntitiesListItemViewHolder;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class TableEntitiesRecyclerViewAdapter extends LinearRecyclerViewAdapter<DynamicTableEntity, TableEntitiesListItemViewHolder> implements IViewHolderClickListener {

    private IRecyclerViewAdapterClickListener<DynamicTableEntity> recyclerViewAdapterClickListener;

    public TableEntitiesRecyclerViewAdapter(ArrayList<DynamicTableEntity> dataset, IRecyclerViewAdapterClickListener adapterClickListener) {
        super(dataset);
        this.recyclerViewAdapterClickListener = adapterClickListener;
    }

    @Override
    public TableEntitiesListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_entities_list_item_layout, parent, false);
        //TODO: set the view's size, margins, paddings and layout parameters
        TableEntitiesListItemViewHolder vh = new TableEntitiesListItemViewHolder(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(TableEntitiesListItemViewHolder holder, int position) {
        DynamicTableEntity tableEntity = getDataset().get(position);
        holder.partitionKey.setText(tableEntity.getPartitionKey());
        holder.rowKey.setText(tableEntity.getRowKey());
    }

    @Override
    public void onClick(View view, int adapterPosition) {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            recyclerViewAdapterClickListener.onClick(view, adapterPosition, this.getDataset().get(adapterPosition));
        }
    }
}
