package edu.csuchico.facematchroster.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;


/**
 * Created by aliansari on 4/30/15.
 */

@Table(name = "Classes")
@DynamoDBTable(tableName = "csuchico_classes")
public class ClassModel extends Model {

    @Column(name = "class_id")
    private String mClassId;        // == class code

    @Column(name = "name")
    private String mName;           // class name

    @Column(name = "instructor_id")
    private String mInstructorId;   // == instructor email

    @Column(name = "number")
    private String mNumber;         // class number

    @Column(name = "section")
    private String mSection;        // section

    @Column(name = "term")
    private String mTerm;           // term spring 2015...

    @Column(name = "timestamp")
    private long mTimeStamp;

    public ClassModel() {
        super();
    }

    public ClassModel(String mClassId, String mName, String mInstructorId, String mNumber, String mSection, String mTerm, long mTimeStamp) {
        this.mClassId = mClassId;
        this.mName = mName;
        this.mInstructorId = mInstructorId;
        this.mNumber = mNumber;
        this.mSection = mSection;
        this.mTerm = mTerm;
        this.mTimeStamp = mTimeStamp;
    }

    @DynamoDBHashKey(attributeName = "class_id")
    public String getClassId() {
        return mClassId;
    }

    public void setClassId(String mClassId) {
        this.mClassId = mClassId;
    }

    @DynamoDBRangeKey(attributeName = "name")
    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @DynamoDBAttribute(attributeName = "instructor_id")
    public String getInstructorId() {
        return mInstructorId;
    }

    public void setInstructorId(String mInstructorId) {
        this.mInstructorId = mInstructorId;
    }

    @DynamoDBAttribute(attributeName = "number")
    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    @DynamoDBAttribute(attributeName = "section")
    public String getSection() {
        return mSection;
    }

    public void setSection(String mSection) {
        this.mSection = mSection;
    }

    @DynamoDBAttribute(attributeName = "term")
    public String getTerm() {
        return mTerm;
    }

    public void setTerm(String mTerm) {
        this.mTerm = mTerm;
    }

    @DynamoDBAttribute(attributeName = "unx_timestamp")
    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }
}
