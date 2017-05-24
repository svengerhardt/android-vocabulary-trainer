package de.trilobytese.vocab;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;
import de.trilobytese.vocab.activity.MainActivity;
import de.trilobytese.vocab.controller.CategoryController;
import de.trilobytese.vocab.controller.DeckController;
import de.trilobytese.vocab.controller.FlashcardController;
import de.trilobytese.vocab.model.Category;
import de.trilobytese.vocab.model.Deck;
import de.trilobytese.vocab.model.Flashcard;
import de.trilobytese.vocab.util.DatabaseUtils;
import de.trilobytese.vocab.util.ScreenUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LaunchActivity extends Activity {

    private static final String TAG = LaunchActivity.class.getSimpleName();

    private CategoryController mCategoryController;
    private DeckController mDeckController;
    private FlashcardController mFlashcardController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.lockScreenOrientation(this);
        setContentView(R.layout.activity_launch);

        mCategoryController = MainApplication.getInstance().getCategoryController();
        mDeckController = MainApplication.getInstance().getDeckController();
        mFlashcardController = MainApplication.getInstance().getFlashcardController();

        new InitTask().execute();
    }

    private class InitTask extends AsyncTask<Void, Void, Void> {

        private boolean databaseExists = false;

        @Override
        protected void onPreExecute() {
            databaseExists = DatabaseUtils.databaseExists(LaunchActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (!databaseExists) {
                populateDatabase();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            launchMainActivity();
        }
    }

    private void populateDatabase() {

        mCategoryController.add(new Category(getString(R.string.menu_navigation_primary)));

        String language = Locale.getDefault().getLanguage();
        AssetManager assetManager = getAssets();

        String root = null;
        String[] files = null;

        try {
            root = "flashcards/" + language;
            files = assetManager.list(root);
        } catch (IOException ex) {
            try {
                root = "flashcards/en";
                files = assetManager.list("flashcards/en");
            } catch (IOException ex2) {
                Log.e(TAG, ex2.getMessage());
            }
        }

        if (files != null) {

            for (String file : files) {
                try {
                    CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(root + "/" + file)), ',');
                    if (reader != null) {
                        List<String[]> entries = reader.readAll();
                        Deck deck = new Deck(file.replace("_", " ").replace(".csv", ""), Category.PRIMARY_CATEGORY_ID);
                        mDeckController.add(deck);
                        List<Flashcard> flashcards = new ArrayList<>();
                        for (String[] entry : entries) {
                            if (entry.length > 1) {
                                if (entry[0] != null && entry[1] != null) {
                                    String c1 = entry[0].trim();
                                    String c2 = entry[1].trim();
                                    if (!c1.isEmpty() && !c2.isEmpty()) {
                                        flashcards.add(new Flashcard(c1, c2, deck.getId()));
                                    }
                                }
                            }
                        }
                        mFlashcardController.insert(flashcards);
                    }
                } catch (IOException ex) {
                    Log.e(TAG, "IOException", ex);
                }
            }
        }
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
