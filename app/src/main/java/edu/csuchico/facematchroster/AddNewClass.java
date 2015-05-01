package edu.csuchico.facematchroster;

/**
 * Created by aliansari on 4/30/15.
 * This class will be called when instructor taps on Add/Create New Class
 * 1. get information from form
 private String InstructorName;
 private long timestamp;
 private String InstructorEmail;
 private String ClassName;
 private String ClassCode;
 private String ClassSection;
 private String Semester;
 private String School;

 2.check to see if table already exits
 3. if exists inform user
 3. if table dne create a new table and inform user
 */

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;
import edu.csuchico.facematchroster.model.NewClass;
import edu.csuchico.facematchroster.ui.BaseActivity;
import edu.csuchico.facematchroster.util.AccountUtils;

import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class AddNewClass extends BaseActivity {
    private static final String TAG = makeLogTag(AddNewClass.class);

    //private static final int REQUEST_IMAGE_GALLERY = 1;
    private EditText mInstructorName;
    private EditText mInstructorEmail;
    private EditText mSchool;
    private EditText mClassName;
    private EditText mClassCode;
    private EditText mClassSection;
    private EditText mSemester;
    private Button mSubmitButton;
    private ImageView mImageView;

    private View.OnClickListener mImageViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           // dispatchGetPictureIntent();
        }
    };

    private View.OnClickListener mSubmitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new saveStudentToCognitoTask().execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        mInstructorName = (EditText) findViewById(R.id.instructorName);
        mInstructorEmail = (EditText) findViewById(R.id.instructorEmail);
        mSchool = (EditText) findViewById(R.id.schoolForm);
        mClassName = (EditText) findViewById(R.id.ClassName);
        mClassCode = (EditText) findViewById(R.id.ClassCode);
        mClassSection = (EditText) findViewById(R.id.ClassSection);
        mSemester = (EditText) findViewById(R.id.Semester);
        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mImageView = (ImageView) findViewById(R.id.imageView);

        mSubmitButton.setOnClickListener(mSubmitButtonListener);
        mImageView.setOnClickListener(mImageViewListener);

        //hack for debugging only, do not use in prod
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onAuthSuccess(String accountName, boolean newlyAuthenticated) {
        super.onAuthSuccess(accountName, newlyAuthenticated);

        mInstructorName.setText(AccountUtils.getPlusName(this));
        mInstructorEmail.setText(accountName);
    }

    /*@Override
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

    *//**
     * source: http://stackoverflow.com/questions/4455558/allow-user-to-select-camera-or-gallery-for-image/
     *//*
    private void dispatchGetPictureIntent() {
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
        galleryIntent.setType("image*//*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Complete action using");

        // Add the camera options
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent, REQUEST_IMAGE_GALLERY);
    }*/

    @Override
    public void finish() {
        super.finish();
        ActivityTransitionAnimation.
                slide(AddNewClass.this, ActivityTransitionAnimation.RIGHT);
    }

    private class saveStudentToCognitoTask extends AsyncTask<Void, Void, Void> {

        final MaterialDialog materialDialog =
                new MaterialDialog.Builder(AddNewClass.this)
                        .title("Signing in...")
                        .cancelable(false)
                        .content("Your phone is contacting our servers")
                        .progress(true, 0).build();
        private boolean mDownloadError = false;

        @Override
        protected void onPreExecute() {
            materialDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //TODO: use mapper.save(student, new DynamoDBMapperConfig(new TableNameOverride(tableName)));
                // to make table name dynamic(get from input/email/consistent as table name stays same in db)
                //TODO: error checking for inputs
                //TODO: post success or failure of upload

                //Initialize the Amazon Cognito credentials provider
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(), // Context
                        "us-east-1:bd3ecd92-f22f-4dc0-a0b5-bcc79294044b", // Identity Pool ID
                        Regions.US_EAST_1 // Region
                );
                Log.d("LogTag", "my ID is " + credentialsProvider.getIdentityId());

                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

                NewClass newClass = new NewClass();
                newClass.setInstructorName(mInstructorName.getText().toString());
                newClass.setTimestamp(System.currentTimeMillis());
                newClass.setInstructorEmail(mInstructorEmail.getText().toString());
                newClass.setSchool(mSchool.getText().toString());
                newClass.setClassName(mClassName.getText().toString());
                newClass.setClassCode(mClassCode.getText().toString());
                newClass.setClassSection(mClassSection.getText().toString());
                newClass.setSemester(mSemester.getText().toString());
                Log.d("Instructor Add Class", "Add New Class populated");

                //TODO: query table to see if user exists already
                //TODO: if user exists ask if he wants to update
                mapper.save(newClass);
                //this should be used
                //mapper.save(student, new DynamoDBMapperConfig(new TableNameOverride(tableName)));
            } catch (Exception ex) {
                ex.printStackTrace();
                mDownloadError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            materialDialog.dismiss();

            if (mDownloadError) {
                new MaterialDialog.Builder(AddNewClass.this)
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
                new MaterialDialog.Builder(AddNewClass.this)
                        .title("Hello " + mInstructorName.getText().toString())
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
}

