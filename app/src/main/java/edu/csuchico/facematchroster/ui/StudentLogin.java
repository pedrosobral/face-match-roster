package edu.csuchico.facematchroster.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;
import edu.csuchico.facematchroster.model.Student;
import edu.csuchico.facematchroster.util.AccountUtils;
import edu.csuchico.facematchroster.util.AmazonAwsUtils;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.LOGE;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class StudentLogin extends BaseActivity implements AmazonAwsUtils.SaveToCognitoHelper.OnCognitoResult {
    private static final String TAG = makeLogTag(StudentLogin.class);

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static boolean sIsButtonNext = true;
    @InjectView(R.id.schoolForm)
    EditText mSchool;
    @InjectView(R.id.submitButton)
    Button mSubmitButton;
    @InjectView(R.id.imageView)
    ImageView mImageView;
    @InjectView(R.id.editImageView)
    TextView mEditImageView;
    @InjectView(R.id.linearLayout_photo)
    LinearLayout mLinearLayoutPhoto;
    @InjectView(R.id.linearLayout_school)
    LinearLayout mLinearLayoutSchool;
    private Bitmap mImageBitmap;

    /**
     * source: http://stackoverflow.com/questions/4455558/allow-user-to-select-camera-or-gallery-for-image/
     */
    @OnClick(R.id.imageView)
    public void dispatchGetPictureIntent() {
        // Camera intent
        final List<Intent> cameraIntents = new ArrayList<>();
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
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, REQUEST_IMAGE_GALLERY);
    }

    @OnClick(R.id.submitButton)
    public void save() {

        if (sIsButtonNext) {        // call next form (e.g school form)
            uploadPhoto();          // save photo
            mLinearLayoutPhoto.setVisibility(View.GONE);
            mLinearLayoutSchool.setVisibility(View.VISIBLE);
            mSubmitButton.setText("Done");
            sIsButtonNext = false;
        } else {                  // form is complete
            final MaterialDialog materialDialog =
                    new MaterialDialog.Builder(StudentLogin.this)
                            .title("Signing in...")
                            .cancelable(false)
                            .content("Your phone is contacting our servers")
                            .progress(true, 0).build();

            AmazonAwsUtils.SaveToCognitoHelper saveToCognitoHelper = AmazonAwsUtils.SaveToCognitoHelper.
                    saveToCognitoWithDialog(StudentLogin.this, materialDialog, StudentLogin.this);

            Student student = new Student();
            student.setUserid(AccountUtils.getActiveAccountName(StudentLogin.this));
            student.setTimestamp(System.currentTimeMillis());
            student.setName(AccountUtils.getPlusName(StudentLogin.this));
            student.setEmail(AccountUtils.getActiveAccountName(StudentLogin.this));
            student.setSchoolName(mSchool.getText().toString());
            student.setS3PicLoc(AmazonAwsUtils.getS3PhotoLink(this));

            saveToCognitoHelper.execute(student);
        }
    }

    @Override
    public void saveToCognitoResult(boolean result) {
        if (!result) {
            new MaterialDialog.Builder(StudentLogin.this)
                    .title("Signing in...")
                    .content("Can't connect")
                    .positiveText("OK")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(StudentLogin.this)
                    .title("Hello " + AccountUtils.getPlusName(StudentLogin.this))
                    .content("Welcome to FaceMatch Roster")
                    .positiveText("Next")
                    .cancelable(false)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                            startActivity(new Intent(StudentLogin.this, ClassesActivity.class));
                            finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_loggin);
        ButterKnife.inject(this);

        sIsButtonNext = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) { // from gallery
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    mImageView.setImageBitmap(mImageBitmap);
                    mEditImageView.setVisibility(View.VISIBLE);
                    mSubmitButton.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else { // from camera
                Bundle extras = data.getExtras();
                if (extras != null) {
                    mImageBitmap = (Bitmap) data.getExtras().get("data");
                    mImageView.setImageBitmap(mImageBitmap);
                    mEditImageView.setVisibility(View.VISIBLE);
                    mSubmitButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * source modified: stackoverflow.com/questions/15428975/save-bitmap-into-file-and-return-file-having-bitmap-image
     *
     * @param filename
     * @return File
     */
    private File bitmapToFile(String filename) {
        final FileOutputStream outStream;

        File file = null;
        try {
            file = getTempFile(this, filename);
            LOGD(TAG, file.getAbsolutePath());
            outStream = new FileOutputStream(file.getAbsolutePath());

            if (outStream != null) {
                if (mImageBitmap != null) {
                    mImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                }
            }
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * source: developer.android.com/training/basics/data-storage/files.html
     *
     * @param context
     * @param url
     * @return File
     */
    public File getTempFile(Context context, String url) {
        File file = null;
        try {
            final String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, ".jpg", context.getCacheDir());
        } catch (IOException e) {
            LOGE(TAG, e.toString());
        }
        return file;
    }

    private void uploadPhoto() {
        final TransferManager transferManager = AmazonAwsUtils.getTransferManager(this);

        final String bucketName = AmazonAwsUtils.BUCKET_NAME;

        final String imageFileName = getImageFileName();
        final String key = AmazonAwsUtils.SCHOOL_NAME_FOLDER + imageFileName;

        final File file = bitmapToFile(imageFileName);

        AsyncTask task = new AsyncTask() {
            @Override
            protected Void doInBackground(Object... objects) {
                transferManager.upload(bucketName, key, file);
                return null;
            }
        };
        task.execute();
    }

    private String getImageFileName() {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());

        final String filename = AccountUtils.getActiveAccountName(this) + "_" + timeStamp + "_";

        // save photo filename
        AmazonAwsUtils.setPhotoFileName(this, filename);

        return filename;
    }

    @Override
    public void finish() {
        super.finish();
        ActivityTransitionAnimation.
                slide(StudentLogin.this, ActivityTransitionAnimation.Direction.RIGHT);
    }
}
