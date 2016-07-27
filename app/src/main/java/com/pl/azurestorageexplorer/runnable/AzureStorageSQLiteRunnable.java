package com.pl.azurestorageexplorer.runnable;

import android.content.ContentValues;

import com.pl.azurestorageexplorer.AzureStorageExplorerApplication;
import com.pl.azurestorageexplorer.storage.AzureStorageAccountSQLiteHelper;
import com.pl.azurestorageexplorer.storage.models.AzureStorageAccount;

import java.util.List;

/**
 * Table to hold all the storage accounts by subscriptionId.
 */
public class AzureStorageSQLiteRunnable implements Runnable {
    private List<AzureStorageAccount> mStorageAccounts;

    public AzureStorageSQLiteRunnable(List<AzureStorageAccount> storageAccounts) {
        mStorageAccounts = storageAccounts;
    }

    @Override
    public void run() {
        AzureStorageExplorerApplication
                .getCustomSQLiteHelper()
                .getWritableDatabase()
                .beginTransaction();

        for (AzureStorageAccount storageAccount : mStorageAccounts) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AzureStorageAccountSQLiteHelper.NAME, storageAccount.getName());
            contentValues.put(AzureStorageAccountSQLiteHelper.KEY, storageAccount.getKey());
            contentValues.put(AzureStorageAccountSQLiteHelper.SUBSCRIPTION_ID, storageAccount.getSubscriptionId());
            AzureStorageExplorerApplication
                    .getCustomSQLiteHelper()
                    .getWritableDatabase()
                    .insert(AzureStorageAccountSQLiteHelper.TABLE_NAME, null, contentValues);
        }

        AzureStorageExplorerApplication
                .getCustomSQLiteHelper()
                .getWritableDatabase()
                .endTransaction();
    }
}
