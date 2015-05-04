package edu.csuchico.facematchroster.util;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class AmazonAwsUtils {
    /**
     * For Amazon AWS Utils
     */
    public static final String COGNITO_POOL_ID = "us-east-1:bd3ecd92-f22f-4dc0-a0b5-bcc79294044b";
    public static final String BUCKET_NAME = "allschools";
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
}