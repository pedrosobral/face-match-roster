package edu.csuchico.facematchroster.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gc.materialdesign.views.ButtonRectangle;

import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.StudentLogin;
import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;

public class ClassesActivity extends BaseActivity {
    private static final String TAG = "facematch_" + ClassesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        findViewById(R.id.test_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ClassesActivity.this, FlashcardActivity.class));
                ActivityTransitionAnimation.slide(ClassesActivity.this, ActivityTransitionAnimation.LEFT);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (sIsDrawerOpen) {
            return false;
        }
        getMenuInflater().inflate(R.menu.menu_classes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getDrawerToggle() != null && getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_settings: {

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
