package edu.csuchico.facematchroster.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;

import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;
import edu.csuchico.facematchroster.util.AccountUtils;
import edu.csuchico.facematchroster.util.LoginAndAuthHelper;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.LOGE;
import static edu.csuchico.facematchroster.util.LogUtils.LOGW;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class BaseActivity extends ActionBarActivity implements LoginAndAuthHelper.Callbacks {
    // symbols for navDrawer items (indices must correspond to array below). This is
    // not a list of items that are necessarily *present* in the Nav Drawer; rather,
    // it's a list of all possible items.
    protected static final int NAVDRAWER_ITEM_CLASSES = 0;
    protected static final int NAVDRAWER_ITEM_ADD_CLASS = 1;
    protected static final int NAVDRAWER_ITEM_SETTINGS = 2;
    protected static final int NAVDRAWER_ITEM_HELP = 4; // 3 separator
    protected static final int NAVDRAWER_ITEM_FEEDBACK = 5;
    private static final String TAG = makeLogTag(BaseActivity.class);
    //    protected static final int NAVDRAWER_ITEM_INVALID = -1;
//    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
//    protected static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;
    // icons for navDrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[]{
            R.drawable.ic_drawer_classes,   // Classes
            R.drawable.ic_drawer_add_class, // Add Class
            R.drawable.ic_drawer_settings,  // Settings
            0,                              // separator
            R.drawable.ic_drawer_help,      // Help
            R.drawable.ic_drawer_feedback   // Feedback
    };
    protected int mLastSelectedPosition;
    // the LoginAndAuthHelper handles signing in to Google Play Services and OAuth
    private LoginAndAuthHelper mLoginAndAuthHelper;
    private String[] mNavDrawerItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerClosed(View drawerView) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerClosed(drawerView);
            // TODO: change title when closed
            getSupportActionBar().setTitle(R.string.toolbar_main_title);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerStateChanged(newState);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerOpened(drawerView);
            getSupportActionBar().setTitle(R.string.toolbar_main_title);
        }
    };
    private Toolbar mActionBarToolbar;

    @Override
    protected void onStart() {
        super.onStart();

        startLoginProcess();
    }

    /**
     * Returns the default account on the device. We use the rule that the first account
     * should be the default. It's arbitrary, but the alternative would be showing an account
     * chooser popup which wouldn't be a smooth first experience with the app. Since the user
     * can easily switch the account with the nav drawer, we opted for this implementation.
     */
    private String getDefaultAccount() {
        // Choose first account on device.
        LOGD(TAG, "Choosing default account (first account on device)");
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts.length == 0) {
            // No Google accounts on device.
            LOGW(TAG, "No Google accounts on device; not setting default account.");
            return null;
        }

        LOGD(TAG, "Default account is: " + accounts[0].name);
        return accounts[0].name;
    }

    private void complainMustHaveGoogleAccount() {
        LOGD(TAG, "Complaining about missing Google account.");
        new AlertDialog.Builder(this)
                .setTitle(R.string.google_account_required_title)
                .setMessage(R.string.google_account_required_message)
                .setPositiveButton(R.string.add_account, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        promptAddAccount();
                    }
                })
                .setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    private void promptAddAccount() {
        Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
        startActivity(intent);
        finish();
    }

    private void startLoginProcess() {
        // TODO: need to show all accounts users has
        LOGD(TAG, "Starting login process.");
        if (!AccountUtils.hasActiveAccount(this)) {
            LOGD(TAG, "No active account, attempting to pick a default.");
            String defaultAccount = getDefaultAccount();
            if (defaultAccount == null) {
                LOGE(TAG, "Failed to pick default account (no accounts). Failing.");
                complainMustHaveGoogleAccount();
                return;
            }
            LOGD(TAG, "Default to: " + defaultAccount);
            AccountUtils.setActiveAccount(this, defaultAccount);
        }

        if (!AccountUtils.hasActiveAccount(this)) {
            LOGD(TAG, "Can't proceed with login -- no account chosen.");
            return;
        } else {
            LOGD(TAG, "Chosen account: " + AccountUtils.getActiveAccountName(this));
        }

        String accountName = AccountUtils.getActiveAccountName(this);
        LOGD(TAG, "Chosen account: " + AccountUtils.getActiveAccountName(this));

        if (mLoginAndAuthHelper != null && mLoginAndAuthHelper.getAccountName().equals(accountName)) {
            LOGD(TAG, "Helper already set up; simply starting it.");
            mLoginAndAuthHelper.start();
            return;
        }

        LOGD(TAG, "Starting login process with account " + accountName);

        if (mLoginAndAuthHelper != null) {
            LOGD(TAG, "Tearing down old Helper, was " + mLoginAndAuthHelper.getAccountName());
            if (mLoginAndAuthHelper.isStarted()) {
                LOGD(TAG, "Stopping old Helper");
                mLoginAndAuthHelper.stop();
            }
            mLoginAndAuthHelper = null;
        }

        LOGD(TAG, "Creating and starting new Helper with account: " + accountName);
        mLoginAndAuthHelper = new LoginAndAuthHelper(this, this, accountName);
        mLoginAndAuthHelper.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onStop() {
        LOGD(TAG, "onStop");
        super.onStop();
        if (mLoginAndAuthHelper != null) {
            mLoginAndAuthHelper.stop();
        }
    }

    private void setupNavDrawer() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            mDrawerList = (ListView) findViewById(R.id.drawer_list);
            if (mDrawerList != null) {
                mNavDrawerItems = getResources().getStringArray(R.array.navDrawer_items);
                // Set the adapter for the list view
                mDrawerList.setAdapter(new NavDrawerItemAdapter(this, mNavDrawerItems));
                // Set the list's click listener
                mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
            }

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mActionBarToolbar,
                    R.string.open_content_drawer, R.string.close_content_drawer);

            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onPlusInfoLoaded(String accountName) {

    }

    @Override
    public void onAuthSuccess(String accountName, boolean newlyAuthenticated) {
        LOGD(TAG, "onAuthSuccess, account " + accountName + ", newlyAuthenticated=" + newlyAuthenticated);
    }

    @Override
    public void onAuthFailure(String accountName) {
        LOGD(TAG, "Auth failed for account " + accountName);
    }

    protected void retryAuth() {
        mLoginAndAuthHelper.retryAuthByUserRequest();
    }

    public static class NavDrawerItemAdapter extends ArrayAdapter<String> {

        public NavDrawerItemAdapter(Context context, String[] objects) {
            super(context, R.layout.navdrawer_item, R.id.title, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            String item = getItem(position);

            View view = null;
            if (item.isEmpty()) {    // separator
                return inflater.inflate(R.layout.navdrawer_separator, parent, false);
            }

            view = inflater.inflate(R.layout.navdrawer_item, parent, false);

            if (view != null) {
                ((TextView) view.findViewById(R.id.title)).setText(item);
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(NAVDRAWER_ICON_RES_ID[position]);
            }

            return view;
        }
    }

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int item, long l) {
            mDrawerLayout.closeDrawer(Gravity.START);

            if (item != mLastSelectedPosition) { // if so start a new activity
                mLastSelectedPosition = item;

                Intent intent;
                switch (item) {
                    case NAVDRAWER_ITEM_CLASSES:
                        intent = new Intent(getBaseContext(), ClassesActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case NAVDRAWER_ITEM_ADD_CLASS:
                        intent = new Intent(getBaseContext(), AddClassActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case NAVDRAWER_ITEM_SETTINGS:
                        intent = new Intent(getBaseContext(), SettingsActivity.class);
                        startActivity(intent);
                        ActivityTransitionAnimation.slide(BaseActivity.this, ActivityTransitionAnimation.LEFT);
                        mLastSelectedPosition = NAVDRAWER_ITEM_CLASSES; // Settings back to classes
                        break;
                    case NAVDRAWER_ITEM_HELP:
                        intent = new Intent(getBaseContext(), HelpActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case NAVDRAWER_ITEM_FEEDBACK:
                        intent = new Intent(getBaseContext(), FeedbackActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        }
    }
}
