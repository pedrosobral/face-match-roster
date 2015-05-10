package edu.csuchico.facematchroster.ui.instructor;

import android.content.Intent;
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
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;
import edu.csuchico.facematchroster.model.ClassModel;
import edu.csuchico.facematchroster.model.Deck;
import edu.csuchico.facematchroster.ui.BaseActivity;
import edu.csuchico.facematchroster.ui.student.ListClasses;
import edu.csuchico.facematchroster.ui.student.StudentLogin;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class ClassesActivity extends BaseActivity {
    public static final String CLASS_ID = "class_id";
    public static final String ClASS_NAME = "class_name";
    private static final String TAG = makeLogTag(ClassesActivity.class);
    private static final int ADD_CLASS_REQUEST = 1;
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.fab)
    FloatingActionsMenu mFloatActionMenu;
    @InjectView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private DeckAdapter mDeckAdapter;

    private DeckAdapter.OnItemClickListener onItemClickListener = new DeckAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Deck deck) {
            LOGD(TAG, "onItemClick: " + deck.getTitle());
            Intent intent = new Intent(ClassesActivity.this, FlashcardActivity.class);
            intent.putExtra(CLASS_ID, deck.getClassId());
            intent.putExtra(ClASS_NAME, deck.getTitle());
            startActivity(intent);
        }

        @Override
        public void onIconClick(final View view) {
            LOGD(TAG, "onIconClick: " + ((TextView) view).getText());
        }
    };
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mDeckAdapter.updateData(getData());
            // dismiss swipeRefresh layout
            mSwipeRefresh.setRefreshing(false);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LOGD(TAG, "onActivityResult()");
        if (requestCode == ADD_CLASS_REQUEST) {
            if (resultCode == RESULT_OK) {
                LOGD(TAG, "onActivityResult: updating...");
                // get the new class added from database
                mDeckAdapter.updateData(getData());
            }
        }
    }

    @OnClick(R.id.add_class)
    public void onAddClass() {
        startActivityForResult(new Intent(ClassesActivity.this, AddClassActivity.class),
                ADD_CLASS_REQUEST);
        mFloatActionMenu.collapse();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);
        ButterKnife.inject(this);
        mDeckAdapter = new DeckAdapter(getData());
        mDeckAdapter.setOnItemClickListener(onItemClickListener);

        mRecyclerView.setAdapter(mDeckAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ClassesActivity.this));

        // refresh
        mSwipeRefresh.setOnRefreshListener(onRefreshListener);
    }

    private List<Deck> getData() {

        List<Deck> listDeck = new ArrayList<>();
        Iterator it = getDataFromDataBase().iterator();
        ClassModel aClass;
        while (it.hasNext()) {
            aClass = (ClassModel) it.next();
            Deck deck = new Deck(aClass.getName(), aClass.getClassId(), null, null);
            listDeck.add(deck);
            deck.save(); // save deck database
            aClass.save();
        }

        return listDeck;
    }

    private List<ClassModel> getDataFromDataBase() {
        return new Select()
                .from(ClassModel.class)
                .execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (getDrawerLayout() != null && getDrawerLayout().isDrawerOpen(Gravity.LEFT)) {
            return false;
        }
        getMenuInflater().inflate(R.menu.menu_classes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getDrawerToggle() != null && getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_settings: {
                startActivity(new Intent(ClassesActivity.this, ListClasses.class));
            }
            break;
            case R.id.debug: {
                Intent intent = new Intent(ClassesActivity.this, StudentLogin.class);
                startActivity(intent);
                ActivityTransitionAnimation.slide(this, ActivityTransitionAnimation.Direction.LEFT);
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

        private OnItemClickListener mOnItemClickListener;
        private List<Deck> mDataModel = Collections.emptyList();

        public DeckAdapter(List<Deck> data) {
            mDataModel = data;
        }

        public void updateData(List<Deck> data) {
            mDataModel = data;
            notifyDataSetChanged();
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
            holder.mIcon.setText(Integer.valueOf(deck.getClassId()).toString());

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
