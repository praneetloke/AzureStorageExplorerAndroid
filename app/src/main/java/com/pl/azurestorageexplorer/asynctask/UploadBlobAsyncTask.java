package com.pl.azurestorageexplorer.asynctask;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.pl.azurestorageexplorer.models.BlobToUpload;
import com.pl.azurestorageexplorer.util.Constants;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Praneet Loke on 4/18/2016.
 */
public class UploadBlobAsyncTask extends AsyncTask<String, Void, Boolean> {
    private WeakReference<IAsyncTaskCallback> callback;
    private String exceptionMessage;
    private BlobToUpload blobToUpload;

    public UploadBlobAsyncTask(IAsyncTaskCallback callback, BlobToUpload blobToUpload) {
        this.callback = new WeakReference<IAsyncTaskCallback>(callback);
        this.blobToUpload = blobToUpload;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String storageUrl = String.format(Constants.STORAGE_ACCOUNT_BLOB_URL_FORMAT, params[0], params[1]);
        boolean uploaded = false;

        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageUrl);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Retrieve reference to a previously created container.
            CloudBlobContainer container = blobClient.getContainerReference(params[2]);

            CloudBlockBlob blob;
            if (params.length > 3 && params[3] != null) {
                CloudBlobDirectory directory = container.getDirectoryReference(params[3]);
                blob = directory.getBlockBlobReference(this.blobToUpload.getFileName());
            } else {
                blob = container.getBlockBlobReference(this.blobToUpload.getFileName());
            }

            if (callback.get() != null) {
                InputStream inputStream = ((Fragment) callback.get()).getActivity().getContentResolver().openInputStream(this.blobToUpload.getUri());
                blob.upload(inputStream, this.blobToUpload.getContentLength());
                uploaded = true;
            }

        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
        return uploaded;
    }

    protected void onPostExecute(Boolean uploaded) {
        if (this.callback.get() == null) {
            return;
        }

        if (exceptionMessage != null) {
            this.callback.get().failed(exceptionMessage);
            return;
        }

        this.callback.get().finished(uploaded);
    }
}
