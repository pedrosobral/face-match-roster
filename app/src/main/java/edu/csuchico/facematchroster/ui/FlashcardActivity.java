package edu.csuchico.facematchroster.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.gc.materialdesign.views.ButtonRectangle;

import edu.csuchico.facematchroster.R;

import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class FlashcardActivity extends BaseActivity {
    private static final String TAG = makeLogTag(FlashcardActivity.class);

    private LinearLayout mFlipCardLayout;

    private ButtonRectangle mFlashcardLayoutEase1;
    private ButtonRectangle mFlashcardLayoutEase2;
    private ButtonRectangle mFlashcardLayoutEase3;
    private ButtonRectangle mFlashcardLayoutEase4;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showAnswerButtons();
        }
    };

    protected void showAnswerButtons() {
        mFlipCardLayout.setVisibility(View.GONE);

        mFlashcardLayoutEase1.setVisibility(View.VISIBLE);
        mFlashcardLayoutEase2.setVisibility(View.VISIBLE);
        mFlashcardLayoutEase3.setVisibility(View.VISIBLE);
        mFlashcardLayoutEase4.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard);

        mFlipCardLayout = (LinearLayout) findViewById(R.id.flashcard_layout_flip);
        findViewById(R.id.flip_card).setOnClickListener(listener);

        mFlashcardLayoutEase1 = (ButtonRectangle) findViewById(R.id.flashcard_layout_ease1);
        mFlashcardLayoutEase2 = (ButtonRectangle) findViewById(R.id.flashcard_layout_ease2);
        mFlashcardLayoutEase3 = (ButtonRectangle) findViewById(R.id.flashcard_layout_ease3);
        mFlashcardLayoutEase4 = (ButtonRectangle) findViewById(R.id.flashcard_layout_ease4);
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
