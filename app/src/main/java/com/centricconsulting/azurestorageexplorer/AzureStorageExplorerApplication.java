package com.centricconsulting.azurestorageexplorer;

import android.app.Application;

import com.centricconsulting.azurestorageexplorer.storage.AzureStorageAccountSQLiteHelper;

/**
 * Created by v-prloke on 4/9/2016.
 */
public class AzureStorageExplorerApplication extends Application {

    public static final String DATABASE_NAME = "AzureStorageExplorer.db";

    private static AzureStorageAccountSQLiteHelper azureStorageAccountSQLiteHelper;

    public static AzureStorageAccountSQLiteHelper getAzureStorageAccountSQLiteHelper() {
        return azureStorageAccountSQLiteHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        azureStorageAccountSQLiteHelper = new AzureStorageAccountSQLiteHelper(getApplicationContext());
        azureStorageAccountSQLiteHelper.getWritableDatabase();
        azureStorageAccountSQLiteHelper.getReadableDatabase();
    }
}
