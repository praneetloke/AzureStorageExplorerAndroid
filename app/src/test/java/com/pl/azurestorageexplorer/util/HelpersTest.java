package com.pl.azurestorageexplorer.util;

import android.os.Bundle;

import com.microsoft.azure.storage.AccessCondition;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.BlobType;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.pl.azurestorageexplorer.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by Praneet Loke on 10/2/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpersTest {

    CloudBlobContainer blobContainer;

    CloudBlob blobItem;

    @Mock
    Bundle mockBundle;

    @Before
    public void Setup() {
        try {
            blobContainer = new CloudBlobContainer(new URI("https://storageaccount.blob.core.windows.net/container"));
            blobItem = new CloudBlob(BlobType.BLOCK_BLOB, "blob", "assdf", blobContainer) {
                @Override
                public void setStreamWriteSizeInBytes(int streamWriteSizeInBytes) {

                }

                @Override
                public void upload(InputStream sourceStream, long length) throws StorageException, IOException {

                }

                @Override
                public void upload(InputStream sourceStream, long length, AccessCondition accessCondition, BlobRequestOptions options, OperationContext opContext) throws StorageException, IOException {

                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }

    @After
    public void Teardown() {
        blobItem = null;
        blobContainer = null;
    }

    @Test
    public void getDrawableResourceForBlobType_isFolderIsTrue_returnsCorrectIconForFolder() {
        int expected = R.drawable.ic_folder;

        int actual = Helpers.getDrawableResourceForBlobType(true, null);

        assertThat(actual, is(expected));
    }

    @Test
    public void getDrawableResourceForBlobType_isFolderIsFalseAndBlobItemIsImage_returnsIconForImages() {
        int expected = R.drawable.ic_image;

        blobItem.getProperties().setContentType("image/png");

        int actual = Helpers.getDrawableResourceForBlobType(false, blobItem);

        assertThat(actual, is(expected));
    }

    @Test
    public void getDrawableResourceForBlobType_isFolderIsFalseAndBlobItemWithNullContentType_returnsGeneralIcon() {
        int expected = R.drawable.ic_file_general;
        int actual = Helpers.getDrawableResourceForBlobType(false, blobItem);

        assertThat(actual, is(expected));
    }

    @Test
    public void getDrawableResourceForBlobType_isFolderIsFalseAndBlobItemIsVideo_returnsIconForVideo() {
        int expected = R.drawable.ic_video;

        blobItem.getProperties().setContentType("video/mp4");

        int actual = Helpers.getDrawableResourceForBlobType(false, blobItem);

        assertThat(actual, is(expected));
    }

    @Test
    public void getBlobInfoFromListBlobItem_withNonNullBlobItem_putsTitleInBundle() {
        try {
            Helpers.getBlobInfoFromListBlobItem(mockBundle, blobItem);
            //verify putString was called for the "title"
            verify(mockBundle).putString(eq("title"), eq(blobItem.getName()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBlobInfoFromListBlobItem_withNonNullBlobItem_putsContentTypeInBundle() {
        blobItem.getProperties().setContentType("image/png");

        Helpers.getBlobInfoFromListBlobItem(mockBundle, blobItem);

        //verify putString was called for the "title"
        verify(mockBundle).putString(eq("contentType"), eq("image/png"));
    }

    @Test
    public void getBlobInfoFromListBlobItem_withNonNullBlobItem_putsBlobTypeInBundle() {
        Helpers.getBlobInfoFromListBlobItem(mockBundle, blobItem);

        //verify putString was called for the "title"
        verify(mockBundle).putString(eq("blobType"), eq(BlobType.BLOCK_BLOB.toString()));
    }

    @Test
    public void getBlobInfoFromListBlobItem_withNonNullBlobItem_putsLengthInBundle() {
        Helpers.getBlobInfoFromListBlobItem(mockBundle, blobItem);

        //verify putString was called for the "title"
        verify(mockBundle).putString(eq("length"), eq("0 bytes"));
    }

    @Test
    public void getBlobInfoFromListBlobItem_withNonNullBlobItem_putsLastModifiedInBundle() {
        Helpers.getBlobInfoFromListBlobItem(mockBundle, blobItem);

        //verify putString was called for the "title"
        verify(mockBundle).putString(eq("lastModified"), eq("n/a"));
    }

    @Test
    public void getBlobInfoFromListBlobItem_withNonNullBlobItem_putsCacheControlInBundle() {
        blobItem.getProperties().setCacheControl("cache");

        Helpers.getBlobInfoFromListBlobItem(mockBundle, blobItem);

        //verify putString was called for the "title"
        verify(mockBundle).putString(eq("cacheControl"), eq("cache"));
    }

    @Test
    public void getHumanReadableLength_1024bytes_returns1KB() {
        String expected = "1.00 KB";

        String actual = Helpers.getHumanReadableLength(1024);

        assertThat(actual, is(expected));
    }

    @Test
    public void getHumanReadableLength_0bytes_returns0Bytes() {
        String expected = "0 bytes";

        String actual = Helpers.getHumanReadableLength(0);

        assertThat(actual, is(expected));
    }
}
