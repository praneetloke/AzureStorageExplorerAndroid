package com.centricconsulting.azurestorageexplorer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centricconsulting.azurestorageexplorer.R;
import com.centricconsulting.azurestorageexplorer.adapter.BlobRecyclerViewAdapter;
import com.centricconsulting.azurestorageexplorer.adapter.interfaces.IRecyclerViewAdapterClickListener;
import com.centricconsulting.azurestorageexplorer.asynctask.BlobListAsyncTask;
import com.centricconsulting.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.IBlobItemNavigateListener;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.ISpinnerNavListener;
import com.centricconsulting.azurestorageexplorer.models.AzureStorageAccount;
import com.centricconsulting.azurestorageexplorer.util.Helpers;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class BlobListFragment extends Fragment
        implements
        ISpinnerNavListener<CloudBlobContainer>,
        IBlobItemNavigateListener,
        IAsyncTaskCallback<ArrayList<ListBlobItem>>,
        IRecyclerViewAdapterClickListener<ListBlobItem> {
    private BlobRecyclerViewAdapter recyclerViewAdapter;

    public BlobListFragment() {

    }

    public static BlobListFragment newInstance() {
        return new BlobListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View root = layoutInflater.inflate(R.layout.blob_list_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.containersRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new BlobRecyclerViewAdapter(null, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        return root;
    }

    @Override
    public void selectionChanged(AzureStorageAccount account, CloudBlobContainer container) {
        //get the blobs for this container
        BlobListAsyncTask blobListAsyncTask = new BlobListAsyncTask(this);
        blobListAsyncTask.execute(account.getName(), account.getKey(), container.getName());
    }

    @Override
    public void finished(ArrayList<ListBlobItem> result) {
        recyclerViewAdapter.replaceDataset(result);
    }

    @Override
    public void failed(String exceptionMessage) {

    }

    @Override
    public void onClick(int viewId, ListBlobItem item) {
        Bundle fragmentArgs = getArguments();
        if (fragmentArgs == null) {
            Log.e(this.getClass().getName(), "Fragment wasn't initialized with arguments.");
        }
        if (fragmentArgs.getSerializable("fragmentListener") == null) {
            Log.e(this.getClass().getName(), "Fragment arguments bundle does not contain the fragmentListener key.");
        }

        //if the info icon was clicked, show the info dialog
        if (viewId == R.id.layout2) {
            BlobInfoDialogFragment fragment = new BlobInfoDialogFragment();
            fragment.setArguments(Helpers.getBlobInfoFromListBlobItem(item));
            fragment.show(getActivity().getSupportFragmentManager(), "BlobInfoDialogFragment");
            return;
        }

        //only tell the parent listener if it is a folder..handle other clicks within
        if ((item instanceof CloudBlobDirectory)) {
            ((OnFragmentInteractionListener) fragmentArgs.getSerializable("fragmentListener")).onBlobItemClicked(item);
        } else {
            //TODO: download the blob?
        }
    }

    @Override
    public void onBlobItemClick(AzureStorageAccount account, ListBlobItem listBlobItem) {
        CloudBlobDirectory cloudBlobDirectory = (CloudBlobDirectory) listBlobItem;
        try {
            //get the blobs for this blob directory
            BlobListAsyncTask blobListAsyncTask = new BlobListAsyncTask(this);
            blobListAsyncTask.execute(account.getName(), account.getKey(), cloudBlobDirectory.getContainer().getName(), cloudBlobDirectory.getPrefix());
        } catch (StorageException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
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
        void onBlobItemClicked(ListBlobItem account);
    }
}
