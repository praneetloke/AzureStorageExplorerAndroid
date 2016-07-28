package com.pl.azurestorageexplorer.fragments.interfaces;

import com.pl.azurestorageexplorer.storage.models.AzureSubscriptionFilter;

/**
 * Created by Praneet Loke on 7/27/2016.
 */
public interface ISubscriptionSelectionChangeListener {
    void onSubscriptionSelectionChanged(AzureSubscriptionFilter item);
}
