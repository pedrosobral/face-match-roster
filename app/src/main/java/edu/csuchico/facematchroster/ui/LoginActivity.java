package edu.csuchico.facematchroster.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.SignInButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.model.Instructor;
import edu.csuchico.facematchroster.ui.instructor.ClassesActivity;
import edu.csuchico.facematchroster.ui.student.ListClasses;
import edu.csuchico.facematchroster.ui.student.StudentLogin;
import edu.csuchico.facematchroster.util.AccountUtils;
import edu.csuchico.facematchroster.util.AmazonAwsUtils;
import edu.csuchico.facematchroster.util.GoogleLogin;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class LoginActivity extends GoogleLogin implements AmazonAwsUtils.SaveToCognitoHelper.OnCognitoResult {
    private static final String TAG = makeLogTag(LoginActivity.class);

    /**
     * Passed as extra to intent's when login occur
     */
    public static final String FROM_LOGIN_ACTIVITY = "from_login_acitivity";

    @InjectView(R.id.dialog_layout)
    LinearLayout mDialogLayout;
    @InjectView(R.id.sign_in_button)
    SignInButton signInButton;

    @OnClick(R.id.sign_in_button)
    public void onSignInClick() {
        connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        signInButton.setVisibility(View.GONE);
        showDialogInstructorStudentLayout();
    }

    @OnClick(R.id.buttonInstructor)
    public void onLoginInstructor() {
        LOGD(TAG, "Login Instructor");

        AccountUtils.setInstructorAccount(LoginActivity.this, true);

        hideDialogInstructorStudentLayout();

        final MaterialDialog materialDialog =
                new MaterialDialog.Builder(LoginActivity.this)
                        .title("Signing in...")
                        .cancelable(false)
                        .content("Your phone is contacting our servers")
                        .progress(true, 0).build();

        AmazonAwsUtils.SaveToCognitoHelper saveToCognitoHelper = AmazonAwsUtils.SaveToCognitoHelper.
                saveToCognitoWithDialog(LoginActivity.this, materialDialog, LoginActivity.this);

        Instructor instructor = new Instructor(
                AccountUtils.getActiveAccountName(this), // email = id
                AccountUtils.getPlusName(this), // name
                System.currentTimeMillis()); // timestamp

        saveToCognitoHelper.execute(instructor);
    }

    @OnClick(R.id.buttonStudent)
    public void onLoginStudent() {
        LOGD(TAG, "Login Student");
        hideDialogInstructorStudentLayout();

        AccountUtils.setInstructorAccount(LoginActivity.this, false);

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
                        Intent intent = new Intent(LoginActivity.this, StudentLogin.class);
                        intent.putExtra(FROM_LOGIN_ACTIVITY, true);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Only show the login activity once
         */
        if (!AccountUtils.hasActiveAccount(this)) {
            setContentView(R.layout.activity_login);
            ButterKnife.inject(this);
        } else if (AccountUtils.isInstructor(LoginActivity.this)) { // instructor account
            startActivity(new Intent(this, ClassesActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, ListClasses.class));     // student account
            finish();
        }
    }

    private void hideDialogInstructorStudentLayout() {
        mDialogLayout.setVisibility(View.GONE);
    }

    private void showDialogInstructorStudentLayout() {
        mDialogLayout.setVisibility(View.VISIBLE);
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
                            Intent intent = new Intent(LoginActivity.this, ClassesActivity.class);
                            intent.putExtra(FROM_LOGIN_ACTIVITY, true);
                            startActivity(intent);
                            finish();

                        }
                    })
                    .show();
        }
    }
}
