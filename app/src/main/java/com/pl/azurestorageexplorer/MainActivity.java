package com.pl.azurestorageexplorer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.pl.azurestorageexplorer.adapter.BlobContainersAdapter;
import com.pl.azurestorageexplorer.adapter.StorageAccountAdapter;
import com.pl.azurestorageexplorer.asynctask.BlobContainerListAsyncTask;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.pl.azurestorageexplorer.fragments.AddAccountDialogFragment;
import com.pl.azurestorageexplorer.fragments.BlobListFragment;
import com.pl.azurestorageexplorer.fragments.ConfirmationDialogFragment;
import com.pl.azurestorageexplorer.fragments.SubscriptionsFilterDialogFragment;
import com.pl.azurestorageexplorer.fragments.interfaces.IBlobItemNavigateListener;
import com.pl.azurestorageexplorer.fragments.interfaces.IDialogFragmentClickListener;
import com.pl.azurestorageexplorer.fragments.interfaces.ISpinnerNavListener;
import com.pl.azurestorageexplorer.models.CloudBlobContainerSerializable;
import com.pl.azurestorageexplorer.models.StorageService;
import com.pl.azurestorageexplorer.models.Subscription;
import com.pl.azurestorageexplorer.parser.XmlToPojo;
import com.pl.azurestorageexplorer.restclient.StorageKeyRestClient;
import com.pl.azurestorageexplorer.runnable.AzureSubscriptionFilterSQLiteRunnable;
import com.pl.azurestorageexplorer.runnable.AzureSubscriptionSQLiteRunnable;
import com.pl.azurestorageexplorer.storage.models.AzureStorageAccount;
import com.pl.azurestorageexplorer.storage.models.AzureSubscription;
import com.pl.azurestorageexplorer.storage.models.AzureSubscriptionFilter;
import com.pl.azurestorageexplorer.util.ActivityUtils;
import com.pl.azurestorageexplorer.util.Constants;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.AuthorizationUIController;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;
import com.wuman.android.auth.oauth2.store.SharedPreferencesCredentialStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        AddAccountDialogFragment.OnFragmentInteractionListener,
        IAsyncTaskCallback<ArrayList<CloudBlobContainerSerializable>>,
        BlobListFragment.OnFragmentInteractionListener,
        IDialogFragmentClickListener {

    private static final int REMOVE_STORAGE_ACCOUNT_REQUEST_CODE = 1;
    private static final String TAG = MainActivity.class.getName();
    private StorageAccountAdapter storageAccountAdapter;
    private BlobContainersAdapter blobContainersAdapter;
    private Spinner navMenuHeaderSpinner;
    private Spinner toolbarSpinner;
    private Stack<Fragment> fragmentStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //hide the title so the spinner has room
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragmentStack = new Stack<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_account);
        if (fab != null) {
            fab.setImageResource(R.drawable.ic_add);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AddAccountDialogFragment().show(getSupportFragmentManager(), "AddAccountDialog");
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        blobContainersAdapter = new BlobContainersAdapter(getApplicationContext(), new ArrayList<CloudBlobContainerSerializable>() {
        });
        toolbarSpinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
        toolbarSpinner.setAdapter(blobContainersAdapter);
        toolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(findViewById(R.id.coordinatorLayout), view.getTag().toString(), Snackbar.LENGTH_SHORT).show();
                final AzureStorageAccount account = storageAccountAdapter.getItem(navMenuHeaderSpinner.getSelectedItemPosition());
                final CloudBlobContainerSerializable container = blobContainersAdapter.getItem(position);
                final Fragment fragment = MainActivity.this.getSupportFragmentManager().findFragmentByTag(BlobListFragment.class.getName());
                if (fragment != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ((ISpinnerNavListener<CloudBlobContainerSerializable>) fragment).selectionChanged(account, container);
                        }
                    }).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Fragment containerListFragment = BlobListFragment.instantiate(getApplicationContext(), BlobListFragment.class.getName());
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), containerListFragment, R.id.contentFrame, BlobListFragment.class.getName());
        fragmentStack.push(containerListFragment);

        authenticate();
    }

    private void authenticate() {
        SharedPreferencesCredentialStore credentialStore =
                new SharedPreferencesCredentialStore(getApplicationContext(),
                        "AzureStorageExplorerApplication", new JacksonFactory());

        AuthorizationFlow.Builder builder = new AuthorizationFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                AndroidHttp.newCompatibleTransport(),
                new JacksonFactory(),
                new GenericUrl(Constants.AZURE_TOKEN_URL),
                null,
                Constants.AZURE_AD_APP_CLIENT_ID,
                Constants.AZURE_AUTHORIZE_URL);
        builder.setCredentialStore(credentialStore);
        AuthorizationFlow flow = builder.build();

        AuthorizationUIController controller =
                new DialogFragmentController(getFragmentManager(), true) {

                    @Override
                    public String getRedirectUri() throws IOException {
                        return Constants.AZURE_AUTH_REDIRECT_URI;
                    }

                    @Override
                    public boolean isJavascriptEnabledForWebView() {
                        return true;
                    }

                    @Override
                    public boolean disableWebViewCache() {
                        return true;
                    }

                    @Override
                    public boolean removePreviousCookie() {
                        return false;
                    }

                };

        OAuthManager oauth = new OAuthManager(flow, controller);
        OAuthManager.OAuthCallback<Credential> callback = new OAuthManager.OAuthCallback<Credential>() {
            private final String TAG = OAuthManager.OAuthCallback.class.getName();

            @Override
            public void run(OAuthManager.OAuthFuture<Credential> future) {
                try {
                    Credential credential = future.getResult();
                    String accessToken = credential.getAccessToken();
                    AzureStorageExplorerApplication.accessToken = accessToken;

                    fetchAzureSubscriptions();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // make API queries with credential.getAccessToken()
            }
        };
        oauth.authorizeImplicitly("", callback, null);
    }

    private void fetchAzureSubscriptions() {
        ArrayList<AzureSubscription> subscriptions = AzureStorageExplorerApplication.getCustomSQLiteHelper().getAzureSubscriptions();
        setupStorageAccountsInNavigationView();

        //if we have already cached the subscriptions, don't fetch them again...let the user tap the "sync" icon to initiate a refresh
        if (subscriptions.size() > 0) {
            return;
        }

        Request request = new Request.Builder()
                .url(Constants.AZURE_LIST_SUBSCRIPTIONS)
                .addHeader("Authorization", String.format("Bearer %s", AzureStorageExplorerApplication.accessToken))
                .addHeader("x-ms-version", Constants.AZURE_API_VERSION)
                .build();

        Call call = AzureStorageExplorerApplication.mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to execute " + call.request(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                final List<Subscription> subscriptions = XmlToPojo.parseList(Subscription.class, response.body().byteStream(), Xml.Encoding.UTF_8);

                //start a new thread to insert all subscriptions into SQLite
                new Thread(new AzureSubscriptionSQLiteRunnable(subscriptions)).start();
                new Thread(new AzureSubscriptionFilterSQLiteRunnable(subscriptions)).start();

                fetchStorageAccounts(subscriptions);
            }
        });
    }

    private void fetchStorageAccounts(List<Subscription> subscriptions) {
        ArrayList<AzureStorageAccount> accounts = AzureStorageExplorerApplication.getCustomSQLiteHelper().getAzureAccounts();
        //return if we already cached storage accounts..let the user explicity initiate a sync
        if (accounts.size() > 0) {
            return;
        }

        for (Subscription subscription : subscriptions) {
            Request request = new Request.Builder()
                    .url(String.format(Constants.AZURE_LIST_STORAGE_ACCOUNTS, subscription.getSubscriptionID()))
                    .addHeader("Authorization", String.format("Bearer %s", AzureStorageExplorerApplication.accessToken))
                    .addHeader("x-ms-version", Constants.AZURE_API_VERSION)
                    .addHeader("subscriptionID", subscription.getSubscriptionID())
                    .build();

            Call call = AzureStorageExplorerApplication.mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), String.format("Failed to fetch storage accounts for subscription ID: %s", call.request().header("subscriptionID")), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(final Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    final List<StorageService> storageServices = XmlToPojo.parseList(StorageService.class, response.body().byteStream(), Xml.Encoding.UTF_8);

                    //now get the keys for all storage accounts
                    new Thread(new StorageKeyRestClient(storageServices, call.request().header("subscriptionID"), new IAsyncTaskCallback<AzureStorageAccount>() {
                        @Override
                        public void finished(final AzureStorageAccount storageAccount) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onStorageAccountAdded(storageAccount);
                                }
                            });
                        }

                        @Override
                        public void failed(String exceptionMessage) {

                        }
                    })).start();
                }
            });
        }
    }

    private void setupStorageAccountsInNavigationView() {
        ArrayList<AzureSubscriptionFilter> selectedSubscriptions = AzureStorageExplorerApplication.getCustomSQLiteHelper().getSelectedAzureSubscriptions();
        ArrayList<String> subscriptionIds = new ArrayList<>();
        for (AzureSubscriptionFilter subscription : selectedSubscriptions) {
            subscriptionIds.add(subscription.getSubscriptionId());
        }
        ArrayList<AzureStorageAccount> accounts = AzureStorageExplorerApplication.getCustomSQLiteHelper().getAzureAccountsForSubscriptionIds(subscriptionIds);
        storageAccountAdapter = new StorageAccountAdapter(getApplicationContext(), accounts);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //setup the spinner in the drawer layout header
        navMenuHeaderSpinner = (Spinner) navigationView.getHeaderView(0).findViewById(R.id.navBarHeaderSpinner);
        navMenuHeaderSpinner.setAdapter(storageAccountAdapter);
        navMenuHeaderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //reset the fragment stack in case the user navigated into blob containers
                resetBlobListFragmentStack();
                final AzureStorageAccount account = storageAccountAdapter.getItem(position);
                if (account.getKey() == null) {
                    //fetch the key for this account first
                    List<StorageService> storageServices = new ArrayList<>();
                    storageServices.add(new StorageService(account.getName()));
                    if (account.getSubscriptionId() != null) {
                        new Thread(new StorageKeyRestClient(storageServices, account.getSubscriptionId(), new IAsyncTaskCallback<AzureStorageAccount>() {
                            @Override
                            public void finished(final AzureStorageAccount storageAccount) {
                                //update the storage account in the SQLite DB with the key
                                AzureStorageExplorerApplication.getCustomSQLiteHelper().updateStorageAccount(storageAccount);
                                //now get the containers for this storage account
                                BlobContainerListAsyncTask containerListAsyncTask = new BlobContainerListAsyncTask(MainActivity.this);
                                containerListAsyncTask.execute(storageAccount.getName(), storageAccount.getKey());
                            }

                            @Override
                            public void failed(String exceptionMessage) {

                            }
                        })).start();
                    }
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            BlobContainerListAsyncTask containerListAsyncTask = new BlobContainerListAsyncTask(MainActivity.this);
                            containerListAsyncTask.execute(account.getName(), account.getKey());
                        }
                    }).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //setup the click listener for the filter icon in the nav menu header
        LinearLayout subscriptionsFilter = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.filterSubscriptionsLayout);
        subscriptionsFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SubscriptionsFilterDialogFragment filterDialogFragment = new SubscriptionsFilterDialogFragment();
                filterDialogFragment.show(getSupportFragmentManager(), SubscriptionsFilterDialogFragment.class.getName());
            }
        });
    }

    private void resetBlobListFragmentStack() {
        if (fragmentStack == null) {
            return;
        }

        //remove the blob list fragments that are already there
        while (fragmentStack.size() >= 2) {
            //if we are about to pop the final child fragment, then hide the title again
            if (fragmentStack.size() == 2) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarSpinner.setVisibility(View.VISIBLE);
            }

            ActivityUtils.popPreviousFragmentFromStack(getSupportFragmentManager(), fragmentStack);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AzureStorageExplorerApplication.getCustomSQLiteHelper().close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentStack.size() >= 2) {
            //if we are about to pop the final child fragment, then hide the title again
            if (fragmentStack.size() == 2) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                toolbarSpinner.setVisibility(View.VISIBLE);
            }

            ActivityUtils.popPreviousFragmentFromStack(getSupportFragmentManager(), fragmentStack);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (fragmentStack != null && fragmentStack.size() >= 2) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            toolbarSpinner.setVisibility(View.GONE);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.removeAccount) {
            //delete the currently selected account
            Bundle args = new Bundle();
            args.putString("title", getString(R.string.delete_storage_account_title));
            args.putString("message", getString(R.string.delete_storage_account_confirmation_message));
            args.putInt("requestCode", REMOVE_STORAGE_ACCOUNT_REQUEST_CODE);
            ConfirmationDialogFragment removeStorageAccount = new ConfirmationDialogFragment();
            removeStorageAccount.setArguments(args);
            removeStorageAccount.show(getSupportFragmentManager(), ConfirmationDialogFragment.class.getName());
        }

        return true;
    }

    @Override
    public void onStorageAccountAdded(AzureStorageAccount account) {
        storageAccountAdapter.add(account);
        storageAccountAdapter.notifyDataSetChanged();
    }

    @Override
    public void finished(ArrayList<CloudBlobContainerSerializable> result) {
        //received a list of blob containers..load them into the toolbar's spinner
        blobContainersAdapter = new BlobContainersAdapter(getApplicationContext(), result);
        toolbarSpinner.setAdapter(blobContainersAdapter);
        toolbarSpinner.dispatchSetSelected(true);
    }

    @Override
    public void failed(String exceptionMessage) {

    }

    @Override
    public void onBlobItemClicked(ListBlobItem blobItem) {
        invalidateOptionsMenu();

        Fragment blobListFragment = BlobListFragment.instantiate(getApplicationContext(), BlobListFragment.class.getName());
        ActivityUtils.addFragmentStacked(getSupportFragmentManager(), blobListFragment, R.id.contentFrame, blobItem.getUri().getPath(), fragmentStack);

        AzureStorageAccount account = storageAccountAdapter.getItem(navMenuHeaderSpinner.getSelectedItemPosition());
        ((IBlobItemNavigateListener) blobListFragment).onBlobItemClick(account, blobItem);
    }

    @Override
    public void onConfirmationDialogPositiveClick(int requestCode) {
        if (requestCode == REMOVE_STORAGE_ACCOUNT_REQUEST_CODE) {
            //first remove all blob list fragments
            if (fragmentStack.size() > 1) {

            }
        }
    }

    @Override
    public void onConfirmationDialogNegativeClick(int requestCode) {

    }
}
