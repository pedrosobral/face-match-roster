package edu.csuchico.facematchroster.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.StudentLogin;
import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;

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

        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent intent = new Intent(ClassesActivity.this, FlashcardActivity.class);
                startActivity(intent);
                ActivityTransitionAnimation.slide(this, ActivityTransitionAnimation.FADE);
            }
                break;
            case R.id.debug: {
                Intent intent = new Intent(ClassesActivity.this, StudentLogin.class);
                startActivity(intent);
                ActivityTransitionAnimation.slide(this, ActivityTransitionAnimation.LEFT);
            }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
