package edu.csuchico.facematchroster.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.csuchico.facematchroster.R;

public class ClassesActivity extends BaseActivity {
    public ClassesActivity() {
        // to prevent to call this activity again
        // see BaseActivity.DrawerItemClickListener
        mLastSelectedPosition = NAVDRAWER_ITEM_CLASSES;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_classes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
