package de.trilobytese.vocab.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.fragment.HelpFragment;

public class HelpActivity extends BaseActivity {

    private HelpFragment mHelpFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.action_title_Help));
        FragmentManager fm = getFragmentManager();
        if (savedInstanceState == null) {
            mHelpFragment = new HelpFragment();
            fm.beginTransaction().replace(R.id.container, mHelpFragment, "pref_fragment").commit();
        } else {
            mHelpFragment = (HelpFragment) fm.findFragmentByTag("pref_fragment");
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_help;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
