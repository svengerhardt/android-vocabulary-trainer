package de.trilobytese.vocab;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainPreferences {

    public static final String PREF_SOUND = "pref_sound";
    public static final String PREF_MAX_FLASHCARDS = "pref_max_flashcards";

    public static boolean isSoundOn() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance());
        return prefs.getBoolean(PREF_SOUND, false);
    }

    public static int getMaxFlashcards() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance());
        return Integer.valueOf(prefs.getString(PREF_MAX_FLASHCARDS, "5"));
    }
}
