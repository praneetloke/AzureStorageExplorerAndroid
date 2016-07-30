package com.pl.azurestorageexplorer.storage.models;

/**
 * Created by Praneet Loke on 4/10/2016.
 */
public class AzureStorageAccount {

    private long id;
    private String name;
    private String key;
    private String subscriptionId;
    private String resourceGroup;

    public AzureStorageAccount(long id, String name, String key, String subscriptionId, String resourceGroup) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.subscriptionId = subscriptionId;
        this.resourceGroup = resourceGroup;
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

    public String getResourceGroup() {
        return resourceGroup;
    }

    public void setResourceGroup(String resourceGroup) {
        this.resourceGroup = resourceGroup;
    }
}
