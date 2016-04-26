package com.centricconsulting.azurestorageexplorer.models;

import java.io.Serializable;

/**
 * Created by Praneet Loke on 4/25/2016.
 */
public class CloudBlobContainerSerializable implements Serializable {
    private String name;

    public CloudBlobContainerSerializable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
