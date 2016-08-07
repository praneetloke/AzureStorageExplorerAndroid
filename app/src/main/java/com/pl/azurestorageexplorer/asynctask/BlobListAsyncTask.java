package com.pl.azurestorageexplorer.asynctask;

import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.pl.azurestorageexplorer.util.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/18/2016.
 */
public class BlobListAsyncTask extends AsyncTask<String, Void, ArrayList<ListBlobItem>> {
    private WeakReference<IAsyncTaskCallback> callback;
    private String exceptionMessage;

    public BlobListAsyncTask(IAsyncTaskCallback<ArrayList<ListBlobItem>> callback) {
        this.callback = new WeakReference<IAsyncTaskCallback>(callback);
    }

    @Override
    protected ArrayList<ListBlobItem> doInBackground(String... params) {
        String storageUrl = String.format(Constants.STORAGE_ACCOUNT_BLOB_URL_FORMAT, params[0], params[1]);
        ArrayList<ListBlobItem> blobs = null;

        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageUrl);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Retrieve reference to a previously created container.
            CloudBlobContainer container = blobClient.getContainerReference(params[2]);

            blobs = new ArrayList<>();

            //if only 3 args were sent, that means we are at the top-level
            if (params.length == 3) {
                // Loop through each blob item in the container.
                for (ListBlobItem blobItem : container.listBlobs()) {
                    blobs.add(blobItem);
                }
            } else {
                // Loop through each blob item in the blob prefix.
                for (ListBlobItem blobItem : container.listBlobs(params[3])) {
                    blobs.add(blobItem);
                }
            }

        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
        return blobs;
    }

    protected void onPostExecute(ArrayList<ListBlobItem> blobs) {
        if (this.callback.get() == null) {
            return;
        }

        if (exceptionMessage != null) {
            this.callback.get().failed(exceptionMessage);
            return;
        }

        this.callback.get().finished(blobs);
    }
}
