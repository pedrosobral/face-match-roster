package edu.csuchico.facematchroster.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.StudentLogin;
import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;
import edu.csuchico.facematchroster.model.Deck;

import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class ClassesActivity extends BaseActivity {
    private static final String TAG = makeLogTag(ClassesActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        findViewById(R.id.test_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ClassesActivity.this, FlashcardActivity.class));
                ActivityTransitionAnimation.slide(ClassesActivity.this, ActivityTransitionAnimation.LEFT);
            }
        });
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

    public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

        private List<Deck> mDataModel = Collections.emptyList();

        private LayoutInflater mInflater;

        // ViewHolder class to save inflated views for recycling
        class ViewHolder extends RecyclerView.ViewHolder {

            public TextView mTextView;

            public ViewHolder(View itemView) {
                super(itemView);

                mTextView = (TextView) findViewById(R.id.deckName);
            }
        }

        public DeckAdapter(Context context, List<Deck> data) {
            mInflater = LayoutInflater.from(context);
            mDataModel = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.deck_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Deck model = mDataModel.get(position);

            // TODO: Bind elements from Deck
            holder.mTextView.setText(model.getTitle());
        }

        @Override
        public int getItemCount() {
            return mDataModel.size();
        }
    }
}
