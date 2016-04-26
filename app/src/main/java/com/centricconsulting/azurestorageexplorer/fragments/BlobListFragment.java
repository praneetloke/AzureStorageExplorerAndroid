package com.centricconsulting.azurestorageexplorer.fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.centricconsulting.azurestorageexplorer.R;
import com.centricconsulting.azurestorageexplorer.adapter.BlobRecyclerViewAdapter;
import com.centricconsulting.azurestorageexplorer.adapter.interfaces.IRecyclerViewAdapterClickListener;
import com.centricconsulting.azurestorageexplorer.asynctask.BlobListAsyncTask;
import com.centricconsulting.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.IBlobItemNavigateListener;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.ISpinnerNavListener;
import com.centricconsulting.azurestorageexplorer.models.AzureStorageAccount;
import com.centricconsulting.azurestorageexplorer.models.CloudBlobContainerSerializable;
import com.centricconsulting.azurestorageexplorer.models.CloudBlobDirectorySerializable;
import com.centricconsulting.azurestorageexplorer.util.Helpers;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class BlobListFragment extends Fragment
        implements
        ISpinnerNavListener<CloudBlobContainerSerializable>,
        IBlobItemNavigateListener,
        IAsyncTaskCallback<ArrayList<ListBlobItem>>,
        IRecyclerViewAdapterClickListener<ListBlobItem> {
    private BlobRecyclerViewAdapter recyclerViewAdapter;
    private CloudBlobDirectorySerializable mCurrentBlobDirectory;

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
    public void selectionChanged(AzureStorageAccount account, CloudBlobContainerSerializable container) {
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
    public void onResume() {
        super.onResume();
        if (mCurrentBlobDirectory != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mCurrentBlobDirectory.getPrefix());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mCurrentBlobDirectory != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mCurrentBlobDirectory.getPrefix());
        }
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
            ((OnFragmentInteractionListener) getActivity()).onBlobItemClicked(item);
        } else {
            //download the blob
            final CloudBlob cloudBlob = (CloudBlob) item;
            final Context context = getContext();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                        File file = new File(path);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        String fileName = cloudBlob.getName().replace("/", "_");
                        File imageFile = new File(file.getAbsolutePath(), fileName);
                        if (!imageFile.exists()) {
                            imageFile.createNewFile();
                        }
                        cloudBlob.downloadToFile(imageFile.getAbsolutePath());

                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        downloadManager.addCompletedDownload(
                                fileName,
                                cloudBlob.getName(),
                                true,
                                cloudBlob.getProperties().getContentType(),
                                imageFile.getAbsolutePath(),
                                imageFile.length(),
                                true);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onBlobItemClick(AzureStorageAccount account, ListBlobItem listBlobItem) {
        try {
            CloudBlobDirectory cloudBlobDirectory = (CloudBlobDirectory) listBlobItem;
            mCurrentBlobDirectory = new CloudBlobDirectorySerializable(cloudBlobDirectory.getContainer().getName(), cloudBlobDirectory.getPrefix());
            //get the blobs for this blob directory
            BlobListAsyncTask blobListAsyncTask = new BlobListAsyncTask(this);
            blobListAsyncTask.execute(account.getName(), account.getKey(), mCurrentBlobDirectory.getContainerName(), mCurrentBlobDirectory.getPrefix());
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
