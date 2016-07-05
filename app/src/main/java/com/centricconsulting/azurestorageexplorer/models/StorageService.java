package com.centricconsulting.azurestorageexplorer.models;

/**
 * Created by Praneet Loke on 7/4/2016.
 */
public class StorageService {
    private String ServiceName;
    private StorageServiceKeys StorageServiceKeys;

    public StorageService() {

    }

    public StorageService(String serviceName) {
        ServiceName = serviceName;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public StorageServiceKeys getStorageServiceKeys() {
        return StorageServiceKeys;
    }

    public void setStorageServiceKeys(StorageServiceKeys storageServiceKeys) {
        StorageServiceKeys = storageServiceKeys;
    }
}
