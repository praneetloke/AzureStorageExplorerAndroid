package com.pl.azurestorageexplorer.restclient;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.pl.azurestorageexplorer.AzureStorageExplorerApplication;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.pl.azurestorageexplorer.models.ARMStorageKeys;
import com.pl.azurestorageexplorer.models.ARMStorageService;
import com.pl.azurestorageexplorer.models.ARMStorageServices;
import com.pl.azurestorageexplorer.storage.AzureStorageAccountSQLiteHelper;
import com.pl.azurestorageexplorer.storage.models.AzureStorageAccount;
import com.pl.azurestorageexplorer.util.Constants;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by Praneet Loke on 7/4/2016.
 */
public class StorageKeyRestClient implements Runnable {
    private ARMStorageServices storageServices;
    private String subscriptionId;
    private IAsyncTaskCallback<AzureStorageAccount> callback;

    public StorageKeyRestClient(ARMStorageServices storageServices, String subscriptionID, IAsyncTaskCallback<AzureStorageAccount> callback) {
        this.storageServices = storageServices;
        subscriptionId = subscriptionID;
        this.callback = callback;
    }

    @Override
    public void run() {
        for (final ARMStorageService storageService : storageServices.getValue()) {
            final String resourceGroupName = storageService.getId().substring(storageService.getId().indexOf("/resourceGroups/") + 16, storageService.getId().indexOf("/providers/"));
            Request request = new Request.Builder()
                    .url(String.format("%s/%s/%s", Constants.AZURE_RESOURCE_MANAGER, storageService.getId(), Constants.AZURE_GET_STORAGE_KEYS))
                    .addHeader("Authorization", String.format("Bearer %s", AzureStorageExplorerApplication.accessToken.get(Constants.AZURE_AUTHORIZE_URL)))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("storageServiceName", storageService.getName())
                    .post(new RequestBody() {
                        @Override
                        public MediaType contentType() {
                            return null;
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {

                        }
                    })
                    .build();

            Call storageKeysCall = AzureStorageExplorerApplication.mOkHttpClient.newCall(request);
            storageKeysCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.failed(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final Gson gson = new Gson();
                    final ARMStorageKeys storageKeys = gson.fromJson(response.body().charStream(), ARMStorageKeys.class);
                    final String name = call.request().header("storageServiceName");

                    if (storageKeys == null || storageKeys.getKeys() == null || storageKeys.getKeys().size() == 0) {
                        callback.failed(String.format("Did not get the keys for %s", name));
                        return;
                    }

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(AzureStorageAccountSQLiteHelper.NAME, name);
                    contentValues.put(AzureStorageAccountSQLiteHelper.KEY, storageKeys.getKeys().get(0).getValue());
                    contentValues.put(AzureStorageAccountSQLiteHelper.SUBSCRIPTION_ID, subscriptionId);
                    contentValues.put(AzureStorageAccountSQLiteHelper.RESOURCE_GROUP_NAME, resourceGroupName);

                    long insertedId = AzureStorageExplorerApplication
                            .getCustomSQLiteHelper()
                            .getWritableDatabase()
                            .insert(AzureStorageAccountSQLiteHelper.TABLE_NAME, null, contentValues);

                    final AzureStorageAccount storageAccount = new AzureStorageAccount(insertedId,
                            name, storageKeys.getKeys().get(0).getValue(), subscriptionId, resourceGroupName);
                    //this will refresh the data set for the spinner in the navigation view
                    callback.finished(storageAccount);
                }
            });
        }
    }
}
