package com.centricconsulting.azurestorageexplorer.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.centricconsulting.azurestorageexplorer.R;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/17/2016.
 */
public class BlobContainersAdapter extends SpinnerArrayAdapter<CloudBlobContainer> {

    public BlobContainersAdapter(Context context, ArrayList<CloudBlobContainer> blobContainers) {
        super(context, blobContainers);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View item = null;
        if (convertView == null) {
            item = getLayoutInflater().inflate(R.layout.spinner_row, parent, false);
        } else {
            item = convertView;
        }

        TextView itemText = (TextView) item.findViewById(R.id.spinnerItemText1);
        if (itemText != null) {
            itemText.setEllipsize(TextUtils.TruncateAt.END);
            itemText.setWidth(getContext().getResources().getDimensionPixelSize(R.dimen.toolbar_spinner_item_width));
            itemText.setSingleLine();
            itemText.setText(getDataset().get(position).getName());
        }

        item.setTag(itemText.getText());
        item.setLongClickable(true);
        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), v.getTag().toString(), Toast.LENGTH_LONG).show();
                return true;
            }
        });
        return item;
    }
}
