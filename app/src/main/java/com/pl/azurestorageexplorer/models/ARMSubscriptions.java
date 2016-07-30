package com.pl.azurestorageexplorer.models;

import java.util.List;

/**
 * Created by Praneet Loke on 7/30/2016.
 */
public class ARMSubscriptions {
    private List<ARMSubscription> value;

    public List<ARMSubscription> getValue() {
        return value;
    }

    public void setValue(List<ARMSubscription> value) {
        this.value = value;
    }
}
