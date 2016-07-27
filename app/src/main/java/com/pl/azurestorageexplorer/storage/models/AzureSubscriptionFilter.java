package com.pl.azurestorageexplorer.storage.models;

/**
 * Created by Praneet Loke on 7/24/2016.
 */
public class AzureSubscriptionFilter {
    private int id;
    private String Name;
    private String SubscriptionId;
    private boolean isSelected;

    public AzureSubscriptionFilter(int id, String name, String subscriptionId, boolean isSelected) {
        this.id = id;
        this.Name = name;
        this.SubscriptionId = subscriptionId;
        this.isSelected = isSelected;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
