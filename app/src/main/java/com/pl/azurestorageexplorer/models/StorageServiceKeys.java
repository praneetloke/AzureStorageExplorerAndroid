package com.pl.azurestorageexplorer.models;

/**
 * Created by Praneet Loke on 7/4/2016.
 */
public class StorageServiceKeys {
    private String Primary;
    private String Secondary;

    public StorageServiceKeys() {

    }

    public String getSecondary() {
        return Secondary;
    }

    public void setSecondary(String secondary) {
        Secondary = secondary;
    }

    public String getPrimary() {
        return Primary;
    }

    public void setPrimary(String primary) {
        Primary = primary;
    }
}
