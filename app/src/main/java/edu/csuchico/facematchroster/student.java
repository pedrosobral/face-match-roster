package edu.csuchico.facematchroster;
import android.provider.ContactsContract;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

/**
 * Created by aliansari on 4/4/15.
 */
@DynamoDBTable(tableName = "csuchico_master")

public class Student {
    private String userid;
    private String timestamp;
    private String name;
    private String email;
    private String mnemonic;
    private String schoolName;
    private String s3PicLoc;

    @DynamoDBIndexRangeKey(attributeName = "unx_timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBIndexHashKey(attributeName = "school_userid")
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public void set(String name) {
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