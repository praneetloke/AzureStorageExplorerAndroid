package com.pl.azurestorageexplorer.asynctask;

import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.EntityResolver;
import com.microsoft.azure.storage.table.TableEntity;
import com.microsoft.azure.storage.table.TableQuery;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.pl.azurestorageexplorer.util.Constants;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Praneet Loke on 4/18/2016.
 */
public class TableEntityAsyncTask extends AsyncTask<String, Void, HashMap<String, EntityProperty>> {
    private WeakReference<IAsyncTaskCallback> callback;
    private String exceptionMessage;

    public TableEntityAsyncTask(IAsyncTaskCallback<HashMap<String, EntityProperty>> callback) {
        this.callback = new WeakReference<IAsyncTaskCallback>(callback);
    }

    @Override
    protected HashMap<String, EntityProperty> doInBackground(String... params) {
        String storageUrl = String.format(Constants.STORAGE_ACCOUNT_BLOB_URL_FORMAT, params[0], params[1]);
        HashMap<String, EntityProperty> tableEntities = null;

        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageUrl);

            CloudTableClient cloudTableClient = storageAccount.createCloudTableClient();
            CloudTable cloudTable = cloudTableClient.getTableReference(params[2]);
            tableEntities = new HashMap<String, EntityProperty>();

            // Define a Entity resolver to project the entity to the Email value.
            EntityResolver<HashMap<String, EntityProperty>> entityResolver = new EntityResolver<HashMap<String, EntityProperty>>() {
                @Override
                public HashMap<String, EntityProperty> resolve(String PartitionKey, String RowKey, Date timeStamp, HashMap<String, EntityProperty> properties, String etag) {
                    return properties;
                }
            };
            TableQuery<TableEntity> tableQuery = new TableQuery<>();
            tableQuery.where(
                    TableQuery.combineFilters(
                            TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, params[3]),
                            TableQuery.Operators.AND,
                            TableQuery.generateFilterCondition("RowKey", TableQuery.QueryComparisons.EQUAL, params[4])
                    )
            );
            ResultSegment<HashMap<String, EntityProperty>> tableEntityResultSegment = cloudTable.executeSegmented(tableQuery, entityResolver, null);
            tableEntities = tableEntityResultSegment.getResults().get(0);
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
        return tableEntities;
    }

    protected void onPostExecute(HashMap<String, EntityProperty> tableEntity) {
        if (this.callback.get() == null) {
            return;
        }

        if (exceptionMessage != null) {
            this.callback.get().failed(exceptionMessage);
            return;
        }

        this.callback.get().finished(tableEntity);
    }
}
