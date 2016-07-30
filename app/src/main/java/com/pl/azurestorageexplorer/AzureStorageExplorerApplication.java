package com.pl.azurestorageexplorer;

import android.app.Application;

import com.pl.azurestorageexplorer.storage.CustomSQLiteHelper;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by Praneet Loke on 4/9/2016.
 */
public class AzureStorageExplorerApplication extends Application {

    public static final String DATABASE_NAME = "AzureStorageExplorer.db";
    public static final Map<String, String> accessToken = new HashMap<>();
    public static OkHttpClient mOkHttpClient = new OkHttpClient();
    private static CustomSQLiteHelper customSQLiteHelper;

    public static CustomSQLiteHelper getCustomSQLiteHelper() {
        return customSQLiteHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        customSQLiteHelper = new CustomSQLiteHelper(getApplicationContext());
        customSQLiteHelper.getWritableDatabase();
        customSQLiteHelper.getReadableDatabase();
    }
}
