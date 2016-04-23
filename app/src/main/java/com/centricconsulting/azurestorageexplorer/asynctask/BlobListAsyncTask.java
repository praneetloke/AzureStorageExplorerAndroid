package com.centricconsulting.azurestorageexplorer.asynctask;

import android.os.AsyncTask;

import com.centricconsulting.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.centricconsulting.azurestorageexplorer.util.Constants;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/18/2016.
 */
public class BlobListAsyncTask extends AsyncTask<String, Void, ArrayList<ListBlobItem>> {
    private IAsyncTaskCallback callback;
    private String exceptionMessage;

    public BlobListAsyncTask(IAsyncTaskCallback<ArrayList<ListBlobItem>> callback) {
        this.callback = callback;
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
        if (exceptionMessage != null) {
            this.callback.failed(exceptionMessage);
            return;
        }

        this.callback.finished(blobs);
    }
}
