package de.trilobytese.vocab.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    private SettingsFragment mSettingsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getString(R.string.action_title_setting));
        FragmentManager fm = getFragmentManager();
        if (savedInstanceState == null) {
            mSettingsFragment = new SettingsFragment();
            fm.beginTransaction().replace(R.id.container, mSettingsFragment, "pref_fragment").commit();
        } else {
            mSettingsFragment = (SettingsFragment) fm.findFragmentByTag("pref_fragment");
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
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
