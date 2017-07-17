package com.pl.azurestorageexplorer.util;

/**
 * Created by Praneet Loke on 4/16/2016.
 */
public class Constants {
    public static final String STORAGE_ACCOUNT_URL_FORMAT = "DefaultEndpointsProtocol=https;" +
            "AccountName=%s;" +
            "AccountKey=%s";

    public static final String AZURE_AUTH_REDIRECT_URI = "https://azurestorageexplorer/";
    public static final String AAD_AUTHORITY = "https://login.microsoftonline.com/common";
    public static final String ARM_RESOURCE = "https%3A%2F%2Fmanagement.azure.com%2F";
    public static final String AZURE_AUTHORIZE_URL = AAD_AUTHORITY + "/oauth2/authorize?resource=" + ARM_RESOURCE;
    public static final String AZURE_TOKEN_URL = AAD_AUTHORITY + "/oauth2/token";
    public static final String AZURE_AD_APP_CLIENT_ID = "9a2e9bbc-6de9-4b91-8899-10e8c4235cc2";

    public static final String AZURE_RESOURCE_MANAGER = "https://management.azure.com";
    public static final String AZURE_LIST_SUBSCRIPTIONS = AZURE_RESOURCE_MANAGER + "/subscriptions";
    public static final String AZURE_STORAGE_RESOURCE_MANAGER = AZURE_LIST_SUBSCRIPTIONS + "/%s/providers/Microsoft.Storage/storageAccounts";
    public static final String AZURE_GET_STORAGE_KEYS = "/listKeys?api-version=2016-01-01";

    public static final String CLASSIC_RESOURCE = "https%3A%2F%2Fmanagement.core.windows.net%2F";
    public static final String CLASSIC_AZURE_AUTHORIZE_URL = AAD_AUTHORITY + "/oauth2/authorize?resource=" + CLASSIC_RESOURCE;
    public static final String LEGACY_AZURE_SERVICE_MANAGEMENT_API = "https://management.core.windows.net";
    public static final String LEGACY_AZURE_API_VERSION = "2015-04-01";
    public static final String LEGACY_AZURE_LIST_STORAGE_ACCOUNTS = "https://management.core.windows.net/%s/services/storageservices";
    public static final String LEGACY_AZURE_GET_STORAGE_KEYS = "https://management.core.windows.net/%s/services/storageservices/%s/keys";
    public static final String LEGACY_STORAGE_ACCOUNT_RESOURCE_GROUP = "classic";

}
