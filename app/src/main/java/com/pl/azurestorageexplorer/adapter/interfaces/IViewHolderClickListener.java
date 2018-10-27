package com.pl.azurestorageexplorer.adapter.interfaces;

import android.view.View;

/**
 * Created by Praneet Loke on 4/23/2016.
 */
public interface IViewHolderClickListener {
    void onClick(View view, int adapterPosition);
    /**
     * Override this if you want to handle long-clicks.
     * @param view
     * @param adapterPosition
     */
    default void onLongClick(View view, int adapterPosition) {
        return;
    }
}
