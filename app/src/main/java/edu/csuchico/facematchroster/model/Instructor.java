package edu.csuchico.facematchroster.model;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Pedro Henrique on 5/2/15 - 11:28 PM.
 */

@DynamoDBTable(tableName = "instructor")
@Table(name = "Instructor")
public class Instructor {

    @Column(name = "instructor_id")
    private String mEmail; // = id

    @Column(name = "name")
    private String mName;

    @Column(name = "timestamp")
    private long mTimestamp;

    public Instructor() {
    }

    public Instructor(String mEmail, String mName, long mTimestamp) {
        this.mEmail = mEmail;
        this.mName = mName;
        this.mTimestamp = mTimestamp;
    }

    @DynamoDBHashKey(attributeName = "instructor_id")
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    @DynamoDBIndexHashKey(attributeName = "name")
    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @DynamoDBAttribute(attributeName = "unx_timestamp")
    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long mTimestamp) {
        this.mTimestamp = mTimestamp;
    }
}
