package com.centricconsulting.azurestorageexplorer.storage.models;

/**
 * Created by Praneet Loke on 7/4/2016.
 */
public class AzureSubscription {
    private int id;
    private String Name;
    private String SubscriptionId;

    public AzureSubscription(int id, String name, String subscriptionId) {
        this.id = id;
        Name = name;
        SubscriptionId = subscriptionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSubscriptionId() {
        return SubscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        SubscriptionId = subscriptionId;
    }
}
