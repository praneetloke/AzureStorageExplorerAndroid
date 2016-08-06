package com.pl.azurestorageexplorer.asynctask;

import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.EntityResolver;
import com.microsoft.azure.storage.table.TableEntity;
import com.microsoft.azure.storage.table.TableQuery;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallbackWithResultContinuation;
import com.pl.azurestorageexplorer.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Praneet Loke on 4/18/2016.
 */
public class TableEntitiesAsyncTask extends AsyncTask<String, Void, ArrayList<HashMap<String, EntityProperty>>> {
    private IAsyncTaskCallbackWithResultContinuation callback;
    private String exceptionMessage;
    private ResultContinuation resultContinuation;
    private boolean hasMoreResults;

    public TableEntitiesAsyncTask(IAsyncTaskCallbackWithResultContinuation<ArrayList<HashMap<String, EntityProperty>>> callback, ResultContinuation resultContinuation) {
        this.callback = callback;
        this.resultContinuation = resultContinuation;
    }

    @Override
    protected ArrayList<HashMap<String, EntityProperty>> doInBackground(String... params) {
        String storageUrl = String.format(Constants.STORAGE_ACCOUNT_BLOB_URL_FORMAT, params[0], params[1]);
        ArrayList<HashMap<String, EntityProperty>> tableEntities = null;

        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageUrl);

            CloudTableClient cloudTableClient = storageAccount.createCloudTableClient();
            CloudTable cloudTable = cloudTableClient.getTableReference(params[2]);
            tableEntities = new ArrayList<>();

            // Define a Entity resolver to project the entity to the Email value.
            EntityResolver<HashMap<String, EntityProperty>> entityResolver = new EntityResolver<HashMap<String, EntityProperty>>() {
                @Override
                public HashMap<String, EntityProperty> resolve(String PartitionKey, String RowKey, Date timeStamp, HashMap<String, EntityProperty> properties, String etag) {
                    properties.put("PartitionKey", new EntityProperty(PartitionKey));
                    properties.put("RowKey", new EntityProperty(RowKey));
                    return properties;
                }
            };
            ResultSegment<HashMap<String, EntityProperty>> tableEntityResultSegment = cloudTable.executeSegmented(new TableQuery<TableEntity>(), entityResolver, resultContinuation);
            tableEntities = tableEntityResultSegment.getResults();
            resultContinuation = tableEntityResultSegment.getContinuationToken();
            hasMoreResults = tableEntityResultSegment.getHasMoreResults();
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
        return tableEntities;
    }

    protected void onPostExecute(ArrayList<HashMap<String, EntityProperty>> tableEntities) {
        if (exceptionMessage != null) {
            this.callback.failed(exceptionMessage);
            return;
        }

        this.callback.finished(tableEntities, resultContinuation, hasMoreResults);
    }
}
