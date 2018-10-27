package com.pl.azurestorageexplorer.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.fragments.interfaces.IDialogFragmentClickListener;

/**
 * Created by Praneet Loke on 4/26/2016.
 */
public class ConfirmationDialogFragment extends DialogFragment {
    public ConfirmationDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(args.getString("title"))
                .setMessage(Html.fromHtml(args.getString("message")))
                .setPositiveButton(getContext().getString(R.string.yes), (dialog, which) -> {
                    if (getTargetFragment() != null) {
                        ((IDialogFragmentClickListener) getTargetFragment()).onConfirmationDialogPositiveClick(getTargetRequestCode());
                    } else {
                        ((IDialogFragmentClickListener) getActivity()).onConfirmationDialogPositiveClick(args.getInt("requestCode", 0));
                    }
                })
                .setNegativeButton(getContext().getString(R.string.no), (dialog, which) -> {
                    dialog.dismiss();
                    if (getTargetFragment() != null) {
                        ((IDialogFragmentClickListener) getTargetFragment()).onConfirmationDialogNegativeClick(getTargetRequestCode());
                    } else {
                        ((IDialogFragmentClickListener) getActivity()).onConfirmationDialogNegativeClick(args.getInt("requestCode", 0));
                    }
                });

        return builder.create();
    }
}
