package edu.csuchico.facematchroster;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


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
    }

    @Override
    public void onClick(View view) {

        
    }
}
