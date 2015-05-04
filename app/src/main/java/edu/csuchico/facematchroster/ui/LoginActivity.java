package edu.csuchico.facematchroster.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.model.Instructor;
import edu.csuchico.facematchroster.util.AccountUtils;
import edu.csuchico.facematchroster.util.SaveToCognitoHelper;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class LoginActivity extends BaseActivity implements SaveToCognitoHelper.OnCognitoResult {
    private static final String TAG = makeLogTag(LoginActivity.class);

    @InjectView(R.id.dialog_layout)
    LinearLayout mDialogLayout;

    @Override
    public void onAuthSuccess(String accountName, boolean newlyAuthenticated) {
        super.onAuthSuccess(accountName, newlyAuthenticated);
    }

    @OnClick(R.id.buttonInstructor)
    public void onLoginInstructor() {
        LOGD(TAG, "Login Instructor");
        hideDialogInstructorStudentLayout();

        final MaterialDialog materialDialog =
                new MaterialDialog.Builder(LoginActivity.this)
                        .title("Signing in...")
                        .cancelable(false)
                        .content("Your phone is contacting our servers")
                        .progress(true, 0).build();

        SaveToCognitoHelper saveToCognitoHelper = SaveToCognitoHelper.
                saveToCognitoWithDialog(LoginActivity.this, materialDialog, LoginActivity.this);

        Instructor instructor = new Instructor(
                "1", // id TODO: what's gonna be the id of instructor?
                AccountUtils.getPlusName(this), // name
                AccountUtils.getActiveAccountName(this), // email
                System.currentTimeMillis()); // timestamp

        saveToCognitoHelper.execute(instructor);
    }

    @OnClick(R.id.buttonStudent)
    public void onLoginStudent() {
        LOGD(TAG, "Login Student");
        hideDialogInstructorStudentLayout();

        // TODO: sometimes the login process return null
        String userName = AccountUtils.getPlusName(LoginActivity.this);
        if (userName == null) {
            userName = "";
        }

        new MaterialDialog.Builder(LoginActivity.this)
                .title("Hello " + userName)
                .content("We are almost done. We just need a few more information")
                .positiveText("Next")
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, StudentLogin.class));
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Only show the login activity once
         */
        if (!AccountUtils.hasToken(this, AccountUtils.getActiveAccountName(this))) {
            setContentView(R.layout.activity_login);
            ButterKnife.inject(this);
        } else {
            startActivity(new Intent(this, ClassesActivity.class));
            finish();
        }
    }

    private void hideDialogInstructorStudentLayout() {
        mDialogLayout.setVisibility(View.GONE);
    }

    @Override
    public void saveToCognitoResult(boolean result) {
        if (result == false) {
            new MaterialDialog.Builder(LoginActivity.this)
                    .title("Signing in...")
                    .content("Can't connect")
                    .positiveText("OK")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            // TODO: sometimes the login process return null
            String userName = AccountUtils.getPlusName(LoginActivity.this);
            if (userName == null) {
                userName = "";
            }
            new MaterialDialog.Builder(LoginActivity.this)
                    .title("Hello " + userName)
                    .content("Welcome to FaceMatch Roster")
                    .positiveText("Next")
                    .cancelable(false)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                            startActivity(new Intent(LoginActivity.this, ClassesActivity.class));
                            finish();

                        }
                    })
                    .show();
        }
    }
}
