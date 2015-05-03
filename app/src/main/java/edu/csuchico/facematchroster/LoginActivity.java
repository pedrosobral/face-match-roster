package edu.csuchico.facematchroster;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.csuchico.facematchroster.ui.BaseActivity;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;


public class LoginActivity extends BaseActivity {

    private static final String TAG = makeLogTag(LoginActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

}
