package de.trilobytese.vocab.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVReader;
import de.trilobytese.vocab.Constants;
import de.trilobytese.vocab.MainApplication;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.controller.DeckController;
import de.trilobytese.vocab.controller.FlashcardController;
import de.trilobytese.vocab.model.Category;
import de.trilobytese.vocab.model.Deck;
import de.trilobytese.vocab.model.Flashcard;
import de.trilobytese.vocab.util.*;

import java.io.*;
import java.util.List;

public class ImportCSVActivity extends Activity {

    private static final String TAG = ImportCSVActivity.class.getSimpleName();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_EXTERNAL_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private DeckController mDeckController;
    private FlashcardController mFlashCardController;

    private View mViewLoading;
    private View mViewLoadingError;

    private TextView mTextError;
    private Button mButtonError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.lockScreenOrientation(this);
        setContentView(R.layout.activity_import);

        mDeckController = MainApplication.getInstance().getDeckController();
        mFlashCardController = MainApplication.getInstance().getFlashcardController();

        mViewLoading = findViewById(R.id.layout_loading);
        mViewLoading.setVisibility(View.VISIBLE);

        mViewLoadingError = findViewById(R.id.layout_loading_error);
        mViewLoadingError.setVisibility(View.GONE);

        mTextError = (TextView)findViewById(R.id.txtError);
        mTextError.setText(getString(R.string.txt_loading_error_open_file));
        mButtonError = (Button)findViewById(R.id.btnError);
        mButtonError.setText(getString(R.string.txt_loading_error_button));
        mButtonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            executeImport();
        } else {
            ActivityCompat.requestPermissions(ImportCSVActivity.this, PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                executeImport();
            } else {
                mViewLoading.setVisibility(View.GONE);
                mViewLoadingError.setVisibility(View.VISIBLE);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void executeImport() {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                Uri data = getIntent().getData();
                if(data != null) {
                    try {
                        importCSV(getIntent());
                        return true;
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "FileNotFoundException", e);
                        return false;
                    } catch (IOException e) {
                        Log.e(TAG, "IOException", e);
                        return false;
                    } catch (RuntimeException e) {
                        Log.e(TAG, "RuntimeException", e);
                        return false;
                    }
                } else {
                    Log.w(TAG, "data==null");
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    launchMainActivity();
                } else {
                    mViewLoading.setVisibility(View.GONE);
                    mViewLoadingError.setVisibility(View.VISIBLE);
                }
            }

        }.execute();
    }

    private void importCSV(Intent intent) throws IOException, RuntimeException {

        CSVReader reader = null;
        String deckName = "Vocab_" + TimeUtils.getDate(System.currentTimeMillis());

        if (intent.getScheme().equals("content")) {
            Uri uri = intent.getData();
            String name = ImportUtils.getNameFromMediaStore(this, uri);
            if (name != null) {
                deckName = name;
            }
            ContentResolver cr = getContentResolver();
            InputStream is = cr.openInputStream(uri);
            if(is == null) return;
            reader = new CSVReader(new InputStreamReader(is), ',');
        } else if (intent.getScheme().equals("file")) {
            String path = intent.getData().getPath();
            deckName = FilenameUtils.getName(path);
            reader = new CSVReader(new FileReader(path), ',');
        }

        if (reader != null) {
            int maxLength = getResources().getInteger(R.integer.flashcard_max_text_length);
            List<String[]> entries = reader.readAll();
            deckName = FilenameUtils.removeExtension(deckName).trim();
            Deck deck = new Deck(deckName, Category.PRIMARY_CATEGORY_ID);
            mDeckController.add(deck);

            int count = 0;

            for (String[] entry : entries) {
                if (entry.length > 1) {
                    if (entry[0] != null && entry[1] != null) {
                        String c1 = entry[0].trim();
                        String c2 = entry[1].trim();
                        if (!c1.isEmpty() && !c2.isEmpty()) {
                            if (c1.length() > maxLength) {
                                c1 = c1.substring(0, maxLength);
                            }
                            if (c2.length() > maxLength) {
                                c2 = c2.substring(0, maxLength);
                            }
                            mFlashCardController.add(new Flashcard(c1, c2, deck.getId()));
                        }
                    }
                }

                count++;

                if (count > Constants.DATABASE_MAX_ROWS) {
                    break;
                }
            }
        }
    }

    private void launchMainActivity() {
        Intent intent = new Intent(ImportCSVActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}