package com.pl.azurestorageexplorer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.storage.models.AzureStorageAccount;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/17/2016.
 */
public class StorageAccountAdapter extends SpinnerArrayAdapter<AzureStorageAccount> {

    public StorageAccountAdapter(Context context, ArrayList<AzureStorageAccount> accounts) {
        super(context, android.R.layout.simple_spinner_item, accounts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View item = null;
        if (convertView == null) {
            item = LayoutInflater.from(getContext()).inflate(R.layout.spinner_row, parent, false);
        } else {
            item = convertView;
        }

        TextView itemText = (TextView) item.findViewById(R.id.spinnerItemText1);
        if (itemText != null) {
            itemText.setText(getDataset().get(position).getName());
        }
        return item;
    }
}
