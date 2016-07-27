package com.pl.azurestorageexplorer.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pl.azurestorageexplorer.AzureStorageExplorerApplication;
import com.pl.azurestorageexplorer.storage.models.AzureStorageAccount;
import com.pl.azurestorageexplorer.storage.models.AzureSubscription;
import com.pl.azurestorageexplorer.storage.models.AzureSubscriptionFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Praneet Loke on 7/26/2016.
 */
public class CustomSQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public CustomSQLiteHelper(Context context) {
        super(context, AzureStorageExplorerApplication.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AzureStorageAccountSQLiteHelper.TABLE_CREATE);
        db.execSQL(AzureSubscriptionsSQLiteHelper.TABLE_CREATE);
        db.execSQL(AzureSubscriptionsFilterSQLiteHelper.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<AzureStorageAccount> getAzureAccounts() {
        Cursor cursor = getReadableDatabase().rawQuery(
                String.format("select id, %s, %s, %s from %s;",
                        AzureStorageAccountSQLiteHelper.NAME,
                        AzureStorageAccountSQLiteHelper.KEY,
                        AzureStorageAccountSQLiteHelper.SUBSCRIPTION_ID,
                        AzureStorageAccountSQLiteHelper.TABLE_NAME),
                new String[]{});

        ArrayList<AzureStorageAccount> accounts = new ArrayList<>();
        while (cursor.moveToNext()) {
            accounts.add(new AzureStorageAccount(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }

        cursor.close();
        return accounts;
    }

    public ArrayList<AzureStorageAccount> getAzureAccountsForSubscriptionIds(List<String> subscriptionIds) {
        if (subscriptionIds == null || subscriptionIds.size() == 0) {
            return getAzureAccounts();
        }

        Cursor cursor = getReadableDatabase().rawQuery(
                String.format("SELECT id, %s, %s, %s FROM %s WHERE %s in (%s);",
                        AzureStorageAccountSQLiteHelper.NAME,
                        AzureStorageAccountSQLiteHelper.KEY,
                        AzureStorageAccountSQLiteHelper.SUBSCRIPTION_ID,
                        AzureStorageAccountSQLiteHelper.TABLE_NAME,
                        AzureStorageAccountSQLiteHelper.SUBSCRIPTION_ID,
                        subscriptionIds.toString()),
                new String[]{});

        ArrayList<AzureStorageAccount> accounts = new ArrayList<>();
        while (cursor.moveToNext()) {
            accounts.add(new AzureStorageAccount(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }

        cursor.close();
        return accounts;
    }

    public void updateStorageAccount(AzureStorageAccount storageAccount) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AzureStorageAccountSQLiteHelper.NAME, storageAccount.getName());
        contentValues.put(AzureStorageAccountSQLiteHelper.KEY, storageAccount.getKey());
        contentValues.put(AzureStorageAccountSQLiteHelper.SUBSCRIPTION_ID, storageAccount.getSubscriptionId());
        getWritableDatabase().update(AzureStorageAccountSQLiteHelper.TABLE_NAME, contentValues, "id = ?", new String[]{Long.toString(storageAccount.getId())});
    }

    public ArrayList<AzureSubscription> getAzureSubscriptions() {
        Cursor cursor = getReadableDatabase().rawQuery(
                String.format("select id, %s, %s from %s;",
                        AzureSubscriptionsSQLiteHelper.NAME,
                        AzureSubscriptionsSQLiteHelper.SUBSCRIPTION_ID,
                        AzureSubscriptionsSQLiteHelper.TABLE_NAME),
                new String[]{});

        ArrayList<AzureSubscription> subscriptions = new ArrayList<>();
        while (cursor.moveToNext()) {
            subscriptions.add(new AzureSubscription(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
        }

        cursor.close();
        return subscriptions;
    }

    public ArrayList<AzureSubscriptionFilter> getAzureSubscriptionsFilters() {
        Cursor cursor = getReadableDatabase().rawQuery(
                String.format("select id, %s, %s, %s from %s;",
                        AzureSubscriptionsFilterSQLiteHelper.NAME,
                        AzureSubscriptionsFilterSQLiteHelper.SUBSCRIPTION_ID,
                        AzureSubscriptionsFilterSQLiteHelper.IS_SELECTED,
                        AzureSubscriptionsFilterSQLiteHelper.TABLE_NAME),
                new String[]{});

        ArrayList<AzureSubscriptionFilter> subscriptions = new ArrayList<>();
        while (cursor.moveToNext()) {
            subscriptions.add(new AzureSubscriptionFilter(cursor.getInt(0), cursor.getString(1), cursor.getString(2), (cursor.getInt(3) == 1 ? true : false)));
        }

        cursor.close();
        return subscriptions;
    }

    public ArrayList<AzureSubscriptionFilter> getSelectedAzureSubscriptions() {
        Cursor cursor = getReadableDatabase().rawQuery(
                String.format("select id, %s, %s, %s from %s where %s = 1;",
                        AzureSubscriptionsFilterSQLiteHelper.NAME,
                        AzureSubscriptionsFilterSQLiteHelper.SUBSCRIPTION_ID,
                        AzureSubscriptionsFilterSQLiteHelper.IS_SELECTED,
                        AzureSubscriptionsFilterSQLiteHelper.TABLE_NAME,
                        AzureSubscriptionsFilterSQLiteHelper.IS_SELECTED),
                new String[]{});

        ArrayList<AzureSubscriptionFilter> subscriptions = new ArrayList<>();
        while (cursor.moveToNext()) {
            subscriptions.add(new AzureSubscriptionFilter(cursor.getInt(0), cursor.getString(1), cursor.getString(2), (cursor.getInt(3) == 1 ? true : false)));
        }

        cursor.close();
        return subscriptions;
    }
}
