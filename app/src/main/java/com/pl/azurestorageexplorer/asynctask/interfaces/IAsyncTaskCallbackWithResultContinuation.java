package com.pl.azurestorageexplorer.asynctask.interfaces;

import com.microsoft.azure.storage.ResultContinuation;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public interface IAsyncTaskCallbackWithResultContinuation<T> {
    void finished(T result, ResultContinuation resultContinuation, boolean hasMoreResults);

    void failed(String exceptionMessage);
}
