package com.centricconsulting.azurestorageexplorer;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.centricconsulting.azurestorageexplorer.adapter.BlobContainersAdapter;
import com.centricconsulting.azurestorageexplorer.adapter.StorageAccountAdapter;
import com.centricconsulting.azurestorageexplorer.asynctask.BlobContainerListAsyncTask;
import com.centricconsulting.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;
import com.centricconsulting.azurestorageexplorer.fragments.AddAccountDialogFragment;
import com.centricconsulting.azurestorageexplorer.fragments.BlobListFragment;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.IBlobItemNavigateListener;
import com.centricconsulting.azurestorageexplorer.fragments.interfaces.ISpinnerNavListener;
import com.centricconsulting.azurestorageexplorer.models.AzureStorageAccount;
import com.centricconsulting.azurestorageexplorer.util.ActivityUtils;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        AddAccountDialogFragment.OnFragmentInteractionListener,
        IAsyncTaskCallback<ArrayList<CloudBlobContainer>>,
        BlobListFragment.OnFragmentInteractionListener {

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ArrayList<AzureStorageAccount> accounts = AzureStorageExplorerApplication.getAzureStorageAccountSQLiteHelper().getAzureAccounts();
        storageAccountAdapter = new StorageAccountAdapter(getApplicationContext(), accounts);
        //setup the spinner in the drawer layout header
        navMenuHeaderSpinner = (Spinner) navigationView.getHeaderView(0).findViewById(R.id.navBarHeaderSpinner);
        navMenuHeaderSpinner.setAdapter(storageAccountAdapter);
        navMenuHeaderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final AzureStorageAccount account = storageAccountAdapter.getItem(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BlobContainerListAsyncTask containerListAsyncTask = new BlobContainerListAsyncTask(MainActivity.this);
                        containerListAsyncTask.execute(account.getName(), account.getKey());
                    }
                }).start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        blobContainersAdapter = new BlobContainersAdapter(getApplicationContext(), new ArrayList<CloudBlobContainer>() {
        });
        toolbarSpinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
        toolbarSpinner.setAdapter(blobContainersAdapter);
        toolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(findViewById(R.id.coordinatorLayout), view.getTag().toString(), Snackbar.LENGTH_LONG).show();
                final AzureStorageAccount account = storageAccountAdapter.getItem(navMenuHeaderSpinner.getSelectedItemPosition());
                final CloudBlobContainer container = blobContainersAdapter.getItem(position);
                final Fragment fragment = MainActivity.this.getSupportFragmentManager().findFragmentByTag(BlobListFragment.class.getName());
                if (fragment != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ((ISpinnerNavListener<CloudBlobContainer>) fragment).selectionChanged(account, container);
                        }
                    }).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putSerializable("fragmentListener", this);
        Fragment containerListFragment = BlobListFragment.instantiate(getApplicationContext(), BlobListFragment.class.getName(), fragmentArgs);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), containerListFragment, R.id.contentFrame, BlobListFragment.class.getName());
        fragmentStack.push(containerListFragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AzureStorageExplorerApplication.getAzureStorageAccountSQLiteHelper().close();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_manage) {
            //TODO: delete the currently selected account
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStorageAccountAdded(AzureStorageAccount account) {
        storageAccountAdapter.add(account);
        storageAccountAdapter.notifyDataSetChanged();
    }

    @Override
    public void finished(ArrayList<CloudBlobContainer> result) {
        //received a list of blob containers..load them into the toolbar's spinner
        blobContainersAdapter.replaceDataset(result);
    }

    @Override
    public void failed(String exceptionMessage) {

    }

    @Override
    public void onBlobItemClicked(ListBlobItem blobItem) {
        invalidateOptionsMenu();

        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putSerializable("fragmentListener", this);
        Fragment blobListFragment = BlobListFragment.instantiate(getApplicationContext(), BlobListFragment.class.getName(), fragmentArgs);
        ActivityUtils.addFragmentStacked(getSupportFragmentManager(), blobListFragment, R.id.contentFrame, blobItem.getUri().getPath(), fragmentStack);

        AzureStorageAccount account = storageAccountAdapter.getItem(navMenuHeaderSpinner.getSelectedItemPosition());
        ((IBlobItemNavigateListener) blobListFragment).onBlobItemClick(account, blobItem);
    }
}
