package com.pl.azurestorageexplorer.util;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class Constants {
    public static final String STORAGE_ACCOUNT_BLOB_URL_FORMAT = "DefaultEndpointsProtocol=https;" +
            "AccountName=%s;" +
            "AccountKey=%s";

    public static final String AZURE_AUTH_REDIRECT_URI = "https://azurestorageexplorer";
    public static final String AZURE_AUTHORIZE_URL = "https://login.microsoftonline.com/common/oauth2/authorize?resource=https%3A%2F%2Fmanagement.core.windows.net%2F";
    public static final String AZURE_TOKEN_URL = "https://login.microsoftonline.com/common/oauth2/token";
    public static final String AZURE_AD_APP_CLIENT_ID = "9a2e9bbc-6de9-4b91-8899-10e8c4235cc2";
    public static final String AZURE_API_VERSION = "2015-04-01";
    public static final String AZURE_LIST_SUBSCRIPTIONS = "https://management.core.windows.net/subscriptions";
    public static final String AZURE_LIST_STORAGE_ACCOUNTS = "https://management.core.windows.net/%s/services/storageservices";
    public static final String AZURE_GET_STORAGE_KEYS = "https://management.core.windows.net/%s/services/storageservices/%s/keys";

}
