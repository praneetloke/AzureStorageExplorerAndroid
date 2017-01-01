package com.pl.azurestorageexplorer.asynctask;

import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableEntity;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TablePayloadFormat;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableRequestOptions;
import com.microsoft.azure.storage.table.TableResult;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.pl.azurestorageexplorer.util.Constants;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by Praneet Loke on 4/18/2016.
 */
public class DeleteTableEntityAsyncTask extends AsyncTask<String, Void, Boolean> {
    private WeakReference<IAsyncTaskCallback> callback;
    private String exceptionMessage;

    public DeleteTableEntityAsyncTask(IAsyncTaskCallback<Boolean> callback) {
        this.callback = new WeakReference<IAsyncTaskCallback>(callback);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String storageUrl = String.format(Constants.STORAGE_ACCOUNT_URL_FORMAT, params[0], params[1]);
        boolean result = false;
        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageUrl);

            CloudTableClient cloudTableClient = storageAccount.createCloudTableClient();
            CloudTable cloudTable = cloudTableClient.getTableReference(params[2]);

            DynamicTableEntity entityToDelete = cloudTable.execute(TableOperation.retrieve(params[3], params[4], DynamicTableEntity.class)).getResultAsType();
            TableResult tableResult = cloudTable.execute(TableOperation.delete(entityToDelete));
            result = true;

        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
        return result;
    }

    protected void onPostExecute(Boolean result) {
        if (this.callback.get() == null) {
            return;
        }

        if (exceptionMessage != null) {
            this.callback.get().failed(exceptionMessage);
            return;
        }

        this.callback.get().finished(result);
    }
}
