package com.centricconsulting.azurestorageexplorer.fragments.interfaces;

import com.centricconsulting.azurestorageexplorer.storage.models.AzureStorageAccount;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public interface ISpinnerNavListener<T> {
    void selectionChanged(AzureStorageAccount account, T item);
}
