package edu.csuchico.facematchroster.ui.student;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import edu.csuchico.facematchroster.model.Deck;
import edu.csuchico.facematchroster.ui.BaseActivity;
import edu.csuchico.facematchroster.util.AmazonAwsUtils;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class ListClasses extends BaseActivity {
    private static final String TAG = makeLogTag(ListClasses.class);

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private DeckAdapter mDeckAdapter;

    private DeckAdapter.OnItemClickListener onItemClickListener = new DeckAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Deck deck) {
            LOGD(TAG, "onItemClick: " + deck.getTitle());
        }

        @Override
        public void onIconClick(final View view) {
            LOGD(TAG, "onIconClick: " + ((TextView) view).getText());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_classes);
        ButterKnife.inject(this);

        mDeckAdapter = new DeckAdapter(getData());
        mDeckAdapter.setOnItemClickListener(onItemClickListener);

        mRecyclerView.setAdapter(mDeckAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ListClasses.this));
    }

    private List<Deck> getData() {
        List<Deck> list = new ArrayList<>();

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

        List<Deck> listDeck = new ArrayList<>();
        if (list != null) {
            LOGD(TAG, "list != null - list.size: " + list.size());
            Iterator it = list.iterator();
            ClassModel classModel;
            while (it.hasNext()) {
                classModel = (ClassModel) it.next();
                listDeck.add(new Deck(classModel.getNumber(), classModel.getName(), null, null, null));
            }
        }

        return listDeck;
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

    public static class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

        private OnItemClickListener mOnItemClickListener;
        private List<Deck> mDataModel = Collections.emptyList();

        public DeckAdapter(List<Deck> data) {
            mDataModel = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deck_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Deck deck = mDataModel.get(position);

            holder.mTextView.setText(deck.getTitle());
            holder.mIcon.setText(new Integer(deck.getId()).toString());

            holder.onBind(deck);
        }

        @Override
        public int getItemCount() {
            return mDataModel.size();
        }

        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        public interface OnItemClickListener {
            void onItemClick(Deck deck);

            void onIconClick(View view);
        }

        // ViewHolder class to save inflated views for recycling
        class ViewHolder extends RecyclerView.ViewHolder {

            @InjectView(R.id.deckName)
            TextView mTextView;
            @InjectView(R.id.icon)
            TextView mIcon;
            private Deck mDeck;

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
            }

            public void onBind(Deck deck) {
                mDeck = deck;
            }
        }
    }


}