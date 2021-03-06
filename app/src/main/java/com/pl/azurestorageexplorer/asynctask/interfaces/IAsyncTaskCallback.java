package com.pl.azurestorageexplorer.asynctask.interfaces;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public interface IAsyncTaskCallback<T> {
    void finished(T result);

    void failed(String exceptionMessage);
}
