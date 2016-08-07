package com.pl.azurestorageexplorer.asynctask;

import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.ResultContinuation;
import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.EntityResolver;
import com.microsoft.azure.storage.table.TableQuery;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallbackWithResultContinuation;
import com.pl.azurestorageexplorer.util.Constants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Praneet Loke on 4/18/2016.
 */
public class TableEntitiesAsyncTask extends AsyncTask<String, Void, ArrayList<DynamicTableEntity>> {
    private WeakReference<IAsyncTaskCallbackWithResultContinuation> callback;
    private String exceptionMessage;
    private ResultContinuation resultContinuation;
    private boolean hasMoreResults;

    public TableEntitiesAsyncTask(IAsyncTaskCallbackWithResultContinuation<ArrayList<DynamicTableEntity>> callback, ResultContinuation resultContinuation) {
        this.callback = new WeakReference<IAsyncTaskCallbackWithResultContinuation>(callback);
        this.resultContinuation = resultContinuation;
    }

    @Override
    protected ArrayList<DynamicTableEntity> doInBackground(String... params) {
        String storageUrl = String.format(Constants.STORAGE_ACCOUNT_BLOB_URL_FORMAT, params[0], params[1]);
        ArrayList<DynamicTableEntity> tableEntities = null;

        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageUrl);

            CloudTableClient cloudTableClient = storageAccount.createCloudTableClient();
            CloudTable cloudTable = cloudTableClient.getTableReference(params[2]);
            tableEntities = new ArrayList<>();

            // Define a Entity resolver to project the entity to the Email value.
            EntityResolver<DynamicTableEntity> entityResolver = new EntityResolver<DynamicTableEntity>() {
                @Override
                public DynamicTableEntity resolve(String PartitionKey, String RowKey, Date timeStamp, HashMap<String, EntityProperty> properties, String etag) {
                    return new DynamicTableEntity(PartitionKey, RowKey);
                }
            };
            TableQuery<DynamicTableEntity> tableQuery = new TableQuery<>();
            tableQuery.select(new String[]{"PartitionKey", "RowKey"});
            ResultSegment<DynamicTableEntity> tableEntityResultSegment = cloudTable.executeSegmented(tableQuery, entityResolver, resultContinuation);
            tableEntities = tableEntityResultSegment.getResults();
            resultContinuation = tableEntityResultSegment.getContinuationToken();
            hasMoreResults = tableEntityResultSegment.getHasMoreResults();
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }
        return tableEntities;
    }

    protected void onPostExecute(ArrayList<DynamicTableEntity> tableEntities) {
        if (this.callback.get() == null) {
            return;
        }

        if (exceptionMessage != null) {
            this.callback.get().failed(exceptionMessage);
            return;
        }

        this.callback.get().finished(tableEntities, resultContinuation, hasMoreResults);
    }
}
