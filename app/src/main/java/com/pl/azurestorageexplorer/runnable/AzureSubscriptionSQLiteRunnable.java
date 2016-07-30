package com.pl.azurestorageexplorer.runnable;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.util.Log;

import com.pl.azurestorageexplorer.AzureStorageExplorerApplication;
import com.pl.azurestorageexplorer.models.ARMSubscription;
import com.pl.azurestorageexplorer.storage.AzureSubscriptionsSQLiteHelper;

import java.util.List;

/**
 * Created by Praneet Loke on 7/4/2016.
 */
public class AzureSubscriptionSQLiteRunnable implements Runnable {
    private static final String TAG = AzureSubscriptionSQLiteRunnable.class.getName();
    private List<ARMSubscription> mSubscriptions;

    public AzureSubscriptionSQLiteRunnable(List<ARMSubscription> subscriptions) {
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
            contentValues.put(AzureSubscriptionsSQLiteHelper.NAME, subscription.getDisplayName());
            contentValues.put(AzureSubscriptionsSQLiteHelper.SUBSCRIPTION_ID, subscription.getSubscriptionId());

            db.insert(AzureSubscriptionsSQLiteHelper.TABLE_NAME, null, contentValues);
        }

        db.setTransactionSuccessful();
        //end transaction
        db.endTransaction();
    }
}
