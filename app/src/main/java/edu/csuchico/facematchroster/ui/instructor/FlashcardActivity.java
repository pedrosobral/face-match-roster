package edu.csuchico.facematchroster.ui.instructor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
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
import butterknife.OnClick;
import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;
import edu.csuchico.facematchroster.model.Card;
import edu.csuchico.facematchroster.model.ClassStudent;
import edu.csuchico.facematchroster.model.Deck;
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
    private String mName;
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showAnswerButtons();
        }
    };
    private List<Student> studentList = new ArrayList<>();
    private List<Card> mCardList;
    private int nunCards = 0;

    @OnClick(R.id.flip_card)
    public void showAnswerButtons() {
        mFlipCardLayout.setVisibility(View.GONE);

        mFlashcardName.setVisibility(View.VISIBLE);
        mFlashcardName.setText(mName);

        mFlashcardLayoutEase1.setVisibility(View.VISIBLE);
        mFlashcardLayoutEase2.setVisibility(View.VISIBLE);
        mFlashcardLayoutEase3.setVisibility(View.VISIBLE);
        mFlashcardLayoutEase4.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.flashcard_layout_ease4)
    public void onAnswerClicked() {

        mFlashcardName.setVisibility(View.GONE);

        mFlashcardLayoutEase1.setVisibility(View.GONE);
        mFlashcardLayoutEase2.setVisibility(View.GONE);
        mFlashcardLayoutEase3.setVisibility(View.GONE);
        mFlashcardLayoutEase4.setVisibility(View.GONE);

        mFlipCardLayout.setVisibility(View.VISIBLE);

        setNextCard();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard);
        ButterKnife.inject(this);

        setTitle(getIntent().getStringExtra(ClassesActivity.ClASS_NAME));

        ArrayList<String> emails = getStudentsId();
        getStudents(emails);

        mFlipCardLayout = (LinearLayout) findViewById(R.id.flashcard_layout_flip);

        mCardList = updateDeck(getIntent().getStringExtra(ClassesActivity.CLASS_ID));

        setNextCard();
    }

    /**
     * TODO: Update eFactor card based on answer
     */
    private void setNextCard() {

        if (mCardList != null && nunCards != mCardList.size()) {

            // set name
            mName = mCardList.get(nunCards).getName();

            // set Photo
            Picasso.with(this)
                    .load(mCardList.get(nunCards).getPhoto())
                    .fit()
                    .centerInside()
                    .into(mPhoto);

            nunCards++;
        } else {
            finish();
            ActivityTransitionAnimation.slide(FlashcardActivity.this, ActivityTransitionAnimation.Direction.LEFT);
        }
    }

    private List<Card> updateDeck(String id) {

        Deck deck = new Select()
                .from(Deck.class)
                .where("class_id = ?", id)
                .executeSingle();

        // TODO: should save cards on database and verify when
        // is necessary to update form the dynamoDB
        if (studentList != null /* && !isDataBaseUpdate(deck)*/) {
            Iterator it = studentList.iterator();

            // clear database
            new Delete().from(Card.class).where("Id != null").execute();

            while (it.hasNext()) {
                Student student = (Student) it.next();
                Card card = new Card(
                        student.getS3PicLoc(),  // TODO: should be a file?
                        student.getName(),      // name
                        deck,                   // deck
                        (float) 2.5,            // eFactor
                        null, null);            // Creation and Update Date

                card.save(); // save card on database
                LOGD(TAG, "updateDeck " + card.getName() + card.getPhoto());
            }
        }
        return deck.cards();
    }

    private boolean isDataBaseUpdate(Deck deck) {
        LOGD(TAG, "isDataUpdate id = " + deck.getClassId());
        List<Card> countList = deck.cards();

        LOGD(TAG, "count = " + countList.size() + " list: " + studentList.size());
        return countList.size() == studentList.size();
    }

    private void getStudents(List<String> emails) {
        final DynamoDBMapper mapper = AmazonAwsUtils.getDynamoDBMapper(FlashcardActivity.this);

        Student hashKeyValues = new Student();
        Iterator it = emails.iterator();

        while (it.hasNext()) {

            hashKeyValues.setUserid(it.next().toString());

            final DynamoDBQueryExpression<Student> queryExpression = new DynamoDBQueryExpression<Student>()
                    .withHashKeyValues(hashKeyValues);

            AsyncTask<Void, Void, List> task = new AsyncTask<Void, Void, List>() {
                @Override
                protected List doInBackground(Void... voids) {
                    return mapper.query(Student.class, queryExpression);
                }
            };

            List<Student> list = null;
            try {
                list = task.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (list != null && list.size() != 0)
                studentList.add(list.get(0));
        }
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
