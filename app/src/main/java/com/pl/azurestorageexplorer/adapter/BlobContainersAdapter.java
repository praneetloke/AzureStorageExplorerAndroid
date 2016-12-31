package com.pl.azurestorageexplorer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.models.CloudBlobContainerSerializable;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/17/2016.
 */
public class BlobContainersAdapter extends SpinnerArrayAdapter<CloudBlobContainerSerializable> {

    public BlobContainersAdapter(Context context, ArrayList<CloudBlobContainerSerializable> blobContainers) {
        super(context, android.R.layout.simple_spinner_item, blobContainers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View item = null;
        if (convertView == null) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_row, parent, false);
        } else {
            item = convertView;
        }

        TextView itemText = (TextView) item.findViewById(R.id.spinnerItemText1);
        if (itemText != null) {
            itemText.setText(getDataset().get(position).getName());
            item.setTag(itemText.getText());
        }

        return item;
    }
}
