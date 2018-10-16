package com.pl.azurestorageexplorer.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.pl.azurestorageexplorer.AzureStorageExplorerApplication;
import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.SubscriptionsRecyclerViewAdapter;
import com.pl.azurestorageexplorer.adapter.interfaces.IRecyclerViewAdapterClickListener;
import com.pl.azurestorageexplorer.fragments.interfaces.ISubscriptionSelectionChangeListener;
import com.pl.azurestorageexplorer.storage.models.AzureSubscriptionFilter;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/26/2016.
 */
public class SubscriptionsFilterDialogFragment extends DialogFragment
        implements IRecyclerViewAdapterClickListener<AzureSubscriptionFilter> {
    private ArrayList<AzureSubscriptionFilter> subscriptions;

    public SubscriptionsFilterDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.subscriptions_filter_layout, null);

        this.subscriptions = AzureStorageExplorerApplication.getCustomSQLiteHelper().getAzureSubscriptionsFilters();

        RecyclerView recyclerView = view.findViewById(R.id.subscriptionsFilterRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new SubscriptionsRecyclerViewAdapter(this.subscriptions, this));

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setNeutralButton(R.string.close, (dialog, which) -> dialog.dismiss());

        builder.setTitle("Filter Subscriptions");

        return builder.create();
    }

    @Override
    public void onClick(View view, int adapterPosition, AzureSubscriptionFilter item) {
        AzureStorageExplorerApplication.getCustomSQLiteHelper().updateAzureSubscriptionsFilterSelection(item);

        //tell main activity to filter the storage accounts in the nav menu header's spinner
        ((ISubscriptionSelectionChangeListener) getActivity()).onSubscriptionSelectionChanged(item);
    }
}
