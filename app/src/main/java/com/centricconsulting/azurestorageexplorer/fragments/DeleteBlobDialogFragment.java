package com.centricconsulting.azurestorageexplorer.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.centricconsulting.azurestorageexplorer.R;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.IDialogFragmentClickListener;

/**
 * Created by Praneet Loke on 4/26/2016.
 */
public class DeleteBlobDialogFragment extends DialogFragment {
    public DeleteBlobDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(getContext().getString(R.string.delete_blob_title))
                .setMessage(getContext().getString(R.string.delete_blob_confirmation_message))
                .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((IDialogFragmentClickListener) getTargetFragment()).onPositiveClick();
                    }
                })
                .setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((IDialogFragmentClickListener) getTargetFragment()).onNegativeClick();
                    }
                });

        return builder.create();
    }
}
