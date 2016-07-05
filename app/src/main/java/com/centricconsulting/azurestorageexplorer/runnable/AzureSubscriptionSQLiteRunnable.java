package com.centricconsulting.azurestorageexplorer.runnable;

import android.content.ContentValues;

import com.centricconsulting.azurestorageexplorer.AzureStorageExplorerApplication;
import com.centricconsulting.azurestorageexplorer.models.Subscription;
import com.centricconsulting.azurestorageexplorer.storage.AzureStorageAccountSQLiteHelper;
import com.centricconsulting.azurestorageexplorer.storage.AzureSubscriptionsSQLiteHelper;

import java.util.List;

/**
 * Created by Praneet Loke on 7/4/2016.
 */
public class AzureSubscriptionSQLiteRunnable implements Runnable {
    private List<Subscription> mSubscriptions;

    public AzureSubscriptionSQLiteRunnable(List<Subscription> subscriptions) {
        mSubscriptions = subscriptions;
    }

    @Override
    public void run() {
        //begin transaction
        AzureStorageExplorerApplication
                .getAzureSubscriptionsSQLiteHelper()
                .getWritableDatabase()
                .beginTransaction();

        for (Subscription subscription : mSubscriptions) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AzureSubscriptionsSQLiteHelper.NAME, subscription.getSubscriptionName());
            contentValues.put(AzureSubscriptionsSQLiteHelper.SUBSCRIPTION_ID, subscription.getSubscriptionID());

            AzureStorageExplorerApplication
                    .getAzureSubscriptionsSQLiteHelper()
                    .getWritableDatabase()
                    .insert(AzureStorageAccountSQLiteHelper.TABLE_NAME, null, contentValues);
        }

        //end transaction
        AzureStorageExplorerApplication
                .getAzureSubscriptionsSQLiteHelper()
                .getWritableDatabase()
                .endTransaction();
    }
}
