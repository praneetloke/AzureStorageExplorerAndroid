package com.pl.azurestorageexplorer.storage;

/**
 * Table for maintaining the subscriptions which the user selected to filter the nav menu spinner.
 */
public class AzureSubscriptionsFilterSQLiteHelper {
    public static final String TABLE_NAME = "azure_subscriptions_filter";
    //columns
    public static final String NAME = "Name";
    public static final String SUBSCRIPTION_ID = "SubscriptionId";
    public static final String IS_SELECTED = "IsSelected";
    //
    public static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT, " +
                    SUBSCRIPTION_ID + " TEXT, " +
                    IS_SELECTED + " INTEGER, " +
                    "CreatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP, UpdatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
}
