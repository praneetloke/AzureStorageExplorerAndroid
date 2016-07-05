package com.centricconsulting.azurestorageexplorer;

import android.app.Application;

import com.centricconsulting.azurestorageexplorer.storage.AzureStorageAccountSQLiteHelper;
import com.centricconsulting.azurestorageexplorer.storage.AzureSubscriptionsSQLiteHelper;

import okhttp3.OkHttpClient;

/**
 * Created by Praneet Loke on 4/9/2016.
 */
public class AzureStorageExplorerApplication extends Application {

    public static final String DATABASE_NAME = "AzureStorageExplorer.db";
    public static OkHttpClient mOkHttpClient = new OkHttpClient();
    public static String accessToken = "";
    private static AzureStorageAccountSQLiteHelper azureStorageAccountSQLiteHelper;
    private static AzureSubscriptionsSQLiteHelper azureSubscriptionsSQLiteHelper;

    public static AzureStorageAccountSQLiteHelper getAzureStorageAccountSQLiteHelper() {
        return azureStorageAccountSQLiteHelper;
    }

    public static AzureSubscriptionsSQLiteHelper getAzureSubscriptionsSQLiteHelper() {
        return azureSubscriptionsSQLiteHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        azureStorageAccountSQLiteHelper = new AzureStorageAccountSQLiteHelper(getApplicationContext());
        azureStorageAccountSQLiteHelper.getWritableDatabase();
        azureStorageAccountSQLiteHelper.getReadableDatabase();

        azureSubscriptionsSQLiteHelper = new AzureSubscriptionsSQLiteHelper(getApplicationContext());

        //ensure that the tables are created
        azureSubscriptionsSQLiteHelper.getWritableDatabase().execSQL(AzureSubscriptionsSQLiteHelper.TABLE_CREATE);
        azureStorageAccountSQLiteHelper.getWritableDatabase().execSQL(AzureStorageAccountSQLiteHelper.TABLE_CREATE);
    }
}
