package com.centricconsulting.azurestorageexplorer.util;

import com.centricconsulting.azurestorageexplorer.R;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

/**
 * Created by Praneet Loke on 4/22/2016.
 */
public class Helpers {
    public static int GetDrawableResourceForBlobType(boolean isFolder, ListBlobItem blobItem) {
        if (isFolder) {
            return R.drawable.ic_folder;
        }

        CloudBlob cloudBlob = (CloudBlob) blobItem;
        String contentType = cloudBlob.getProperties().getContentType();
        if (contentType != null && contentType.startsWith("image")) {
            return R.drawable.ic_image;
        }

        return R.drawable.ic_file_general;
    }

    public static String GetBlobContentType(ListBlobItem blobItem) {
        return ((CloudBlob) blobItem).getProperties().getContentType();
    }
}
