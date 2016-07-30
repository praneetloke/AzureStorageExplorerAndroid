package com.pl.azurestorageexplorer.models;

/**
 * Created by Praneet Loke on 7/30/2016.
 */
public class ARMStorageKey {
    private String keyName;
    private String value;
    private String permissions;

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
