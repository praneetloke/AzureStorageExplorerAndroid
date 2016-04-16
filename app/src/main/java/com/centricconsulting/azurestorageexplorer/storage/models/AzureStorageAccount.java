package com.centricconsulting.azurestorageexplorer.storage.models;

/**
 * Created by Praneet Loke on 4/10/2016.
 */
public class AzureStorageAccount {

    private long id;
    private String Name;
    private String Key;

    public AzureStorageAccount(long id, String name, String key) {
        this.id = id;
        this.Name = name;
        this.Key = key;
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
}
