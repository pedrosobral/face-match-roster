package edu.csuchico.facematchroster.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;


/**
 * Created by aliansari on 4/4/15.
 */
@DynamoDBTable(tableName = "csuchico_master")
@Table(name = "Students")
public class Student extends Model{

    @Column(name = "user_id")
    private String userid;

    @Column(name = "timestamp")
    private long timestamp;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "mnemonic")
    private String mnemonic;

    @Column(name = "school")
    private String schoolName;

    @Column(name = "photo_location")
    private String s3PicLoc;

    public Student() {
    }

    public Student(String userid, long timestamp, String name, String email, String mnemonic, String schoolName, String s3PicLoc) {
        this.userid = userid;
        this.timestamp = timestamp;
        this.name = name;
        this.email = email;
        this.mnemonic = mnemonic;
        this.schoolName = schoolName;
        this.s3PicLoc = s3PicLoc;
    }

    @DynamoDBHashKey(attributeName = "school_userid")
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @DynamoDBAttribute(attributeName = "unx_timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBAttribute(attributeName = "name")
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
