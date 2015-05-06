package edu.csuchico.facematchroster.model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Pedro Henrique on 5/5/15 - 1:09 PM.
 */
@DynamoDBTable(tableName = "class_student")
public class ClassStudent {
    private String mClassId;
    private String mStudentId;

    public ClassStudent() {
    }

    public ClassStudent(String mClassId, String mStudentId) {
        this.mClassId = mClassId;
        this.mStudentId = mStudentId;
    }

    @DynamoDBHashKey(attributeName = "class_id")
    public String getClassId() {
        return mClassId;
    }

    public void setClassId(String mClassId) {
        this.mClassId = mClassId;
    }

    @DynamoDBRangeKey(attributeName = "student_id")
    public String getStudentId() {
        return mStudentId;
    }

    public void setStudentId(String mStudentId) {
        this.mStudentId = mStudentId;
    }
}
