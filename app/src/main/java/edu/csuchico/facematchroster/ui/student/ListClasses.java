package edu.csuchico.facematchroster.ui.student;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.model.ClassModel;
import edu.csuchico.facematchroster.model.ClassStudent;
import edu.csuchico.facematchroster.ui.BaseActivity;
import edu.csuchico.facematchroster.util.AccountUtils;
import edu.csuchico.facematchroster.util.AmazonAwsUtils;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class ListClasses extends BaseActivity implements AmazonAwsUtils.SaveToCognitoHelper.OnCognitoResult {
    private static final String TAG = makeLogTag(ListClasses.class);
    private final ArrayList<String> classes_id = new ArrayList<>();
    private final ArrayList<String> classes_enrolled = new ArrayList<>();
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;
    private ClassAdapter mClassAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ClassAdapter.OnItemClickListener onItemClickListener = new ClassAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(ClassModel classModel) {
            LOGD(TAG, "onItemClick: " + classModel.getName());
        }

        @Override
        public void onIconClick(final View view) {
            LOGD(TAG, "onIconClick: " + ((TextView) view).getText());
        }

        @Override
        public void onEnrollClick(View view, ClassModel classModel) {
//            ((Button) view).setText("Enrolled");
            saveStudentOnClass(classModel);
        }
    };
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            // update data and refresh
            List<ClassModel> classModels = getData();

            mClassAdapter.updateData(classModels, classes_enrolled);
            // dismiss swipeRefresh layout
            mSwipeRefresh.setRefreshing(false);
        }
    };


    private void saveStudentOnClass(ClassModel aClass) {

        ClassStudent classStudent = new ClassStudent(
                aClass.getClassId(), // class id
                AccountUtils.getActiveAccountName(ListClasses.this) // student id
        );

        // TODO: should be saved after save on cognito
        classStudent.save(); // save class on database

        AmazonAwsUtils.SaveToCognitoHelper saveToCognitoHelper = AmazonAwsUtils
                .SaveToCognitoHelper
                .saveToCognitoWithoutDialog(ListClasses.this, ListClasses.this);

        saveToCognitoHelper.execute(classStudent);
    }

    private void updateButtonState(List<ClassModel> classes) {
        LOGD(TAG, "updateButtonState");

        // get all class student enrolled on SQLite
        final List<ClassStudent> list = new Select().all().from(ClassStudent.class).execute();

        if (list != null && list.size() == 0)
            return;

        final Iterator it = classes.iterator();
        // get all classes id from DynamoDB
        while (it.hasNext()) {
            ClassModel classModel = (ClassModel) it.next();
            classes_id.add(classModel.getClassId());
            LOGD(TAG, "dynamo Class_id: " + classModel.getClassId());
        }

        final Iterator ids = list.iterator();
        // compare each class id form DynamoDB with ids student enrolled
        while (ids.hasNext()) {
            String id = ((ClassStudent) ids.next()).getClassId();
            LOGD(TAG, "1 sql class_id: " + id);
            if (classes_id.contains(id)) {
                LOGD(TAG, "student enrolled class_id: " + id);
                classes_id.remove(id);
                classes_enrolled.add(id);
            }
        }
        // class_id contain all classes_id student enrolled
    }

    @Override
    public void saveToCognitoResult(boolean result) {
        if (result) {
            List<ClassModel> classModels = getData();
            mClassAdapter.updateData(classModels, classes_enrolled);

            Toast.makeText(ListClasses.this, "Enrolled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ListClasses.this, "Can't enroll", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_classes);
        ButterKnife.inject(this);

        List<ClassModel> classModels = getData();

        mClassAdapter = new ClassAdapter(classModels, classes_enrolled);
        mClassAdapter.setOnItemClickListener(onItemClickListener);

        mRecyclerView.setAdapter(mClassAdapter);
        mLinearLayoutManager = new LinearLayoutManager(ListClasses.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // refresh
        mSwipeRefresh.setOnRefreshListener(onRefreshListener);
    }

    private List<ClassModel> getData() {
        List<ClassModel> list = new ArrayList<>();

        // empty scan to get all classes
        final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        final DynamoDBMapper mapper = AmazonAwsUtils.getDynamoDBMapper(this);

        AsyncTask<Void, Void, List> task = new AsyncTask<Void, Void, List>() {
            @Override
            protected List doInBackground(Void... voids) {
                return mapper.scan(ClassModel.class, scanExpression);
            }
        };

        try {
            list = task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        List<ClassModel> listClasses = new ArrayList<>();
        if (list != null) {
            LOGD(TAG, "list != null - list.size: " + list.size());
            Iterator it = list.iterator();
            ClassModel classModel;
            while (it.hasNext()) {
                classModel = (ClassModel) it.next();
                listClasses.add(classModel);
//                classModel.save(); // save class on database
            }
        }

        updateButtonState(list);

        return listClasses;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (getDrawerLayout() != null && getDrawerLayout().isDrawerOpen(Gravity.LEFT)) {
            return false;
        }
        getMenuInflater().inflate(R.menu.menu_list_classes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getDrawerToggle() != null && getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {

        }

        return super.onOptionsItemSelected(item);
    }

    public static class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

        private OnItemClickListener mOnItemClickListener;
        private List<ClassModel> mDataModel = Collections.emptyList();
        private ArrayList<String> classesEnrolled;

        public ClassAdapter(List<ClassModel> data, ArrayList<String> aClassesEnrolled) {
            mDataModel = data;
            classesEnrolled = aClassesEnrolled;
        }

        public void updateData(List<ClassModel> data, ArrayList<String> aClassesEnrolled) {
            mDataModel = data;
            classesEnrolled = aClassesEnrolled;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ClassModel classModel = mDataModel.get(position);

            holder.mTextView.setText(classModel.getName());
            holder.mIcon.setText(classModel.getNumber());

            String class_id = classModel.getClassId();
            LOGD(TAG, "1 onBindView: " + class_id);
            if (classesEnrolled != null && classesEnrolled.contains(class_id)) {
                LOGD(TAG, "2 onBindView: " + class_id);
                holder.enrollButton.setText("Enrolled");
                holder.enrollButton.setTextColor(Color.GREEN);
                classesEnrolled.remove(class_id);
            }

            holder.onBind(classModel);
        }

        @Override
        public int getItemCount() {
            return mDataModel.size();
        }

        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        public interface OnItemClickListener {
            void onItemClick(ClassModel classModel);

            void onIconClick(View view);

            void onEnrollClick(View view, ClassModel classModel);
        }

        // ViewHolder class to save inflated views for recycling
        class ViewHolder extends RecyclerView.ViewHolder {

            @InjectView(R.id.deckName)
            TextView mTextView;
            @InjectView(R.id.icon)
            TextView mIcon;
            @InjectView(R.id.buttonEnroll)
            Button enrollButton;

            private ClassModel mDeck;

            public ViewHolder(View itemView) {
                super(itemView);

                ButterKnife.inject(this, itemView);

                mIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onIconClick(mIcon);
                        }
                    }
                });
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(mDeck);
                        }
                    }
                });
                enrollButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onEnrollClick(view, mDeck);
                        }
                    }
                });
            }

            public void onBind(ClassModel deck) {
                mDeck = deck;
            }
        }
    }
}