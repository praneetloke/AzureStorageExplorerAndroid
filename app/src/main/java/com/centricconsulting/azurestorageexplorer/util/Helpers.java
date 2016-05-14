package com.centricconsulting.azurestorageexplorer.util;

import android.os.Bundle;

import com.centricconsulting.azurestorageexplorer.R;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.net.URISyntaxException;

/**
 * Created by Praneet Loke on 4/22/2016.
 */
public class Helpers {
    public static int getDrawableResourceForBlobType(boolean isFolder, ListBlobItem blobItem) {
        if (isFolder) {
            return R.drawable.ic_folder;
        }

        CloudBlob cloudBlob = (CloudBlob) blobItem;
        String contentType = cloudBlob.getProperties().getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image")) {
                return R.drawable.ic_image;
            } else if (contentType.startsWith("video")) {
                return R.drawable.ic_video;
            }
        }

        return R.drawable.ic_file_general;
    }

    public static String getBlobContentType(ListBlobItem blobItem) {
        return ((CloudBlob) blobItem).getProperties().getContentType();
    }

    public static Bundle getBlobInfoFromListBlobItem(CloudBlob cloudBlob) {
        Bundle args = new Bundle();
        try {
            args.putString("title", cloudBlob.getName());
        } catch (URISyntaxException e) {
            //ignore
        }
        args.putString("lastModified", cloudBlob.getProperties().getLastModified().toString());
        args.putString("contentType", cloudBlob.getProperties().getContentType());
        args.putString("cacheControl", cloudBlob.getProperties().getCacheControl());
        args.putString("blobType", cloudBlob.getProperties().getBlobType().name());
        args.putString("length", getHumanReadableLength(cloudBlob.getProperties().getLength()));

        return args;
    }

    public static String getHumanReadableLength(long bytes) {
        String result = null;
        if (bytes > 1024 && bytes / 1024 < 1024) {
            result = String.format("%,.2f KB", (double) bytes / 1024);
        } else if (bytes / 1024 >= 1024 && bytes / 1048576 < 1024) {
            result = String.format("%,.2f MB", (double) bytes / 1048576);
        } else if (bytes / 1048576 >= 1024) {
            result = "More than 1GB";
        } else {
            result = String.format("%d bytes", bytes);
        }

        return result;
    }
}
