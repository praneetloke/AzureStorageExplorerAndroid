package com.pl.azurestorageexplorer.storage.models;

/**
 * Created by Praneet Loke on 7/4/2016.
 */
public class AzureSubscription {
    private int id;
    private String name;
    private String subscriptionId;

    public AzureSubscription(int id, String name, String subscriptionId) {
        this.id = id;
        this.name = name;
        this.subscriptionId = subscriptionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}
