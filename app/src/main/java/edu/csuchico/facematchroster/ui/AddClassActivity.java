package edu.csuchico.facematchroster.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.anim.ActivityTransitionAnimation;
import edu.csuchico.facematchroster.model.ClassModel;
import edu.csuchico.facematchroster.util.AccountUtils;
import edu.csuchico.facematchroster.util.AmazonAwsUtils;

import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class AddClassActivity extends BaseActivity implements AmazonAwsUtils.SaveToCognitoHelper.OnCognitoResult {

    private static final String TAG = makeLogTag(AddClassActivity.class);

    @InjectView(R.id.name_class)
    EditText mClassName;
    @InjectView(R.id.class_code)
    EditText mClassCode;
    @InjectView(R.id.class_number)
    EditText mClassNumber;
    @InjectView(R.id.class_section)
    EditText mClassSection;
    @InjectView(R.id.school_term_spinner)
    Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        ButterKnife.inject(this);

        setupSpinner();
    }

    private void setupSpinner() {
        // TODO: must be generated automatically
        String[] schoolTermList = new String[]{"Spring 2015", "Fall 2015", "Summer 2015"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, schoolTermList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_class, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            save();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void save() {
        ClassModel newClass = new ClassModel(
                mClassCode.getText().toString(),
                mClassName.getText().toString(),
                AccountUtils.getActiveAccountName(this),
                mClassNumber.getText().toString(),
                mClassSection.getText().toString(),
                mSpinner.getSelectedItem().toString(),
                System.currentTimeMillis()
        );

        final MaterialDialog materialDialog =
                new MaterialDialog.Builder(AddClassActivity.this)
                        .title("Saving...")
                        .cancelable(false)
                        .content("Your phone is contacting our servers")
                        .progress(true, 0).build();

        AmazonAwsUtils
                .SaveToCognitoHelper
                .saveToCognitoWithDialog(AddClassActivity.this, materialDialog, AddClassActivity.this)
                .execute(newClass);
    }

    @Override
    public void saveToCognitoResult(boolean result) {
        if (result == true) {
            Toast.makeText(AddClassActivity.this, "Class saved", Toast.LENGTH_LONG).show();
            finish();
            ActivityTransitionAnimation.slide(AddClassActivity.this, ActivityTransitionAnimation.Direction.LEFT);
        } else {
            Toast.makeText(AddClassActivity.this, "Can't save", Toast.LENGTH_LONG).show();
        }
    }
}
