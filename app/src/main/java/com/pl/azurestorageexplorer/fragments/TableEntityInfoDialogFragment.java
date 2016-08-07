package com.pl.azurestorageexplorer.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.microsoft.azure.storage.table.EntityProperty;
import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.TableEntityRecyclerViewAdapter;
import com.pl.azurestorageexplorer.adapter.interfaces.IRecyclerViewAdapterClickListener;
import com.pl.azurestorageexplorer.asynctask.TableEntityAsyncTask;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;

import java.util.HashMap;

/**
 * Created by Praneet Loke on 4/23/2016.
 */
public class TableEntityInfoDialogFragment extends DialogFragment
        implements
        IRecyclerViewAdapterClickListener<EntityProperty>,
        IAsyncTaskCallback<HashMap<String, EntityProperty>> {

    private TableEntityRecyclerViewAdapter recyclerViewAdapter;

    public TableEntityInfoDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.table_entity_info_dialog_fragment_layout, null);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.entityPropertiesListRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new TableEntityRecyclerViewAdapter(new HashMap<String, EntityProperty>(), this);
        recyclerView.setAdapter(recyclerViewAdapter);

        if (getArguments() != null) {
            ((TextView) root.findViewById(R.id.entityPropertyRowKeyValue)).setText(getArguments().getString("rowKey"));
            builder.setTitle(getArguments().getString("partitionKey"));
        }

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(root)
                // Add action buttons
                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        return builder.create();
    }

    private void getEntityInfo() {
        Bundle args = getArguments();

        if (args == null) {
            return;
        }

        final TableEntityAsyncTask tableEntityAsyncTask = new TableEntityAsyncTask(this);
        tableEntityAsyncTask.execute(args.getString("storageAccount"), args.getString("storageAccountKey"), args.getString("tableName"), args.getString("partitionKey"), args.getString("rowKey"));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        getEntityInfo();
    }

    @Override
    public void onClick(View view, int adapterPosition, EntityProperty item) {

    }

    @Override
    public void finished(HashMap<String, EntityProperty> result) {
        recyclerViewAdapter.replaceDataset(result);
    }

    @Override
    public void failed(String exceptionMessage) {

    }
}
