package com.pl.azurestorageexplorer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.pl.azurestorageexplorer.adapter.BlobContainersAdapter;
import com.pl.azurestorageexplorer.adapter.StorageAccountAdapter;
import com.pl.azurestorageexplorer.adapter.StorageTablesAdapter;
import com.pl.azurestorageexplorer.asynctask.BlobContainerListAsyncTask;
import com.pl.azurestorageexplorer.asynctask.StorageTablesAsyncTask;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.pl.azurestorageexplorer.enums.StorageServiceType;
import com.pl.azurestorageexplorer.fragments.AddAccountDialogFragment;
import com.pl.azurestorageexplorer.fragments.BlobListFragment;
import com.pl.azurestorageexplorer.fragments.ConfirmationDialogFragment;
import com.pl.azurestorageexplorer.fragments.ProgressDialogFragment;
import com.pl.azurestorageexplorer.fragments.SubscriptionsFilterDialogFragment;
import com.pl.azurestorageexplorer.fragments.TableEntitiesFragment;
import com.pl.azurestorageexplorer.fragments.interfaces.IBlobItemNavigateListener;
import com.pl.azurestorageexplorer.fragments.interfaces.IDialogFragmentClickListener;
import com.pl.azurestorageexplorer.fragments.interfaces.ISpinnerNavListener;
import com.pl.azurestorageexplorer.fragments.interfaces.ISubscriptionSelectionChangeListener;
import com.pl.azurestorageexplorer.models.ARMStorageService;
import com.pl.azurestorageexplorer.models.ARMStorageServices;
import com.pl.azurestorageexplorer.models.ARMSubscription;
import com.pl.azurestorageexplorer.models.ARMSubscriptions;
import com.pl.azurestorageexplorer.models.CloudBlobContainerSerializable;
import com.pl.azurestorageexplorer.models.StorageService;
import com.pl.azurestorageexplorer.models.StorageTableSerializable;
import com.pl.azurestorageexplorer.parser.XmlToPojo;
import com.pl.azurestorageexplorer.restclient.LegacyStorageKeyRestClient;
import com.pl.azurestorageexplorer.restclient.StorageKeyRestClient;
import com.pl.azurestorageexplorer.runnable.AzureSubscriptionFilterSQLiteRunnable;
import com.pl.azurestorageexplorer.runnable.AzureSubscriptionSQLiteRunnable;
import com.pl.azurestorageexplorer.spinner.ReSelectableSpinner;
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
import java.util.concurrent.CancellationException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        AddAccountDialogFragment.OnFragmentInteractionListener,
        IAsyncTaskCallback<ArrayList<?>>,
        BlobListFragment.OnFragmentInteractionListener,
        TableEntitiesFragment.OnFragmentInteractionListener,
        IDialogFragmentClickListener,
        ISubscriptionSelectionChangeListener {

    private static final int SIGN_OUT_REQUEST_CODE = 1;
    private static final String TAG = MainActivity.class.getName();
    private final Gson gson = new Gson();
    private StorageAccountAdapter storageAccountAdapter;
    private BlobContainersAdapter blobContainersAdapter;
    private StorageTablesAdapter storageTablesAdapter;
    private ReSelectableSpinner navMenuHeaderSpinner;
    private Spinner toolbarSpinner;
    private Stack<Fragment> fragmentStack;
    private DrawerLayout drawer;
    private Handler handler = new Handler();
    private SharedPreferencesCredentialStore credentialStore;
    private ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();

    //the default selected menu item in the navigation drawer is Blobs
    private StorageServiceType storageServiceType = StorageServiceType.BLOB;

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

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                String fragmentTag = null;
                Object item = null;
                //figure out which fragment we need to tell about the toolbar spinner change
                if (storageServiceType == StorageServiceType.BLOB) {
                    fragmentTag = BlobListFragment.class.getName();
                    item = blobContainersAdapter.getItem(position);
                } else if (storageServiceType == StorageServiceType.TABLE) {
                    fragmentTag = TableEntitiesFragment.class.getName();
                    item = storageTablesAdapter.getItem(position);
                }

                final AzureStorageAccount account = storageAccountAdapter.getItem(navMenuHeaderSpinner.getSelectedItemPosition());
                final Fragment fragment = MainActivity.this.getSupportFragmentManager().findFragmentByTag(fragmentTag);
                if (fragment != null) {
                    final Object itemFinal = item;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ((ISpinnerNavListener) fragment).selectionChanged(account, itemFinal);
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

        credentialStore =
                new SharedPreferencesCredentialStore(getApplicationContext(),
                        "AzureStorageExplorerApplication", JacksonFactory.getDefaultInstance());

        final OAuthManager.OAuthCallback<Credential> classicCallback = new OAuthManager.OAuthCallback<Credential>() {
            @Override
            public void run(OAuthManager.OAuthFuture<Credential> future) {
                try {
                    Credential credential = future.getResult();
                    String accessToken = credential.getAccessToken();
                    AzureStorageExplorerApplication.accessToken.put(Constants.CLASSIC_AZURE_AUTHORIZE_URL, accessToken);

                    //now that we have access tokens for both ARM and Classic..start with fetching the subscriptions
                    fetchAzureSubscriptions();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        final OAuthManager.OAuthCallback<Credential> callback = new OAuthManager.OAuthCallback<Credential>() {
            @Override
            public void run(OAuthManager.OAuthFuture<Credential> future) {
                try {
                    Credential credential = future.getResult();
                    String accessToken = credential.getAccessToken();
                    AzureStorageExplorerApplication.accessToken.put(Constants.AZURE_AUTHORIZE_URL, accessToken);

                    authenticate(Constants.CLASSIC_AZURE_AUTHORIZE_URL, classicCallback, "classic");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        //kick-off requesting the access token for the Azure RM API first
        authenticate(Constants.AZURE_AUTHORIZE_URL, callback, "arm");
    }

    private void authenticate(final String authorizeUrl, OAuthManager.OAuthCallback callback, String credentialStoreUserId) {
        AuthorizationFlow.Builder builder = new AuthorizationFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                new GenericUrl(Constants.AZURE_TOKEN_URL),
                null,
                Constants.AZURE_AD_APP_CLIENT_ID,
                authorizeUrl);
        builder.setCredentialStore(credentialStore);
        AuthorizationFlow authorizationFlow = builder.build();

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
                        //setting this to true will cause the oauth flow to prompt the user for password, for each resource that the token is being requested for
                        return false;
                    }

                };

        OAuthManager oauth = new OAuthManager(authorizationFlow, controller);


        try {
            oauth.authorizeImplicitly(credentialStoreUserId, callback, null);
        } catch (CancellationException ex) {
            //ignore
            Toast.makeText(MainActivity.this, "Sign-in process canceled.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAzureSubscriptions() {
        ArrayList<AzureSubscription> subscriptions = AzureStorageExplorerApplication.getCustomSQLiteHelper().getAzureSubscriptions();
        setupStorageAccountsInNavigationView();

        //if we have already cached the subscriptions, don't fetch them again...let the user tap the "sync" icon to initiate a refresh
        if (subscriptions.size() > 0) {
            return;
        }

        //show a progress dialog..this is going to take some time
        progressDialogFragment.show(getSupportFragmentManager(), ProgressDialogFragment.class.getName());

        Request request = new Request.Builder()
                .url(String.format("%s?api-version=2015-01-01", Constants.AZURE_LIST_SUBSCRIPTIONS))
                .addHeader("Authorization", String.format("Bearer %s", AzureStorageExplorerApplication.accessToken.get(Constants.AZURE_AUTHORIZE_URL)))
                .addHeader("Content-Type", "application/json")
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

                final ARMSubscriptions subscriptions = gson.fromJson(response.body().charStream(), ARMSubscriptions.class);

                //start a new thread to insert all subscriptions into SQLite
                new Thread(new AzureSubscriptionSQLiteRunnable(subscriptions.getValue())).start();
                new Thread(new AzureSubscriptionFilterSQLiteRunnable(subscriptions.getValue())).start();

                fetchStorageAccounts(subscriptions.getValue());
            }
        });
    }

    private void fetchStorageAccounts(List<ARMSubscription> subscriptions) {
        ArrayList<AzureStorageAccount> accounts = AzureStorageExplorerApplication.getCustomSQLiteHelper().getAzureAccounts();
        //return if we already cached storage accounts..let the user explicity initiate a sync
        if (accounts.size() > 0) {
            progressDialogFragment.dismiss();
            return;
        }

        for (ARMSubscription subscription : subscriptions) {
            fetchResourceManagerStorageAccounts(subscription);
            fetchClassicStorageAccounts(subscription);
        }
    }

    private void fetchResourceManagerStorageAccounts(ARMSubscription subscription) {
        Request request = new Request.Builder()
                .url(String.format("%s?api-version=2016-01-01&$top=1000", String.format(Constants.AZURE_STORAGE_RESOURCE_MANAGER, subscription.getSubscriptionId())))
                .addHeader("Authorization", String.format("Bearer %s", AzureStorageExplorerApplication.accessToken.get(Constants.AZURE_AUTHORIZE_URL)))
                .addHeader("Content-Type", "application/json")
                .addHeader("subscriptionID", subscription.getSubscriptionId())
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
                final ARMStorageServices storageServices = gson.fromJson(response.body().charStream(), ARMStorageServices.class);

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
                        Log.e(TAG, exceptionMessage);
                    }
                })).start();
            }
        });
    }

    private void fetchClassicStorageAccounts(ARMSubscription subscription) {
        Request request = new Request.Builder()
                .url(String.format(Constants.LEGACY_AZURE_LIST_STORAGE_ACCOUNTS, subscription.getSubscriptionId()))
                .addHeader("Authorization", String.format("Bearer %s", AzureStorageExplorerApplication.accessToken.get(Constants.CLASSIC_AZURE_AUTHORIZE_URL)))
                .addHeader("x-ms-version", Constants.LEGACY_AZURE_API_VERSION)
                .addHeader("subscriptionID", subscription.getSubscriptionId())
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
                new Thread(new LegacyStorageKeyRestClient(storageServices, call.request().header("subscriptionID"), new IAsyncTaskCallback<AzureStorageAccount>() {
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

    private void setupStorageAccountsInNavigationView() {
        ArrayList<AzureStorageAccount> accounts = AzureStorageExplorerApplication.getCustomSQLiteHelper().getFilteredAzureAccounts();
        storageAccountAdapter = new StorageAccountAdapter(getApplicationContext(), accounts);

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //disable menu icon tinting
        navigationView.setItemIconTintList(null);
        //setup the spinner in the drawer layout header
        navMenuHeaderSpinner = (ReSelectableSpinner) navigationView.getHeaderView(0).findViewById(R.id.navBarHeaderSpinner);
        navMenuHeaderSpinner.setAdapter(storageAccountAdapter);
        navMenuHeaderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }, 300);
                //reset the fragment stack in case the user navigated into blob containers
                resetBlobListFragmentStack();

                if (storageAccountAdapter.getCount() == 0) {
                    //TODO: no storage accounts found and yet a selection happened? Perhaps because of fast-filtering?
                    return;
                }

                final AzureStorageAccount account = storageAccountAdapter.getItem(position);
                final StorageServiceType storageServiceTypeFinal = storageServiceType;

                if (account.getKey() == null) {
                    //fetch the key for this account first
                    ARMStorageServices storageServices = new ARMStorageServices();
                    storageServices.setValues(new ARMStorageService[]{new ARMStorageService(account.getName())});
                    if (account.getSubscriptionId() != null) {
                        new Thread(new StorageKeyRestClient(storageServices, account.getSubscriptionId(), new IAsyncTaskCallback<AzureStorageAccount>() {
                            @Override
                            public void finished(final AzureStorageAccount refreshedStorageAccount) {
                                //update the storage account in the SQLite DB with the key
                                AzureStorageExplorerApplication.getCustomSQLiteHelper().updateStorageAccount(refreshedStorageAccount);
                                //now get the containers for this storage account
                                if (storageServiceTypeFinal == StorageServiceType.BLOB) {
                                    BlobContainerListAsyncTask containerListAsyncTask = new BlobContainerListAsyncTask(MainActivity.this);
                                    containerListAsyncTask.execute(refreshedStorageAccount.getName(), refreshedStorageAccount.getKey());
                                } else if (storageServiceTypeFinal == StorageServiceType.TABLE) {
                                    StorageTablesAsyncTask storageTablesAsyncTask = new StorageTablesAsyncTask(MainActivity.this);
                                    storageTablesAsyncTask.execute(refreshedStorageAccount.getName(), refreshedStorageAccount.getKey());
                                }
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
                            if (storageServiceTypeFinal == StorageServiceType.BLOB) {
                                BlobContainerListAsyncTask containerListAsyncTask = new BlobContainerListAsyncTask(MainActivity.this);
                                containerListAsyncTask.execute(account.getName(), account.getKey());
                            } else if (storageServiceTypeFinal == StorageServiceType.TABLE) {
                                StorageTablesAsyncTask storageTablesAsyncTask = new StorageTablesAsyncTask(MainActivity.this);
                                storageTablesAsyncTask.execute(account.getName(), account.getKey());
                            }
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

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawer(GravityCompat.START);
            }
        }, 200);

        if (id == R.id.signOutMenuItem) {
            //delete the currently selected account
            Bundle args = new Bundle();
            args.putString("title", getString(R.string.sign_out_dialog_title));
            args.putString("message", getString(R.string.sign_out_dialog_confirmation_message));
            args.putInt("requestCode", SIGN_OUT_REQUEST_CODE);
            ConfirmationDialogFragment signOutDialogFragment = new ConfirmationDialogFragment();
            signOutDialogFragment.setArguments(args);
            signOutDialogFragment.show(getSupportFragmentManager(), ConfirmationDialogFragment.class.getName());
        } else if (id == R.id.tablesMenuItem) {
            storageServiceType = StorageServiceType.TABLE;
            //user may have navigated into a virtual directory, so reset the stack
            resetBlobListFragmentStack();
            //remove the blob list fragment and add the tables fragment
            final String fragmentTag = TableEntitiesFragment.class.getName();
            final Fragment tableEntitiesFragment = TableEntitiesFragment.instantiate(getApplicationContext(), fragmentTag);
            ActivityUtils.replaceFragment(getSupportFragmentManager(), R.id.contentFrame, tableEntitiesFragment, fragmentTag, fragmentStack);
            //re-trigger a storage account selection
            navMenuHeaderSpinner.setSelection(navMenuHeaderSpinner.getSelectedItemPosition());
        } else if (id == R.id.blobsMenuItem) {
            storageServiceType = StorageServiceType.BLOB;
            //remove the blob list fragment and add the tables fragment
            final String fragmentTag = BlobListFragment.class.getName();
            final Fragment blobListFragment = BlobListFragment.instantiate(getApplicationContext(), fragmentTag);
            ActivityUtils.replaceFragment(getSupportFragmentManager(), R.id.contentFrame, blobListFragment, fragmentTag, fragmentStack);
            //re-trigger a storage account selection
            navMenuHeaderSpinner.setSelection(navMenuHeaderSpinner.getSelectedItemPosition());
        }

        return true;
    }

    @Override
    public void onStorageAccountAdded(AzureStorageAccount account) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogFragment.dismiss();
            }
        });

        storageAccountAdapter.add(account);
        storageAccountAdapter.notifyDataSetChanged();
    }

    @Override
    public void finished(ArrayList<?> result) {
        if (result.get(0) instanceof CloudBlobContainerSerializable) {
            //received a list of blob containers..load them into the toolbar's spinner
            blobContainersAdapter = new BlobContainersAdapter(getApplicationContext(), (ArrayList<CloudBlobContainerSerializable>) result);
            toolbarSpinner.setAdapter(blobContainersAdapter);
        } else if (result.get(0) instanceof StorageTableSerializable) {
            //received a list of blob containers..load them into the toolbar's spinner
            storageTablesAdapter = new StorageTablesAdapter(getApplicationContext(), (ArrayList<StorageTableSerializable>) result);
            toolbarSpinner.setAdapter(storageTablesAdapter);
        }

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

    @SuppressWarnings("deprecation")
    @Override
    public void onConfirmationDialogPositiveClick(int requestCode) {
        if (requestCode == SIGN_OUT_REQUEST_CODE) {
            progressDialogFragment.show(getSupportFragmentManager(), ProgressDialogFragment.class.getName());
            //first remove all blob list fragments
            resetBlobListFragmentStack();

            //clear all shared prefs
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("AzureStorageExplorerApplication", MODE_PRIVATE);
            prefs.edit().clear().commit();
            //clear SQLite
            AzureStorageExplorerApplication.getCustomSQLiteHelper().clearAllData();
            //clear the adapters
            storageAccountAdapter.clear();
            blobContainersAdapter.clear();

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();

            progressDialogFragment.dismiss();
        }
    }

    @Override
    public void onConfirmationDialogNegativeClick(int requestCode) {

    }

    @Override
    public void onSubscriptionSelectionChanged(AzureSubscriptionFilter item) {
        ArrayList<AzureStorageAccount> accounts = AzureStorageExplorerApplication.getCustomSQLiteHelper().getFilteredAzureAccounts();
        storageAccountAdapter.replaceDataset(accounts);
        navMenuHeaderSpinner.setSelection(0);
    }

    @Override
    public void onTableEntityClicked(DynamicTableEntity tableEntity) {
        //TODO: does the activity want to handle this?
    }
}
