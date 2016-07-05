package com.centricconsulting.azurestorageexplorer.runnable;

import android.content.ContentValues;

import com.centricconsulting.azurestorageexplorer.AzureStorageExplorerApplication;
import com.centricconsulting.azurestorageexplorer.storage.AzureStorageAccountSQLiteHelper;
import com.centricconsulting.azurestorageexplorer.storage.models.AzureStorageAccount;

import java.util.List;

/**
 * Created by Praneet Loke on 7/4/2016.
 */
public class AzureStorageSQLiteRunnable implements Runnable {
    private List<AzureStorageAccount> mStorageAccounts;

    public AzureStorageSQLiteRunnable(List<AzureStorageAccount> storageAccounts) {
        mStorageAccounts = storageAccounts;
    }

    @Override
    public void run() {
        AzureStorageExplorerApplication
                .getAzureStorageAccountSQLiteHelper()
                .getWritableDatabase()
                .beginTransaction();

        for (AzureStorageAccount storageAccount : mStorageAccounts) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AzureStorageAccountSQLiteHelper.NAME, storageAccount.getName());
            contentValues.put(AzureStorageAccountSQLiteHelper.KEY, storageAccount.getKey());
            contentValues.put(AzureStorageAccountSQLiteHelper.SUBSCRIPTION_ID, storageAccount.getSubscriptionId());
            AzureStorageExplorerApplication
                    .getAzureStorageAccountSQLiteHelper()
                    .getWritableDatabase()
                    .insert(AzureStorageAccountSQLiteHelper.TABLE_NAME, null, contentValues);
        }

        AzureStorageExplorerApplication
                .getAzureStorageAccountSQLiteHelper()
                .getWritableDatabase()
                .endTransaction();
    }
}
