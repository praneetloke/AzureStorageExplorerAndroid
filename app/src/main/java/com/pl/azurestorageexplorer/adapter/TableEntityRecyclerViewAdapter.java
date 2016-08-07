package com.pl.azurestorageexplorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.azure.storage.table.EntityProperty;
import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.interfaces.IRecyclerViewAdapterClickListener;
import com.pl.azurestorageexplorer.adapter.interfaces.IViewHolderClickListener;
import com.pl.azurestorageexplorer.adapter.viewholder.TableEntityViewHolder;

import java.util.HashMap;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class TableEntityRecyclerViewAdapter extends LinearHashMapRecyclerViewAdapter<String, EntityProperty, TableEntityViewHolder> implements IViewHolderClickListener {

    private IRecyclerViewAdapterClickListener<EntityProperty> recyclerViewAdapterClickListener;

    public TableEntityRecyclerViewAdapter(HashMap<String, EntityProperty> dataset, IRecyclerViewAdapterClickListener adapterClickListener) {
        super(dataset);
        this.recyclerViewAdapterClickListener = adapterClickListener;
    }

    @Override
    public TableEntityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_entity_layout, parent, false);
        TableEntityViewHolder vh = new TableEntityViewHolder(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(TableEntityViewHolder holder, int position) {
        String entityPropertyName = getKey(position);
        EntityProperty entityProperty = getValue(entityPropertyName);
        holder.entityPropertyName.setText(entityPropertyName);
        holder.entityPropertyValue.setText(entityProperty.getValueAsString());
    }

    @Override
    public void onClick(View view, int adapterPosition) {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            recyclerViewAdapterClickListener.onClick(view, adapterPosition, getValue(getKey(adapterPosition)));
        }
    }
}
