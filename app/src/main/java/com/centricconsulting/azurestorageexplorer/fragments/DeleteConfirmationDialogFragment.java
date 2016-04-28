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
public class DeleteConfirmationDialogFragment extends DialogFragment {
    public DeleteConfirmationDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(args.getString("title"))
                .setMessage(args.getString("message"))
                .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getTargetFragment() != null) {
                            ((IDialogFragmentClickListener) getTargetFragment()).onPositiveClick();
                        } else {
                            ((IDialogFragmentClickListener) getActivity()).onPositiveClick();
                        }
                    }
                })
                .setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (getTargetFragment() != null) {
                            ((IDialogFragmentClickListener) getTargetFragment()).onNegativeClick();
                        } else {
                            ((IDialogFragmentClickListener) getActivity()).onNegativeClick();
                        }
                    }
                });

        return builder.create();
    }
}
