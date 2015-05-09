package edu.csuchico.facematchroster.ui.instructor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.gc.materialdesign.views.ButtonRectangle;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.model.ClassStudent;
import edu.csuchico.facematchroster.model.Student;
import edu.csuchico.facematchroster.ui.BaseActivity;
import edu.csuchico.facematchroster.util.AmazonAwsUtils;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class FlashcardActivity extends BaseActivity {
    private static final String TAG = makeLogTag(FlashcardActivity.class);
    @InjectView(R.id.flashcard_layout_ease1)
    ButtonRectangle mFlashcardLayoutEase1;
    @InjectView(R.id.flashcard_layout_ease2)
    ButtonRectangle mFlashcardLayoutEase2;
    @InjectView(R.id.flashcard_layout_ease3)
    ButtonRectangle mFlashcardLayoutEase3;
    @InjectView(R.id.flashcard_layout_ease4)
    ButtonRectangle mFlashcardLayoutEase4;
    @InjectView(R.id.answer_field)
    ImageView mPhoto;
    @InjectView(R.id.flashcard_name)
    TextView mFlashcardName;

    private LinearLayout mFlipCardLayout;
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showAnswerButtons();
        }
    };
    private String mName;

    protected void showAnswerButtons() {
        mFlipCardLayout.setVisibility(View.GONE);

        mFlashcardName.setVisibility(View.VISIBLE);
        mFlashcardName.setText(mName);

        mFlashcardLayoutEase1.setVisibility(View.VISIBLE);
        mFlashcardLayoutEase2.setVisibility(View.VISIBLE);
        mFlashcardLayoutEase3.setVisibility(View.VISIBLE);
        mFlashcardLayoutEase4.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard);
        ButterKnife.inject(this);

        setTitle(getIntent().getStringExtra(ClassesActivity.ClASS_NAME));

        ArrayList<String> emails = getStudentsId();
        String url = setStudentPhoto(emails);

        mFlipCardLayout = (LinearLayout) findViewById(R.id.flashcard_layout_flip);
        findViewById(R.id.flip_card).setOnClickListener(listener);

        LOGD(TAG, "URL S3: " + url);
        Picasso.with(this)
                .load(url)
                .fit()
                .centerInside()
                .into(mPhoto);
    }

    private String setStudentPhoto(List<String> emails) {
        final DynamoDBMapper mapper = AmazonAwsUtils.getDynamoDBMapper(FlashcardActivity.this);

        Student hashKeyValeus = new Student();
        hashKeyValeus.setUserid(emails.get(0));

        final DynamoDBQueryExpression<Student> queryExpression = new DynamoDBQueryExpression<Student>()
                .withHashKeyValues(hashKeyValeus);

        AsyncTask<Void, Void, List> task = new AsyncTask<Void, Void, List>() {
            @Override
            protected List doInBackground(Void... voids) {
                return mapper.query(Student.class, queryExpression);
            }
        };

        List<Student> studentList = null;

        try {
            studentList = task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String url = null;
        if (studentList != null) {
            LOGD(TAG, "studentsList != null");
            Iterator it = studentList.iterator();
            Student student;
            while (it.hasNext()) {
                student = (Student) it.next();
                LOGD(TAG, "URL S3: " + student.getS3PicLoc());
                url = student.getS3PicLoc();
                mName = student.getName();
            }
        }

        return url;
    }

    private ArrayList<String> getStudentsId() {
        String class_id = getIntent().getStringExtra(ClassesActivity.CLASS_ID);
        LOGD(TAG, "class_id: " + class_id);

        final DynamoDBMapper mapper = AmazonAwsUtils.getDynamoDBMapper(FlashcardActivity.this);

        ClassStudent hashKeyValues = new ClassStudent();
        hashKeyValues.setClassId(class_id);

        final DynamoDBQueryExpression<ClassStudent> queryExpression = new DynamoDBQueryExpression<ClassStudent>()
                .withHashKeyValues(hashKeyValues);

        AsyncTask<Void, Void, List> task = new AsyncTask<Void, Void, List>() {
            @Override
            protected List doInBackground(Void... voids) {
                return mapper.query(ClassStudent.class, queryExpression);
            }
        };

        List<ClassStudent> classStudentList = null;

        try {
            classStudentList = task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ArrayList<String> emails = new ArrayList<>();
        if (classStudentList != null) {
            Iterator it = classStudentList.iterator();
            ClassStudent classStudent;
            while (it.hasNext()) {
                classStudent = (ClassStudent) it.next();
                LOGD(TAG, "email: " + classStudent.getStudentId());
                emails.add(classStudent.getStudentId());
            }
        }
        return emails;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flashcard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
