package com.pl.azurestorageexplorer.models;

/**
 * Created by Praneet Loke on 7/30/2016.
 */
public class ARMStorageService {
    private String id;
    private String name;
    private String location;

    public ARMStorageService(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
