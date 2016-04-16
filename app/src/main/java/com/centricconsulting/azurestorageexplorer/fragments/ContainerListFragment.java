package com.centricconsulting.azurestorageexplorer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.centricconsulting.azurestorageexplorer.R;
import com.centricconsulting.azurestorageexplorer.arrayadapters.BlobContainerRecyclerViewAdapter;
import com.centricconsulting.azurestorageexplorer.asynctask.BlobListAsyncTask;
import com.centricconsulting.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.ISpinnerNavListener;
import com.centricconsulting.azurestorageexplorer.storage.models.AzureStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

import java.util.ArrayList;

/**
 * Created by v-prloke on 4/16/2016.
 */
public class ContainerListFragment extends Fragment implements IAsyncTaskCallback<ArrayList<CloudBlobContainer>>,
        ISpinnerNavListener<AzureStorageAccount> {
    private BlobContainerRecyclerViewAdapter recyclerViewAdapter;
    private String storageAccountName;
    private String storageKey;

    public ContainerListFragment() {

    }

    public static ContainerListFragment newInstance() {
        return new ContainerListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View root = layoutInflater.inflate(R.layout.containers_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.containersRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new BlobContainerRecyclerViewAdapter(null);
        recyclerView.setAdapter(recyclerViewAdapter);

        return root;
    }

    @Override
    public void finished(ArrayList<CloudBlobContainer> result) {
        recyclerViewAdapter.replaceDataset(result);
    }

    @Override
    public void failed(String exceptionMessage) {
        Toast.makeText(getContext(), exceptionMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void selectionChanged(AzureStorageAccount storageAccount) {
        BlobListAsyncTask blobListAsyncTask = new BlobListAsyncTask(this);
        blobListAsyncTask.execute(storageAccount.getName(), storageAccount.getKey());
    }
}
