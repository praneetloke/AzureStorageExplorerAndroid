package com.pl.azurestorageexplorer.models;

import java.io.Serializable;

/**
 * Created by Praneet Loke on 4/25/2016.
 */
public class CloudBlobDirectorySerializable implements Serializable {
    private String containerName;
    private String prefix;

    public CloudBlobDirectorySerializable(String containerName, String prefix) {
        this.containerName = containerName;
        this.prefix = prefix;
    }

    public String getContainerName() {
        return this.containerName;
    }

    public String getPrefix() {
        return this.prefix;
    }
}
