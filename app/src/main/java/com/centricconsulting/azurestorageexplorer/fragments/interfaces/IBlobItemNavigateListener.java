package com.centricconsulting.azurestorageexplorer.fragments.interfaces;

import com.centricconsulting.azurestorageexplorer.storage.models.AzureStorageAccount;
import com.microsoft.azure.storage.blob.ListBlobItem;

/**
 * Created by Praneet Loke on 4/23/2016.
 */
public interface IBlobItemNavigateListener {
    void onBlobItemClick(AzureStorageAccount account, ListBlobItem listBlobItem);
}
