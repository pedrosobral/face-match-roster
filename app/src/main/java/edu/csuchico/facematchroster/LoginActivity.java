package edu.csuchico.facematchroster;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.csuchico.facematchroster.ui.BaseActivity;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class LoginActivity extends BaseActivity {
    private static final String TAG = makeLogTag(LoginActivity.class);

    @InjectView(R.id.dialog_layout)
    LinearLayout mDialogLayout;

    @OnClick(R.id.buttonInstructor)
    public void onLoginInstructor() {
        LOGD(TAG, "Login Instructor");
        hideDialogInstructorStudentLayout();
    }

    @OnClick(R.id.buttonStudent)
    public void onLoginStudent() {
        LOGD(TAG, "Login Student");
        hideDialogInstructorStudentLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    private void hideDialogInstructorStudentLayout() {
        mDialogLayout.setVisibility(View.GONE);
    }
}
