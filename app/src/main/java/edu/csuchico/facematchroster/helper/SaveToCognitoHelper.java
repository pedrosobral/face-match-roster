package edu.csuchico.facematchroster.helper;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import edu.csuchico.facematchroster.model.ClassModel;
import edu.csuchico.facematchroster.model.Student;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

/**
 * Created by Pedro Henrique on 5/1/15 - 7:26 PM.
 */
public class SaveToCognitoHelper extends AsyncTask<Object, Integer, Boolean> {

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

            //Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    mContext, // Context
                    "us-east-1:bd3ecd92-f22f-4dc0-a0b5-bcc79294044b", // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );
            LOGD(TAG, "my ID is " + credentialsProvider.getIdentityId());

            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

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
        }

        return model;
    }

}
