package com.pl.azurestorageexplorer.fragments.interfaces;

import com.pl.azurestorageexplorer.storage.models.AzureStorageAccount;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public interface ISpinnerNavListener<T> {
    void selectionChanged(AzureStorageAccount account, T item);
}
