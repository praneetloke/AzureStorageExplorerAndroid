package com.pl.azurestorageexplorer.storage;

/**
 * Table to store the subscription info downloaded since login.
 */
public class AzureSubscriptionsSQLiteHelper {
    public static final String TABLE_NAME = "azure_subscriptions";
    //columns
    public static final String NAME = "Name";
    public static final String SUBSCRIPTION_ID = "SubscriptionId";
    //
    public static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT, " +
                    SUBSCRIPTION_ID + " TEXT, CreatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP, UpdatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
}
