package com.pl.azurestorageexplorer.models;

import android.net.Uri;

/**
 * Created by Praneet Loke on 8/20/2016.
 */
public class BlobToUpload {
    private String fileName;
    private long contentLength;
    private String mimeType;
    private Uri uri;

    public BlobToUpload() {
    }

    public BlobToUpload(String fileName, long contentLength, String mimeType, Uri uri) {
        this.fileName = fileName;
        this.contentLength = contentLength;
        this.uri = uri;
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
