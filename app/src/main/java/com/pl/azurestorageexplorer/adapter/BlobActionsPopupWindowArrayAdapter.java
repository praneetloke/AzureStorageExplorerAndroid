package com.pl.azurestorageexplorer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/27/2016.
 */
public class BlobActionsPopupWindowArrayAdapter extends SpinnerArrayAdapter<String> implements Serializable {
    private ArrayList<Integer> iconResIds;

    public BlobActionsPopupWindowArrayAdapter(Context context, int layoutResId, ArrayList<String> dataset, ArrayList<Integer> iconResIds) {
        this(context, layoutResId, dataset);
        this.iconResIds = iconResIds;
    }

    public BlobActionsPopupWindowArrayAdapter(Context context, int layoutResId, ArrayList<String> dataset) {
        super(context, layoutResId, dataset);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View item = null;
        if (convertView == null) {
            item = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        } else {
            item = convertView;
        }

        TextView itemText = (TextView) item.findViewById(android.R.id.text1);
        if (itemText != null) {
            itemText.setText(getDataset().get(position));
            if (iconResIds != null && iconResIds.size() > 0) {
                itemText.setCompoundDrawablePadding(15);
                itemText.setCompoundDrawablesWithIntrinsicBounds(iconResIds.get(position), 0, 0, 0);
                item.setTag(iconResIds.get(position));
            } else {
                item.setTag(itemText.getText());
            }
        }

        return item;
    }
}
