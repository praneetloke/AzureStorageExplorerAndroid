package com.centricconsulting.azurestorageexplorer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centricconsulting.azurestorageexplorer.R;
import com.centricconsulting.azurestorageexplorer.adapter.BlobContainerRecyclerViewAdapter;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.ISpinnerNavListener;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class BlobListFragment extends Fragment implements ISpinnerNavListener<CloudBlobContainer> {
    private BlobContainerRecyclerViewAdapter recyclerViewAdapter;

    public BlobListFragment() {

    }

    public static BlobListFragment newInstance() {
        return new BlobListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View root = layoutInflater.inflate(R.layout.blob_list_fragment, container, false);

//        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.containersRecyclerView);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerViewAdapter = new BlobContainerRecyclerViewAdapter(null);
//        recyclerView.setAdapter(recyclerViewAdapter);

        return root;
    }

    @Override
    public void selectionChanged(CloudBlobContainer container) {
        //TODO: get the blobs
    }
}
