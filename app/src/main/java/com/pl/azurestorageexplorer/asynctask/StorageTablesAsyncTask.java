package com.pl.azurestorageexplorer.asynctask;

import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.pl.azurestorageexplorer.models.StorageTableSerializable;
import com.pl.azurestorageexplorer.util.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class StorageTablesAsyncTask extends AsyncTask<String, Void, ArrayList<StorageTableSerializable>> {
    private WeakReference<IAsyncTaskCallback> callback;
    private String exceptionMessage;

    public StorageTablesAsyncTask(IAsyncTaskCallback<ArrayList<?>> callback) {
        this.callback = new WeakReference<IAsyncTaskCallback>(callback);
    }

    @Override
    protected ArrayList<StorageTableSerializable> doInBackground(String... params) {
        String storageUrl = String.format(Constants.STORAGE_ACCOUNT_URL_FORMAT, params[0], params[1]);
        ArrayList<StorageTableSerializable> cloudTables = null;

        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageUrl);

            // Create the table client.
            CloudTableClient cloudTableClient = storageAccount.createCloudTableClient();
            cloudTables = new ArrayList<>();

            for (String table : cloudTableClient.listTables()) {
                cloudTables.add(new StorageTableSerializable(table));
            }
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
        return cloudTables;
    }

    protected void onPostExecute(ArrayList<StorageTableSerializable> tables) {
        if (this.callback.get() == null) {
            return;
        }

        if (exceptionMessage != null) {
            this.callback.get().failed(exceptionMessage);
            return;
        }

        this.callback.get().finished(tables);
    }
}
