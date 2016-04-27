package com.centricconsulting.azurestorageexplorer.adapter.interfaces;

import android.view.View;

/**
 * Created by Praneet Loke on 4/23/2016.
 */
public interface IRecyclerViewAdapterClickListener<T> {
    void onClick(View view, int adapterPosition, T item);
}
