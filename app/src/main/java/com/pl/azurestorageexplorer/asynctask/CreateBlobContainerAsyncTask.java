package com.pl.azurestorageexplorer.asynctask;

import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.pl.azurestorageexplorer.util.Constants;

import java.lang.ref.WeakReference;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class CreateBlobContainerAsyncTask extends AsyncTask<String, Void, String> {
    private WeakReference<IAsyncTaskCallback<String>> callback;
    private String exceptionMessage;

    public CreateBlobContainerAsyncTask(IAsyncTaskCallback<String> callback) {
        this.callback = new WeakReference<>(callback);
    }

    @Override
    protected String doInBackground(String... params) {
        String storageUrl = String.format(Constants.STORAGE_ACCOUNT_BLOB_URL_FORMAT, params[0], params[1]);
        boolean created = false;
        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageUrl);

            // Create the blob client.
            created = storageAccount.createCloudBlobClient().getContainerReference(params[2]).createIfNotExists();
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }

        return created ? params[2] : null;
    }

    protected void onPostExecute(String containerName) {
        if (this.callback.get() == null) {
            return;
        }

        if (exceptionMessage != null || containerName == null) {
            this.callback.get().failed(exceptionMessage);
            return;
        }

        this.callback.get().finished(containerName);
    }
}
