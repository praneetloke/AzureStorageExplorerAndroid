package com.centricconsulting.azurestorageexplorer.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centricconsulting.azurestorageexplorer.R;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.IDialogFragmentClickListener;
import com.centricconsulting.azurestorageexplorer.storage.models.AzureStorageAccount;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/26/2016.
 */
public class SubscriptionsFilterDialogFragment extends DialogFragment {
    private ArrayList<AzureStorageAccount> mStorageAccounts;

    public SubscriptionsFilterDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().containsKey("accounts")) {
            this.mStorageAccounts = (ArrayList<AzureStorageAccount>) getArguments().getSerializable("accounts");
        }
        View view = inflater.inflate(R.layout.storage_accounts_filter_layout, container);

        getDialog().setTitle("Filter storage accounts");

        return view;
    }
}
