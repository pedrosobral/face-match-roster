package edu.csuchico.facematchroster;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;


/**
 * Created by aliansari on 4/4/15.
 */
@DynamoDBTable(tableName = "csuchico_master")

public class Student {
    private String userid;
    private long timestamp;
    private String name;
    private String email;
    private String mnemonic;
    private String schoolName;
    private String s3PicLoc;

    @DynamoDBIndexHashKey(attributeName = "unx_timestamp")
    public long getTimeStamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBHashKey(attributeName = "school_userid")
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @DynamoDBIndexRangeKey(attributeName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute(attributeName = "mnemonic")
    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @DynamoDBAttribute(attributeName = "school_name")
    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @DynamoDBAttribute(attributeName = "s3_pic_loc")
    public String getS3PicLoc() {
        return s3PicLoc;
    }

    public void setS3PicLoc(String s3PicLoc) {
        this.s3PicLoc = s3PicLoc;
    }
}
