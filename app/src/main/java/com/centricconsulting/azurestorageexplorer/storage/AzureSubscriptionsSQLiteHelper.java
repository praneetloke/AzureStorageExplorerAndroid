package com.centricconsulting.azurestorageexplorer.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.centricconsulting.azurestorageexplorer.AzureStorageExplorerApplication;
import com.centricconsulting.azurestorageexplorer.storage.models.AzureSubscription;

import java.util.ArrayList;

/**
 * Created by Praneet Loke on 4/9/2016.
 */
public class AzureSubscriptionsSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "azure_subscriptions";
    //columns
    public static final String NAME = "Name";
    public static final String SUBSCRIPTION_ID = "SubscriptionId";
    //
    public static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT, " +
                    SUBSCRIPTION_ID + " TEXT, CreatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP, UpdatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    private static final int DATABASE_VERSION = 2;

    public AzureSubscriptionsSQLiteHelper(Context context) {
        super(context, AzureStorageExplorerApplication.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
}
