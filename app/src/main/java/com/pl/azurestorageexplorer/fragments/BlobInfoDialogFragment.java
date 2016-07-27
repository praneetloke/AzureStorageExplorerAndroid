package com.pl.azurestorageexplorer.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pl.azurestorageexplorer.R;

/**
 * Created by Praneet Loke on 4/23/2016.
 */
public class BlobInfoDialogFragment extends DialogFragment {

    public BlobInfoDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = getArguments();

        // Inflate and set the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.blob_info_dialog_fragment_layout, null);

        ((TextView) view.findViewById(R.id.lastModified)).setText(args.getString("lastModified"));
        ((TextView) view.findViewById(R.id.contentType)).setText(args.getString("contentType"));
        ((TextView) view.findViewById(R.id.cacheControl)).setText(args.getString("cacheControl"));
        ((TextView) view.findViewById(R.id.blobType)).setText(args.getString("blobType"));
        ((TextView) view.findViewById(R.id.length)).setText(args.getString("length"));

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        if (args.containsKey("title")) {
            builder.setTitle(args.getString("title"));
        }
        return builder.create();
    }
}
