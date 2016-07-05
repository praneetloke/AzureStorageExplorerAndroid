package com.centricconsulting.azurestorageexplorer.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.centricconsulting.azurestorageexplorer.AzureStorageExplorerApplication;
import com.centricconsulting.azurestorageexplorer.storage.models.AzureStorageAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Praneet Loke on 4/9/2016.
 */
public class AzureStorageAccountSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "azure_storage_account";
    //columns
    public static final String NAME = "Name";
    public static final String KEY = "Key";
    public static final String SUBSCRIPTION_ID = "SubscriptionId";
    //
    public static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT, " +
                    KEY + " TEXT, " + SUBSCRIPTION_ID + " TEXT, CreatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP, UpdatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    private static final int DATABASE_VERSION = 2;

    public AzureStorageAccountSQLiteHelper(Context context) {
        super(context, AzureStorageExplorerApplication.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
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
        getWritableDatabase().update(TABLE_NAME, contentValues, "id = ?", new String[]{Long.toString(storageAccount.getId())});
    }
}
