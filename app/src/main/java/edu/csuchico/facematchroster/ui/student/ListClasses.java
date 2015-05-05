package edu.csuchico.facematchroster.ui.student;

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

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.model.ClassModel;
import edu.csuchico.facematchroster.ui.BaseActivity;
import edu.csuchico.facematchroster.util.AccountUtils;
import edu.csuchico.facematchroster.util.AmazonAwsUtils;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class ListClasses extends BaseActivity implements AmazonAwsUtils.SaveToCognitoHelper.OnCognitoResult {
    private static final String TAG = makeLogTag(ListClasses.class);

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
            ((Button) view).setText("Enrolled");
            saveStudentOnClass(classModel);
        }
    };
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            // update data and refresh
            mClassAdapter.updateData(getData());
            // dismiss swipeRefresh layout
            mSwipeRefresh.setRefreshing(false);
        }
    };

    private void saveStudentOnClass(ClassModel aClass) {

        ClassStudent classStudent = new ClassStudent(
                aClass.getClassId(), // class id
                AccountUtils.getActiveAccountName(ListClasses.this) // student id
        );

        AmazonAwsUtils.SaveToCognitoHelper saveToCognitoHelper = AmazonAwsUtils
                .SaveToCognitoHelper
                .saveToCognitoWithoutDialog(ListClasses.this, ListClasses.this);

        saveToCognitoHelper.execute(classStudent);
    }

    @Override
    public void saveToCognitoResult(boolean result) {
        if (result == true) {
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

        mClassAdapter = new ClassAdapter(getData());
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
            }
        }

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

        public ClassAdapter(List<ClassModel> data) {
            mDataModel = data;
        }

        public void updateData(List<ClassModel> data) {
            mDataModel = data;
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
}