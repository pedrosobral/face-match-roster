package edu.csuchico.facematchroster.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import edu.csuchico.facematchroster.model.ClassModel;
import edu.csuchico.facematchroster.model.Instructor;
import edu.csuchico.facematchroster.model.Student;
import edu.csuchico.facematchroster.ui.student.ListClasses;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class AmazonAwsUtils {
    static final String TAG = makeLogTag(AccountUtils.class);
    /**
     * For Amazon AWS Utils
     */
    public static final String COGNITO_POOL_ID = "us-east-1:bd3ecd92-f22f-4dc0-a0b5-bcc79294044b";
    public static final String BUCKET_NAME = "allschools";
    public static final String SCHOOL_NAME_FOLDER = "csuchico/";
    public static final String PREFIX_S3_PHOTO_LINK = "https://s3-us-west-2.amazonaws.com/";

    private static final String PREFIX_PREF_PHOTO_FILENAME = "photo_filename_";
    public static CognitoCachingCredentialsProvider sCredProvider;
    public static DynamoDBMapper sDynamoDBMapper;
    public static TransferManager sTransferManager;

    /**
     * Amazon AWS Utils
     */
    public static CognitoCachingCredentialsProvider getCredentialsProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context, // Context
                    COGNITO_POOL_ID, // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );
        }
        return sCredProvider;
    }

    public static DynamoDBMapper getDynamoDBMapper(Context context) {
        if (sDynamoDBMapper == null) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(getCredentialsProvider(context));
            return new DynamoDBMapper(ddbClient);
        }
        return sDynamoDBMapper;
    }

    public static TransferManager getTransferManager(Context context) {
        if (sTransferManager == null) {
            sTransferManager = new TransferManager(getCredentialsProvider(context));
        }
        return sTransferManager;
    }

    private static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getPhotoFileName(final Context context) {
        SharedPreferences sp = AmazonAwsUtils.getSharedPreferences(context);
        return sp.getString(PREFIX_PREF_PHOTO_FILENAME, null);
    }

    public static boolean setPhotoFileName(final Context context, final String filename) {
        LOGD(TAG, "Set photo filename: " + filename);
        SharedPreferences sp = AmazonAwsUtils.getSharedPreferences(context);
        sp.edit().putString(PREFIX_PREF_PHOTO_FILENAME, filename).commit();
        return true;
    }

    public static String getS3PhotoLink(final Context context) {
        return PREFIX_S3_PHOTO_LINK
                + BUCKET_NAME + "/" + SCHOOL_NAME_FOLDER + getPhotoFileName(context);
    }

    /**
     * Created by Pedro Henrique on 5/1/15 - 7:26 PM.
     */
    public static class SaveToCognitoHelper extends AsyncTask<Object, Integer, Boolean> {

        private static final String TAG = makeLogTag(SaveToCognitoHelper.class);

        private Context mContext;
        private MaterialDialog mDialog;
        private OnCognitoResult mResult;

        private SaveToCognitoHelper(Context mContext, MaterialDialog mDialog, OnCognitoResult mResult) {
            this.mContext = mContext;
            this.mDialog = mDialog;
            this.mResult = mResult;
        }

        public static SaveToCognitoHelper saveToCognitoWithDialog(Context mContext, MaterialDialog mDialog, OnCognitoResult mResult) {
            return new SaveToCognitoHelper(mContext, mDialog, mResult);
        }

        public static SaveToCognitoHelper saveToCognitoWithoutDialog(Context mContext, OnCognitoResult mResult) {
            return new SaveToCognitoHelper(mContext, null, mResult);
        }

        public interface OnCognitoResult {
            void saveToCognitoResult(boolean result);
        }

        @Override
        protected void onPreExecute() {
            if (mDialog != null) {
                mDialog.show();
            }
        }

        protected Boolean doInBackground(Object... objects) {

            Object model = getModelToSave(objects);

            try {
                //TODO: use mapper.save(student, new DynamoDBMapperConfig(new TableNameOverride(tableName)));
                // to make table name dynamic(get from input/email/consistent as table name stays same in db)
                //TODO: error checking for inputs
                //TODO: post success or failure of upload

                DynamoDBMapper mapper = getDynamoDBMapper(mContext);

                //TODO: query table to see if user exists already
                //TODO: if user exists ask if he wants to update
                mapper.save(model);
                //this should be used
                //mapper.save(student, new DynamoDBMapperConfig(new TableNameOverride(tableName)));
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (mResult != null) {
                mResult.saveToCognitoResult(aBoolean);
            }
        }

        private Object getModelToSave(Object[] objects) {
            Object model = null;

            if (objects[0] instanceof ClassModel) {
                model = ((ClassModel) objects[0]);
            } else if (objects[0] instanceof Student) {
                model = ((Student) objects[0]);
            } else if (objects[0] instanceof Instructor) {
                model = (Instructor) objects[0];
            } else if (objects[0] instanceof ListClasses.ClassStudent) {
                model = (ListClasses.ClassStudent) objects[0];
            }

            return model;
        }

    }
}