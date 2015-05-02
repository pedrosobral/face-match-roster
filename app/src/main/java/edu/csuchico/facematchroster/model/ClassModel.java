package edu.csuchico.facematchroster.model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;


/**
 * Created by aliansari on 4/30/15.
 */
@DynamoDBTable(tableName = "csuchico_master")

public class ClassModel {
    private String InstructorName;
    private long timestamp;
    private String InstructorEmail;
    private String ClassName;
    private String ClassCode;
    private String ClassSection;
    private String Semester;
    private String School;

    @DynamoDBIndexRangeKey(attributeName = "unx_timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBHashKey(attributeName = "InstructorName")
    public String getInstructorName() {
        return InstructorName;
    }

    public void setInstructorName(String InstructorName) {
        this.InstructorName = InstructorName;
    }

    @DynamoDBIndexRangeKey(attributeName = "InstructorEmail")
    public String getInstructorEmail() {
        return InstructorEmail;
    }

    public void setInstructorEmail(String InstructorEmail) {
        this.InstructorEmail = InstructorEmail;
    }

    @DynamoDBAttribute(attributeName = "ClassName")
    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String ClassName) {
        this.ClassName = ClassName;
    }

    @DynamoDBAttribute(attributeName = "ClassCode")
    public String getClassCode() {
        return ClassCode;
    }

    public void setClassCode(String ClassCode) {
        this.ClassCode = ClassCode;
    }

    @DynamoDBAttribute(attributeName = "ClassSection")
    public String getClassSection() {
        return ClassSection;
    }

    public void setClassSection(String ClassSection) {
        this.ClassSection = ClassSection;
    }

    @DynamoDBAttribute(attributeName = "Semester")
    public String getSemester() {
        return Semester;
    }

    public void setSemester(String Semester) {
        this.Semester = Semester;
    }

    @DynamoDBAttribute(attributeName = "School")
    public String getSchool() {
        return School;
    }

    public void setSchool(String School) {
        this.School = School;
    }
}
