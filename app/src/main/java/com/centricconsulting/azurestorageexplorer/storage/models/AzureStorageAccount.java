package com.centricconsulting.azurestorageexplorer.storage.models;

/**
 * Created by Praneet Loke on 4/10/2016.
 */
public class AzureStorageAccount {

    private long id;
    private String Name;
    private String Key;
    private String SubscriptionId;

    public AzureStorageAccount(long id, String name, String key, String subscriptionId) {
        this.id = id;
        this.Name = name;
        this.Key = key;
        this.SubscriptionId = subscriptionId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public String getKey() {
        return Key;
    }

    public String getSubscriptionId() {
        return SubscriptionId;
    }
}
