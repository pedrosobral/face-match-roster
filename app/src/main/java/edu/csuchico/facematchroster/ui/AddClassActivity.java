package edu.csuchico.facematchroster.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import edu.csuchico.facematchroster.R;
import edu.csuchico.facematchroster.util.SaveToCognitoHelper;
import edu.csuchico.facematchroster.model.ClassModel;

import static edu.csuchico.facematchroster.util.LogUtils.LOGD;
import static edu.csuchico.facematchroster.util.LogUtils.makeLogTag;

public class AddClassActivity extends BaseActivity implements SaveToCognitoHelper.OnCognitoResult {

    private static final String TAG = makeLogTag(AddClassActivity.class);

    private EditText mClassName;
    private EditText mClassNumber;
    private EditText mClassSection;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        mClassName = (EditText) findViewById(R.id.name_class);
        mClassNumber = (EditText) findViewById(R.id.class_number);
        mClassSection = (EditText) findViewById(R.id.class_section);

        setupSpinner();
    }

    private void setupSpinner() {
        mSpinner = (Spinner) findViewById(R.id.school_term_spinner);

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
        ClassModel newClass = new ClassModel("Bryan Dixon",
                System.currentTimeMillis(), "bryan@csuchico.edu", mClassName.getText().toString(),
                mClassNumber.getText().toString(), mClassSection.getText().toString(),
                mSpinner.getSelectedItem().toString(), "CSU Chico");

        SaveToCognitoHelper
                .saveToCognitoWithoutDialog(AddClassActivity.this, AddClassActivity.this)
                .execute(newClass);
    }

    @Override
    public void saveToCognitoResult(boolean result) {
        if (result == true) {
            LOGD(TAG, "saved");
            finish();
        } else {
            Toast.makeText(AddClassActivity.this, "Can't save", Toast.LENGTH_LONG).show();
        }
    }
}
