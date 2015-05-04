package edu.csuchico.facematchroster.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

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
}