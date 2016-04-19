package com.centricconsulting.azurestorageexplorer.asynctask;

import android.os.AsyncTask;

import com.centricconsulting.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.centricconsulting.azurestorageexplorer.util.Constants;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class BlobContainerListAsyncTask extends AsyncTask<String, Void, ArrayList<CloudBlobContainer>> {
    private IAsyncTaskCallback callback;
    private String exceptionMessage;

    public BlobContainerListAsyncTask(IAsyncTaskCallback<ArrayList<CloudBlobContainer>> callback) {
        this.callback = callback;
    }

    @Override
    protected ArrayList<CloudBlobContainer> doInBackground(String... params) {
        String storageUrl = String.format(Constants.STORAGE_ACCOUNT_BLOB_URL_FORMAT, params[0], params[1]);
        ArrayList<CloudBlobContainer> containers = null;

        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageUrl);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            containers = new ArrayList<>();

            for (CloudBlobContainer container : blobClient.listContainers()) {
                containers.add(container);
            }
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
        return containers;
    }

    protected void onPostExecute(ArrayList<CloudBlobContainer> containers) {
        if (exceptionMessage != null) {
            this.callback.failed(exceptionMessage);
            return;
        }

        this.callback.finished(containers);
    }
}
