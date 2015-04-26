package edu.csuchico.facematchroster.ui;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.StudentLogin;
import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;
import edu.csuchico.facematchroster.model.Deck;

import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class ClassesActivity extends BaseActivity {
    private static final String TAG = makeLogTag(ClassesActivity.class);

    private RecyclerView mRecyclerView;
    private DeckAdapter mDeckAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mDeckAdapter = new DeckAdapter(getData());

        mRecyclerView.setAdapter(mDeckAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ClassesActivity.this));

    }

    private List<Deck> getData() {
        List<Deck> listDeck = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            listDeck.add(new Deck(i, "Class: " + i, null, null, null));
        }
        return listDeck;
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

            }
            break;
            case R.id.debug: {
                Intent intent = new Intent(ClassesActivity.this, StudentLogin.class);
                startActivity(intent);
                ActivityTransitionAnimation.slide(this, ActivityTransitionAnimation.LEFT);
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

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
        }

        @Override
        public int getItemCount() {
            return mDataModel.size();
        }

        // ViewHolder class to save inflated views for recycling
        class ViewHolder extends RecyclerView.ViewHolder {

            public TextView mTextView;

            public ViewHolder(View itemView) {
                super(itemView);

                mTextView = (TextView) itemView.findViewById(R.id.deckName);
            }
        }
    }
}
