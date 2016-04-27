package com.centricconsulting.azurestorageexplorer.fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.centricconsulting.azurestorageexplorer.R;
import com.centricconsulting.azurestorageexplorer.adapter.BlobRecyclerViewAdapter;
import com.centricconsulting.azurestorageexplorer.adapter.interfaces.IRecyclerViewAdapterClickListener;
import com.centricconsulting.azurestorageexplorer.asynctask.BlobListAsyncTask;
import com.centricconsulting.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.IBlobItemNavigateListener;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.IDialogFragmentClickListener;
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
        IRecyclerViewAdapterClickListener<ListBlobItem>,
        PopupMenu.OnMenuItemClickListener,
        IDialogFragmentClickListener {
    private static final long ANIMATION_DURATION = 500;
    private BlobRecyclerViewAdapter recyclerViewAdapter;
    private CloudBlobDirectorySerializable mCurrentBlobDirectory;
    private int mCurrentlySelectedBlobItemAdapterPosition;

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
    public void onClick(View view, int adapterPosition, ListBlobItem item) {
        //if the info icon was clicked, show the info dialog
        if (view.getId() == R.id.layout2) {
            mCurrentlySelectedBlobItemAdapterPosition = adapterPosition;
            showPopup(view);
            return;
        }

        //only tell the parent listener if it is a folder..handle other clicks within
        if ((item instanceof CloudBlobDirectory)) {
            ((OnFragmentInteractionListener) getActivity()).onBlobItemClicked(item);
        }
    }

    private void showPopup(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.blob_actions);
        popup.show();
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final CloudBlob cloudBlob = (CloudBlob) recyclerViewAdapter.getDataset().get(mCurrentlySelectedBlobItemAdapterPosition);

        switch (item.getItemId()) {
            case R.id.blob_properties:
                BlobInfoDialogFragment fragment = new BlobInfoDialogFragment();
                fragment.setArguments(Helpers.getBlobInfoFromListBlobItem(cloudBlob));
                fragment.show(getActivity().getSupportFragmentManager(), "BlobInfoDialogFragment");
                return true;
            case R.id.blob_delete:
                DeleteBlobDialogFragment deleteBlobDialogFragment = new DeleteBlobDialogFragment();
                deleteBlobDialogFragment.setTargetFragment(this, R.id.blob_delete);
                deleteBlobDialogFragment.show(getActivity().getSupportFragmentManager(), "DeleteBlobDialogFragment");
                return true;
            case R.id.blob_download:
                //download the blob
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
                            showToast(e.getMessage());
                        }
                    }
                }).start();
                return true;
            case R.id.blob_view:
            default:
                return false;
        }
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
    public void onPositiveClick() {
        final CloudBlob cloudBlob = (CloudBlob) recyclerViewAdapter.getDataset().get(mCurrentlySelectedBlobItemAdapterPosition);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (cloudBlob.deleteIfExists()) {
                        showSnackbar(BlobListFragment.this.getString(R.string.delete_confirmation));
                        //delete this item from the local dataset and notify the adapter
                        recyclerViewAdapter.getDataset().remove(cloudBlob);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (StorageException e) {
                    showToast(BlobListFragment.this.getString(R.string.blob_delete_failed));
                }
            }
        }).start();
    }

    @Override
    public void onNegativeClick() {

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
