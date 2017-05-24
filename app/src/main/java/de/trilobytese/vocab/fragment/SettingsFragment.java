package de.trilobytese.vocab.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import de.trilobytese.vocab.MainApplication;
import de.trilobytese.vocab.MainPreferences;
import de.trilobytese.vocab.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference mPrefFlashcards;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mPrefFlashcards = (ListPreference)findPreference(MainPreferences.PREF_MAX_FLASHCARDS);
        mPrefFlashcards.setSummary(mPrefFlashcards.getEntry());

        Preference prefBuildVersion = findPreference("pref_build_version");
        prefBuildVersion.setSummary(MainApplication.APP_VERSION);
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(MainPreferences.PREF_MAX_FLASHCARDS)) {
            mPrefFlashcards.setSummary(mPrefFlashcards.getEntry());
        }
    }
}
