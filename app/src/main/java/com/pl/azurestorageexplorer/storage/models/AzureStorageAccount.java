package com.pl.azurestorageexplorer.storage.models;

/**
 * Created by Praneet Loke on 4/10/2016.
 */
public class AzureStorageAccount {

    private long id;
    private String name;
    private String key;
    private String subscriptionId;

    public AzureStorageAccount(long id, String name, String key, String subscriptionId) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.subscriptionId = subscriptionId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }
}
