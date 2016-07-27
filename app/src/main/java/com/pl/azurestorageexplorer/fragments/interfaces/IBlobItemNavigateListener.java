package com.pl.azurestorageexplorer.fragments.interfaces;

import com.microsoft.azure.storage.blob.ListBlobItem;
import com.pl.azurestorageexplorer.storage.models.AzureStorageAccount;

/**
 * Created by Praneet Loke on 4/23/2016.
 */
public interface IBlobItemNavigateListener {
    void onBlobItemClick(AzureStorageAccount account, ListBlobItem listBlobItem);
}
