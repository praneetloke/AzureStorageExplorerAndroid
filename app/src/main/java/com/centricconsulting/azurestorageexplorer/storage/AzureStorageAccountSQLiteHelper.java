package com.centricconsulting.azurestorageexplorer.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.centricconsulting.azurestorageexplorer.AzureStorageExplorerApplication;
import com.centricconsulting.azurestorageexplorer.models.AzureStorageAccount;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/9/2016.
 */
public class AzureStorageAccountSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "azure_storage_account";
    //columns
    public static final String NAME = "Name";
    public static final String KEY = "Key";
    private static final int DATABASE_VERSION = 1;
    //
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT, " +
                    KEY + " TEXT, CreatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP, UpdatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

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
                String.format("select id, %s, %s from %s;",
                        AzureStorageAccountSQLiteHelper.NAME,
                        AzureStorageAccountSQLiteHelper.KEY,
                        AzureStorageAccountSQLiteHelper.TABLE_NAME),
                new String[]{});

        ArrayList<AzureStorageAccount> accounts = new ArrayList<>();
        while (cursor.moveToNext()) {
            accounts.add(new AzureStorageAccount(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
        }

        cursor.close();
        return accounts;
    }
}
