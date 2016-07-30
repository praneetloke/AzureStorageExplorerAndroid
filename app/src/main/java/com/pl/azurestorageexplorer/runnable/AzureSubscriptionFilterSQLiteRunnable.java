package com.pl.azurestorageexplorer.runnable;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.util.Log;

import com.pl.azurestorageexplorer.AzureStorageExplorerApplication;
import com.pl.azurestorageexplorer.models.ARMSubscription;
import com.pl.azurestorageexplorer.storage.AzureSubscriptionsFilterSQLiteHelper;

import java.util.List;

/**
 * Created by Praneet Loke on 7/4/2016.
 */
public class AzureSubscriptionFilterSQLiteRunnable implements Runnable {
    private static final String TAG = AzureSubscriptionFilterSQLiteRunnable.class.getName();
    private List<ARMSubscription> mSubscriptions;

    public AzureSubscriptionFilterSQLiteRunnable(List<ARMSubscription> subscriptions) {
        mSubscriptions = subscriptions;
    }

    @Override
    public void run() {
        //begin transaction
        SQLiteDatabase db = AzureStorageExplorerApplication
                .getCustomSQLiteHelper()
                .getWritableDatabase();

        boolean transactionStarted = false;

        while (!transactionStarted) {
            try {
                db.beginTransaction();
                transactionStarted = true;
            } catch (SQLiteDatabaseLockedException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

        for (ARMSubscription subscription : mSubscriptions) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AzureSubscriptionsFilterSQLiteHelper.NAME, subscription.getDisplayName());
            contentValues.put(AzureSubscriptionsFilterSQLiteHelper.SUBSCRIPTION_ID, subscription.getSubscriptionId());
            contentValues.put(AzureSubscriptionsFilterSQLiteHelper.IS_SELECTED, 1);

            db.insert(AzureSubscriptionsFilterSQLiteHelper.TABLE_NAME, null, contentValues);
        }

        db.setTransactionSuccessful();
        //end transaction
        db.endTransaction();
    }
}
