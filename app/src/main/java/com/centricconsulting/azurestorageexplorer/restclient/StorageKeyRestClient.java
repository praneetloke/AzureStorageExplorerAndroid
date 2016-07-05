package com.centricconsulting.azurestorageexplorer.restclient;

import android.content.ContentValues;
import android.util.Xml;

import com.centricconsulting.azurestorageexplorer.AzureStorageExplorerApplication;
import com.centricconsulting.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.centricconsulting.azurestorageexplorer.models.StorageService;
import com.centricconsulting.azurestorageexplorer.parser.XmlToPojo;
import com.centricconsulting.azurestorageexplorer.storage.AzureStorageAccountSQLiteHelper;
import com.centricconsulting.azurestorageexplorer.storage.models.AzureStorageAccount;
import com.centricconsulting.azurestorageexplorer.util.Constants;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Praneet Loke on 7/4/2016.
 */
public class StorageKeyRestClient implements Runnable {
    private List<StorageService> mStorageServices;
    private String mSubscriptionId;
    private IAsyncTaskCallback<AzureStorageAccount> mCallback;

    public StorageKeyRestClient(List<StorageService> storageServices, String subscriptionID, IAsyncTaskCallback<AzureStorageAccount> callback) {
        mStorageServices = storageServices;
        mSubscriptionId = subscriptionID;
        mCallback = callback;
    }

    @Override
    public void run() {
        for (StorageService storageService : mStorageServices) {
            Request request = new Request.Builder()
                    .url(String.format(Constants.AZURE_GET_STORAGE_KEYS,
                            mSubscriptionId,
                            storageService.getServiceName()))
                    .addHeader("Authorization", String.format("Bearer %s", AzureStorageExplorerApplication.accessToken))
                    .addHeader("x-ms-version", Constants.AZURE_API_VERSION)
                    .addHeader("storageServiceName", storageService.getServiceName())
                    .build();

            Call storageKeysCall = AzureStorageExplorerApplication.mOkHttpClient.newCall(request);
            storageKeysCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mCallback.failed(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final StorageService storageAccountKeys = XmlToPojo.parseItem(StorageService.class, response.body().byteStream(), Xml.Encoding.UTF_8);

                    final String name = call.request().header("storageServiceName");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(AzureStorageAccountSQLiteHelper.NAME, name);
                    contentValues.put(AzureStorageAccountSQLiteHelper.KEY, storageAccountKeys.getStorageServiceKeys().getPrimary());
                    contentValues.put(AzureStorageAccountSQLiteHelper.SUBSCRIPTION_ID, mSubscriptionId);
                    long insertedId = AzureStorageExplorerApplication
                            .getAzureStorageAccountSQLiteHelper()
                            .getWritableDatabase()
                            .insert(AzureStorageAccountSQLiteHelper.TABLE_NAME, null, contentValues);

                    final AzureStorageAccount storageAccount = new AzureStorageAccount(insertedId,
                            name, storageAccountKeys.getStorageServiceKeys().getPrimary(), mSubscriptionId);
                    //this will refresh the data set for the spinner in the navigation view
                    mCallback.finished(storageAccount);
                }
            });
        }
    }
}
