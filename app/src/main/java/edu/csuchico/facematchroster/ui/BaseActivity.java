package edu.csuchico.facematchroster.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;

public class BaseActivity extends ActionBarActivity {

    // symbols for navDrawer items (indices must correspond to array below). This is
    // not a list of items that are necessarily *present* in the Nav Drawer; rather,
    // it's a list of all possible items.
    protected static final int NAVDRAWER_ITEM_CLASSES = 0;
    protected static final int NAVDRAWER_ITEM_ADD_CLASS = 1;
    protected static final int NAVDRAWER_ITEM_SETTINGS = 2;
    protected static final int NAVDRAWER_ITEM_HELP = 4; // 3 separator
    protected static final int NAVDRAWER_ITEM_FEEDBACK = 5;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
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
