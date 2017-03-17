package com.pl.azurestorageexplorer.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.adapter.BlobActionsPopupWindowArrayAdapter;
import com.pl.azurestorageexplorer.adapter.BlobRecyclerViewAdapter;
import com.pl.azurestorageexplorer.adapter.interfaces.IRecyclerViewAdapterClickListener;
import com.pl.azurestorageexplorer.asynctask.BlobListAsyncTask;
import com.pl.azurestorageexplorer.asynctask.UploadBlobAsyncTask;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.pl.azurestorageexplorer.fragments.interfaces.IBlobItemNavigateListener;
import com.pl.azurestorageexplorer.fragments.interfaces.ICoordinatorLayoutFragment;
import com.pl.azurestorageexplorer.fragments.interfaces.IDialogFragmentClickListener;
import com.pl.azurestorageexplorer.fragments.interfaces.ISpinnerNavListener;
import com.pl.azurestorageexplorer.models.BlobToUpload;
import com.pl.azurestorageexplorer.models.CloudBlobContainerSerializable;
import com.pl.azurestorageexplorer.models.CloudBlobDirectorySerializable;
import com.pl.azurestorageexplorer.storage.models.AzureStorageAccount;
import com.pl.azurestorageexplorer.util.Helpers;

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
        IAsyncTaskCallback,
        IRecyclerViewAdapterClickListener<ListBlobItem>,
        AdapterView.OnItemClickListener,
        IDialogFragmentClickListener,
        ICoordinatorLayoutFragment {

    private static final ArrayList<String> BLOB_ACTIONS = new ArrayList<>();
    private static final ArrayList<Integer> BLOB_ACTIONS_ICONS = new ArrayList<>();
    private static final int DELETE_REQUEST_CODE = 21;
    private static final int INTENT_OPEN_DOCUMENT_REQUEST_CODE = 45;
    private static final int READ_WRITE_EXTERNAL_STORAGE_FOR_BLOB_DOWNLOAD_PERMISSION_REQUEST_CODE = 87;
    private static final int READ_WRITE_EXTERNAL_STORAGE_FOR_BLOB_OPEN_PERMISSION_REQUEST_CODE = 99;
    private BlobRecyclerViewAdapter recyclerViewAdapter;
    private CloudBlobDirectorySerializable currentBlobDirectory;
    private int currentlySelectedBlobItemAdapterPosition;
    private ListPopupWindow listPopupWindow;
    private ProgressBar progressBar;
    private Thread blobDownloadThread;
    private Thread blobOpenThread;
    private String currentBlobContainerName;
    private AzureStorageAccount currentAzureStorageAccount;

    private Handler handler = new Handler();

    public BlobListFragment() {
        if (BLOB_ACTIONS.size() == 0) {
            BLOB_ACTIONS.add("Open");
            BLOB_ACTIONS.add("Properties");
            BLOB_ACTIONS.add("Download");
            BLOB_ACTIONS.add("Delete forever");

            BLOB_ACTIONS_ICONS.add(R.drawable.ic_view);
            BLOB_ACTIONS_ICONS.add(R.drawable.ic_info_outline);
            BLOB_ACTIONS_ICONS.add(R.drawable.ic_download);
            BLOB_ACTIONS_ICONS.add(R.drawable.ic_delete_forever);
        }
    }

    public static BlobListFragment newInstance() {
        return new BlobListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View root = layoutInflater.inflate(R.layout.blob_list_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.blobListRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new BlobRecyclerViewAdapter(null, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        progressBar = (ProgressBar) root.findViewById(R.id.blobListProgressBar);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.add_blob);
        if (fab != null) {
            fab.setImageResource(R.drawable.ic_add);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
                    // browser.
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                    // Filter to only show results that can be "opened", such as a
                    // file (as opposed to a list of contacts or timezones)
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");

                    startActivityForResult(intent, INTENT_OPEN_DOCUMENT_REQUEST_CODE);
                }
            });
        }

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
    public void selectionChanged(AzureStorageAccount account, CloudBlobContainerSerializable container) {
        currentBlobContainerName = container.getName();
        currentAzureStorageAccount = account;

        Snackbar.make(this.getCoordinatorLayout(), currentBlobContainerName, Snackbar.LENGTH_SHORT).show();

        showProgressBar();
        //get the blobs for this container
        BlobListAsyncTask blobListAsyncTask = new BlobListAsyncTask(this);
        blobListAsyncTask.execute(account.getName(), account.getKey(), container.getName());
    }

    @Override
    public void finished(Object result) {
        hideProgressBar();
        if (result instanceof ArrayList) {
            recyclerViewAdapter.replaceDataset((ArrayList<ListBlobItem>) result);
        } else if (result instanceof Boolean) {
            showSnackbar((Boolean) result ? getString(R.string.blob_created) : getString(R.string.failed_to_create_blob));
        }
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
        if (currentBlobDirectory != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentBlobDirectory.getPrefix());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (currentBlobDirectory != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentBlobDirectory.getPrefix());
        }
    }

    @Override
    public void onClick(View view, int adapterPosition, ListBlobItem item) {
        //if the menu overflow icon was clicked, show the popup
        if (view.getId() == R.id.layout2) {
            currentlySelectedBlobItemAdapterPosition = adapterPosition;
            showPopup(view);
            return;
        }

        //only tell the parent listener if it is a folder..handle other clicks within
        if ((item instanceof CloudBlobDirectory)) {
            ((OnFragmentInteractionListener) getActivity()).onBlobItemClicked(item);
        }
    }

    private void showPopup(View view) {
        if (listPopupWindow == null) {
            listPopupWindow = new ListPopupWindow(getContext());
            listPopupWindow.setModal(false);
            listPopupWindow.setOnItemClickListener(this);
            listPopupWindow.setWidth(350);
            listPopupWindow.setAdapter(new BlobActionsPopupWindowArrayAdapter(getContext(), android.R.layout.simple_list_item_1, BLOB_ACTIONS, BLOB_ACTIONS_ICONS));
            listPopupWindow.setDropDownGravity(Gravity.START);
        }

        listPopupWindow.setAnchorView(view);
        listPopupWindow.show();
    }

    @Override
    public void onBlobItemClick(AzureStorageAccount account, ListBlobItem listBlobItem) {
        try {
            CloudBlobDirectory cloudBlobDirectory = (CloudBlobDirectory) listBlobItem;
            currentBlobDirectory = new CloudBlobDirectorySerializable(cloudBlobDirectory.getContainer().getName(), cloudBlobDirectory.getPrefix());
            //get the blobs for this blob directory
            BlobListAsyncTask blobListAsyncTask = new BlobListAsyncTask(this);
            blobListAsyncTask.execute(account.getName(), account.getKey(), currentBlobDirectory.getContainerName(), currentBlobDirectory.getPrefix());
        } catch (StorageException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void showSnackbar(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(BlobListFragment.this.getCoordinatorLayout(), message, Snackbar.LENGTH_SHORT).show();
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
            deletionConfirmed();
        }
    }

    private void deletionConfirmed() {
        final CloudBlob cloudBlob = (CloudBlob) recyclerViewAdapter.getDataset().get(currentlySelectedBlobItemAdapterPosition);
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
    public void onConfirmationDialogNegativeClick(int requestCode) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listPopupWindow.dismiss();
        final CloudBlob cloudBlob = (CloudBlob) recyclerViewAdapter.getDataset().get(currentlySelectedBlobItemAdapterPosition);

        switch ((int) view.getTag()) {
            case R.drawable.ic_info_outline: {
                BlobInfoDialogFragment fragment = new BlobInfoDialogFragment();
                Bundle args = new Bundle();
                fragment.setArguments(Helpers.getBlobInfoFromListBlobItem(args, cloudBlob));
                fragment.show(getActivity().getSupportFragmentManager(), BlobInfoDialogFragment.class.getName());
            }
                break;
            case R.drawable.ic_delete_forever: {
                Bundle args = new Bundle();
                args.putString("title", getString(R.string.delete_blob_title));
                args.putString("message", getString(R.string.delete_blob_confirmation_message));
                ConfirmationDialogFragment deleteConfirmation = new ConfirmationDialogFragment();
                deleteConfirmation.setArguments(args);
                deleteConfirmation.setTargetFragment(this, DELETE_REQUEST_CODE);
                deleteConfirmation.show(getActivity().getSupportFragmentManager(), ConfirmationDialogFragment.class.getName());
            }
                break;
            case R.drawable.ic_download:
                //download the blob
                final Context context = getContext();
                blobDownloadThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //show the progress bar
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showProgressBar();
                            }
                        });

                        try {
                            final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                            File file = new File(path);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            String fileName = cloudBlob.getName().replace("/", "_");
                            File blobFile = new File(file.getAbsolutePath(), fileName);
                            if (!blobFile.exists()) {
                                blobFile.createNewFile();
                            }
                            cloudBlob.downloadToFile(blobFile.getAbsolutePath());
                            //hide the progress bar
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressBar();
                                }
                            });

                            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                            downloadManager.addCompletedDownload(
                                    fileName,
                                    cloudBlob.getName(),
                                    true,
                                    Helpers.getMimeType(blobFile),
                                    blobFile.getAbsolutePath(),
                                    blobFile.length(),
                                    true);
                        } catch (Exception e) {
                            showToast(e.getMessage());
                        }
                    }
                });

                //if we don't already have permissions, request it and start the thread after the permissions are granted for external storage
                if (checkForStoragePermissions(READ_WRITE_EXTERNAL_STORAGE_FOR_BLOB_DOWNLOAD_PERMISSION_REQUEST_CODE)) {
                    blobDownloadThread.start();
                }
                break;
            case R.drawable.ic_view:
                blobOpenThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //show the progress bar
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showProgressBar();
                            }
                        });
                        try {
                            final String path = getContext().getExternalCacheDir().getAbsolutePath();
                            File cachePath = new File(path, "cache");
                            if (!cachePath.exists()) {
                                cachePath.mkdirs();
                            }
                            String fileName = cloudBlob.getName().replace("/", "_");
                            File blobFile = new File(cachePath.getAbsolutePath(), fileName);
                            if (!blobFile.exists()) {
                                blobFile.createNewFile();
                            }
                            cloudBlob.downloadToFile(blobFile.getAbsolutePath());
                            //hide the progress bar
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressBar();
                                }
                            });

                            String mimeType = Helpers.getMimeType(blobFile);
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Uri contentUri = FileProvider.getUriForFile(getContext(), "com.myappfactory.fileprovider", blobFile);
                                intent.setDataAndType(contentUri, mimeType);
                            } else {
                                intent.setDataAndType(Uri.fromFile(blobFile), mimeType);
                            }

                            startActivity(intent);
                        } catch (Exception e) {
                            showToast(e.getMessage());
                        }
                    }
                });

                //if we don't already have permissions, request it and start the thread after the permissions are granted for external storage
                if (checkForStoragePermissions(READ_WRITE_EXTERNAL_STORAGE_FOR_BLOB_OPEN_PERMISSION_REQUEST_CODE)) {
                    blobOpenThread.start();
                }
                break;
            default:
                break;
        }
    }

    private boolean checkForStoragePermissions(int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_WRITE_EXTERNAL_STORAGE_FOR_BLOB_DOWNLOAD_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (blobDownloadThread != null) {
                        blobDownloadThread.start();
                    }
                } else {

                }
                return;
            }
            case READ_WRITE_EXTERNAL_STORAGE_FOR_BLOB_OPEN_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (blobOpenThread != null) {
                        blobOpenThread.start();
                    }
                } else {

                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == INTENT_OPEN_DOCUMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                BlobToUpload blobToUpload = getDocumentMetadata(uri);
                if (blobToUpload.getContentLength() <= 0) {
                    return;
                }
                showProgressBar();
                final UploadBlobAsyncTask uploadBlobAsyncTask = new UploadBlobAsyncTask(this, blobToUpload);
                String blobDirectory = null;
                if (currentBlobDirectory != null) {
                    blobDirectory = currentBlobDirectory.getPrefix();
                }
                uploadBlobAsyncTask.execute(currentAzureStorageAccount.getName(), currentAzureStorageAccount.getKey(), currentBlobContainerName, blobDirectory);
            }
        }
    }

    private BlobToUpload getDocumentMetadata(Uri uri) {
        BlobToUpload blobToUpload = new BlobToUpload();
        blobToUpload.setUri(uri);
        Cursor cursor = getActivity().getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an
                // int can't be null in Java, the behavior is implementation-specific,
                // which is just a fancy term for "unpredictable".  So as
                // a rule, check if it's null before assigning to an int.  This will
                // happen often:  The storage API allows for remote files, whose
                // size might not be locally known.
                long size = 0;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = Long.parseLong(cursor.getString(sizeIndex));
                }

                blobToUpload.setContentLength(size);
                blobToUpload.setFileName(cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                blobToUpload.setMimeType(cursor.getString(
                        cursor.getColumnIndex("mime_type")));
            } else {
                ContentResolver contentResolver = getActivity().getContentResolver();
                String mimeType = contentResolver.getType(uri);
                blobToUpload.setMimeType(mimeType);
                File file = new File(uri.getPath());
                blobToUpload.setContentLength(file.length());
                blobToUpload.setFileName(file.getName());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return blobToUpload;
    }

    @Override
    public View getCoordinatorLayout() {
        return getView().findViewById(R.id.blobListCoordinatorLayout);
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
