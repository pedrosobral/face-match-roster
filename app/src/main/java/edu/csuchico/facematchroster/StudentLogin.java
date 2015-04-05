package edu.csuchico.facematchroster;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.model.*;


public class StudentLogin extends Activity implements View.OnClickListener {

    private EditText mName;
    private EditText mMnemonic;
    private EditText mSchool;
    private EditText mEmail;
    private EditText mId;
    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_loggin);

        mName = (EditText) findViewById(R.id.nameForm);
        mMnemonic = (EditText) findViewById(R.id.mnemonicForm);
        mSchool = (EditText) findViewById(R.id.schoolForm);
        mEmail = (EditText) findViewById(R.id.emaiForm);
        mId = (EditText) findViewById(R.id.idForm);
        mSubmitButton = (Button) findViewById(R.id.submitButton);

        mSubmitButton.setOnClickListener(this);

        //hack for debugging only, do not use in prod
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

    }

    @Override
    public void onClick(View view) {
    //TODO: upload pic to s3 and get s3 loc of pic
    //TODO: use mapper.save(student, new DynamoDBMapperConfig(new TableNameOverride(tableName)));
    // to make table name dynamic(get from input)
    //TODO: error checking for inputs

        //new thread to upload to DynDB
        //should we use async instead of new thread?
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    //Initialize the Amazon Cognito credentials provider
                    CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                            getApplicationContext(), // Context
                            "us-east-1:bd3ecd92-f22f-4dc0-a0b5-bcc79294044b", // Identity Pool ID
                            Regions.US_EAST_1 // Region
                    );
                    Log.d("LogTag", "my ID is " + credentialsProvider.getIdentityId());

                    AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                    DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

                    Student student = new Student();
                    student.setUserid(mId.getText().toString());
                    student.setTimestamp(System.currentTimeMillis());
                    student.setName(mName.getText().toString());
                    student.setEmail(mEmail.getText().toString());
                    student.setMnemonic(mMnemonic.getText().toString());
                    student.setSchoolName(mSchool.getText().toString());
                    //student.setS3PicLoc();
                    Log.d("StudentLogin","Student Class populated");

                    mapper.save(student);
                    //this should be used
                    //mapper.save(student, new DynamoDBMapperConfig(new TableNameOverride(tableName)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();


    }
}
