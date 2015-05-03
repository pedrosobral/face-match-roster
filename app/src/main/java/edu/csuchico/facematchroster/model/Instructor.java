package edu.csuchico.facematchroster.model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Pedro Henrique on 5/2/15 - 11:28 PM.
 */

@DynamoDBTable(tableName = "instructor")
public class Instructor {

    private String mInstructorId;
    private String mName;
    private String mEmail;
    private long mTimestamp;

    public Instructor() {
    }

    public Instructor(String mInstructorId, String mName, String mEmail, long mTimestamp) {
        this.mInstructorId = mInstructorId;
        this.mName = mName;
        this.mEmail = mEmail;
        this.mTimestamp = mTimestamp;
    }

    @DynamoDBHashKey(attributeName = "instructor_id")
    public String getInstructorId() {
        return mInstructorId;
    }

    public void setInstructorId(String mInstructorId) {
        this.mInstructorId = mInstructorId;
    }

    @DynamoDBIndexHashKey(attributeName = "name")
    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    @DynamoDBAttribute(attributeName = "unx_timestamp")
    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long mTimestamp) {
        this.mTimestamp = mTimestamp;
    }
}
