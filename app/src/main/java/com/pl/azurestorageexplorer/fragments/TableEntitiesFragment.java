package com.pl.azurestorageexplorer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.BlobActionsPopupWindowArrayAdapter;
import com.pl.azurestorageexplorer.adapter.TableEntitiesRecyclerViewAdapter;
import com.pl.azurestorageexplorer.adapter.interfaces.IRecyclerViewAdapterClickListener;
import com.pl.azurestorageexplorer.asynctask.TableEntitiesAsyncTask;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallbackWithResultContinuation;
import com.pl.azurestorageexplorer.fragments.interfaces.IDialogFragmentClickListener;
import com.pl.azurestorageexplorer.fragments.interfaces.ISpinnerNavListener;
import com.pl.azurestorageexplorer.models.StorageTableSerializable;
import com.pl.azurestorageexplorer.storage.models.AzureStorageAccount;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class TableEntitiesFragment extends Fragment
        implements
        ISpinnerNavListener<StorageTableSerializable>,
        IRecyclerViewAdapterClickListener<DynamicTableEntity>,
        AdapterView.OnItemClickListener,
        IDialogFragmentClickListener,
        IAsyncTaskCallbackWithResultContinuation<ArrayList<DynamicTableEntity>> {

    private static final ArrayList<String> TABLE_ENTITY_ACTIONS = new ArrayList<>();
    private static final ArrayList<Integer> TABLE_ENTITY_ACTIONS_ICONS = new ArrayList<>();
    private static final int DELETE_REQUEST_CODE = 1;
    private TableEntitiesRecyclerViewAdapter recyclerViewAdapter;
    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private ResultContinuation resultContinuation;
    private boolean hasMoreResults;
    private String currentTableName;
    private ListPopupWindow listPopupWindow;
    private AzureStorageAccount azureStorageAccount;
    private int currentlySelectedTableEntityItemAdapterPosition;

    public TableEntitiesFragment() {
        if (TABLE_ENTITY_ACTIONS.size() == 0) {
            TABLE_ENTITY_ACTIONS.add("Open");
            TABLE_ENTITY_ACTIONS.add("Properties");
            TABLE_ENTITY_ACTIONS.add("Delete forever");

            TABLE_ENTITY_ACTIONS_ICONS.add(R.drawable.ic_view);
            TABLE_ENTITY_ACTIONS_ICONS.add(R.drawable.ic_info_outline);
            TABLE_ENTITY_ACTIONS_ICONS.add(R.drawable.ic_delete_forever);
        }
    }

    public static TableEntitiesFragment newInstance() {
        return new TableEntitiesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View root = layoutInflater.inflate(R.layout.table_entities_fragment_layout, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.tableEntitiesRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new TableEntitiesRecyclerViewAdapter(new ArrayList<DynamicTableEntity>(), this);
        recyclerView.setAdapter(recyclerViewAdapter);

        progressBar = (ProgressBar) root.findViewById(R.id.tableEntitiesProgressBar);

        return root;
    }

    private void showProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void selectionChanged(AzureStorageAccount account, StorageTableSerializable table) {
        showProgressBar();
        //get the blobs for this container
        resultContinuation = null;
        hasMoreResults = false;
        currentTableName = table.getName();
        azureStorageAccount = account;
        //reset the list
        recyclerViewAdapter.getDataset().clear();
        TableEntitiesAsyncTask tableEntitiesAsyncTask = new TableEntitiesAsyncTask(this, resultContinuation);
        tableEntitiesAsyncTask.execute(account.getName(), account.getKey(), table.getName());
    }

    @Override
    public void finished(ArrayList<DynamicTableEntity> result, ResultContinuation resultContinuation, boolean hasMoreResults) {
        hideProgressBar();
        this.hasMoreResults = hasMoreResults;
        this.resultContinuation = resultContinuation;
        recyclerViewAdapter.getDataset().addAll(result);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void failed(String exceptionMessage) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentTableName != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentTableName);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (currentTableName != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentTableName);
        }
    }

    @Override
    public void onClick(View view, int adapterPosition, DynamicTableEntity item) {
        //TODO: show an info dialog that shows the full entity
        if (view.getId() == R.id.layout1) {

        } else if (view.getId() == R.id.layout2) {
            currentlySelectedTableEntityItemAdapterPosition = adapterPosition;
            showPopup(view);
        }

        ((OnFragmentInteractionListener) getActivity()).onTableEntityClicked(item);
    }

    private void showPopup(View view) {
        if (listPopupWindow == null) {
            listPopupWindow = new ListPopupWindow(getContext());
            listPopupWindow.setModal(false);
            listPopupWindow.setOnItemClickListener(this);
            listPopupWindow.setWidth(350);
            listPopupWindow.setAdapter(new BlobActionsPopupWindowArrayAdapter(getContext(), android.R.layout.simple_list_item_1, TABLE_ENTITY_ACTIONS, TABLE_ENTITY_ACTIONS_ICONS));
            listPopupWindow.setDropDownGravity(Gravity.START);
        }

        listPopupWindow.setAnchorView(view);
        listPopupWindow.show();
    }

    public void showSnackbar(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void showToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConfirmationDialogPositiveClick(int requestCode) {
        if (requestCode == DELETE_REQUEST_CODE) {
            //TODO: delete the entity
        }
    }

    @Override
    public void onConfirmationDialogNegativeClick(int requestCode) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listPopupWindow.dismiss();

        switch ((int) view.getTag()) {
            case R.drawable.ic_info_outline:
                //TODO: show info dialog
                Toast.makeText(getContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
                break;
            case R.drawable.ic_delete_forever: {
                Bundle args = new Bundle();
                args.putString("title", getString(R.string.delete_table_entity_title));
                args.putString("message", getString(R.string.delete_table_entity_confirmation_message));
                ConfirmationDialogFragment deleteConfirmation = new ConfirmationDialogFragment();
                deleteConfirmation.setArguments(args);
                deleteConfirmation.setTargetFragment(this, DELETE_REQUEST_CODE);
                deleteConfirmation.show(getActivity().getSupportFragmentManager(), ConfirmationDialogFragment.class.getName());
            }

                break;
            case R.drawable.ic_view: {
                //show the full entity in a dialog fragment
                Bundle args = new Bundle();
                args.putString("storageAccount", azureStorageAccount.getName());
                args.putString("storageAccountKey", azureStorageAccount.getKey());
                args.putString("tableName", currentTableName);
                args.putString("partitionKey", recyclerViewAdapter.getDataset().get(currentlySelectedTableEntityItemAdapterPosition).getPartitionKey());
                args.putString("rowKey", recyclerViewAdapter.getDataset().get(currentlySelectedTableEntityItemAdapterPosition).getRowKey());
                TableEntityInfoDialogFragment tableEntityInfoDialogFragment = new TableEntityInfoDialogFragment();
                tableEntityInfoDialogFragment.setArguments(args);
                tableEntityInfoDialogFragment.show(getActivity().getSupportFragmentManager(), TableEntityInfoDialogFragment.class.getName());
            }
                break;
            default:
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener extends Serializable {
        void onTableEntityClicked(DynamicTableEntity tableEntity);
    }
}
