package com.centricconsulting.azurestorageexplorer.arrayadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.centricconsulting.azurestorageexplorer.R;
import com.centricconsulting.azurestorageexplorer.storage.models.AzureStorageAccount;

import java.util.ArrayList;

/**
 * Created by v-prloke on 4/10/2016.
 */
public class SpinnerArrayAdapter extends ArrayAdapter<AzureStorageAccount> {
    private ArrayList<AzureStorageAccount> accounts;
    private LayoutInflater layoutInflater;

    public SpinnerArrayAdapter(Context context, ArrayList<AzureStorageAccount> accounts) {
        super(context, android.R.layout.simple_spinner_item, accounts);
        this.accounts = accounts;

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View item = null;
        if (convertView == null) {
            item = layoutInflater.inflate(R.layout.spinner_row, parent, false);
        } else {
            item = convertView;
        }

        TextView itemText = (TextView) item.findViewById(R.id.spinnerItemText1);
        if (itemText != null) {
            itemText.setText(accounts.get(position).getName());
        }
        return item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }
}
