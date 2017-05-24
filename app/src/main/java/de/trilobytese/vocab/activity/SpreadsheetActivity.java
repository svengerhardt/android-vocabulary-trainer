package de.trilobytese.vocab.activity;

import android.app.Activity;
import android.os.Bundle;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.fragment.SpreadsheetFragment;

public class SpreadsheetActivity extends BaseActivity {

    private SpreadsheetFragment mSpreadsheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpreadsheetFragment = (SpreadsheetFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_spreadsheet);
        mSpreadsheetFragment.setToolbar(mToolbar);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_spreadsheet;
    }

    @Override
    public void finish() {
        if (mSpreadsheetFragment.isUpdated()) {
            setResult(Activity.RESULT_OK, getIntent());
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        super.finish();
    }
}
