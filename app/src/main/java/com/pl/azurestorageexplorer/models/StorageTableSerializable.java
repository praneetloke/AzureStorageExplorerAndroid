package com.pl.azurestorageexplorer.models;

import java.io.Serializable;

/**
 * Created by Praneet Loke on 8/6/2016.
 */
public class StorageTableSerializable implements Serializable {
    private String name;

    public StorageTableSerializable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
