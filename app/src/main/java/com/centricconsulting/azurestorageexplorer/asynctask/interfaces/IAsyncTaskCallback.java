package com.centricconsulting.azurestorageexplorer.asynctask.interfaces;

/**
 * Created by v-prloke on 4/16/2016.
 */
public interface IAsyncTaskCallback<T> {
    void finished(T result);

    void failed(String exceptionMessage);
}
