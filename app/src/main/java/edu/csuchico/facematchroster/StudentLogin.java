package edu.csuchico.facematchroster;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;
import edu.csuchico.facematchroster.util.SaveToCognitoHelper;
import edu.csuchico.facematchroster.model.Student;
import edu.csuchico.facematchroster.ui.BaseActivity;
import edu.csuchico.facematchroster.util.AccountUtils;

import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class StudentLogin extends BaseActivity implements SaveToCognitoHelper.OnCognitoResult {
    private static final String TAG = makeLogTag(StudentLogin.class);

    private static final int REQUEST_IMAGE_GALLERY = 1;
    @InjectView(R.id.nameForm) EditText mName;
    @InjectView(R.id.mnemonicForm) EditText mMnemonic;
    @InjectView(R.id.schoolForm) EditText mSchool;
    @InjectView(R.id.emaiForm) EditText mEmail;
    @InjectView(R.id.idForm) EditText mId;
    @InjectView(R.id.imageView) ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_loggin);
        ButterKnife.inject(this);
    }

    @Override
    public void onAuthSuccess(String accountName, boolean newlyAuthenticated) {
        super.onAuthSuccess(accountName, newlyAuthenticated);

        mName.setText(AccountUtils.getPlusName(this));
        mEmail.setText(accountName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) { // from gallery
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    mImageView.setImageBitmap(imageBitmap);
                    (findViewById(R.id.editImageView)).setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else { // from camera
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    mImageView.setImageBitmap(imageBitmap);
                    (findViewById(R.id.editImageView)).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * source: http://stackoverflow.com/questions/4455558/allow-user-to-select-camera-or-gallery-for-image/
     */
    @OnClick(R.id.imageView)
    public void dispatchGetPictureIntent() {
        // Camera intent
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.MEDIA_IGNORE_FILENAME, ".nomedia");

            cameraIntents.add(intent);
        }

        // Filesystem intent
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Complete action using");

        // Add the camera options
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void finish() {
        super.finish();
        ActivityTransitionAnimation.
                slide(StudentLogin.this, ActivityTransitionAnimation.RIGHT);
    }

    @OnClick(R.id.submitButton)
    public void save() {
        final MaterialDialog materialDialog =
                new MaterialDialog.Builder(StudentLogin.this)
                        .title("Signing in...")
                        .cancelable(false)
                        .content("Your phone is contacting our servers")
                        .progress(true, 0).build();

        SaveToCognitoHelper saveToCognitoHelper = SaveToCognitoHelper.
                saveToCognitoWithDialog(StudentLogin.this, materialDialog, StudentLogin.this);

        Student student = new Student();
        student.setUserid(mId.getText().toString());
        student.setTimestamp(System.currentTimeMillis());
        student.setName(mName.getText().toString());
        student.setEmail(mEmail.getText().toString());
        student.setMnemonic(mMnemonic.getText().toString());
        student.setSchoolName(mSchool.getText().toString());

        saveToCognitoHelper.execute(student);
    }

    @Override
    public void saveToCognitoResult(boolean result) {
        if (result == false) {
            new MaterialDialog.Builder(StudentLogin.this)
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
            new MaterialDialog.Builder(StudentLogin.this)
                    .title("Hello " + mName.getText().toString())
                    .content("Welcome to FaceMatch Roster")
                    .positiveText("Next")
                    .cancelable(false)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                            finish();

                        }
                    })
                    .show();
        }
    }
}
