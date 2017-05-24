package de.trilobytese.vocab.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.trilobytese.vocab.MainApplication;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.controller.DeckController;
import de.trilobytese.vocab.controller.FlashcardController;
import de.trilobytese.vocab.dialog.DialogInfoFragment;
import de.trilobytese.vocab.dialog.listener.OnDialogNegativeListener;
import de.trilobytese.vocab.dialog.listener.OnDialogPositiveListener;
import de.trilobytese.vocab.fragment.TrainingDataRetainedFragment;
import de.trilobytese.vocab.model.Deck;
import de.trilobytese.vocab.model.Flashcard;
import de.trilobytese.vocab.model.Training;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Training1Activity extends BaseActivity implements LoaderManager.LoaderCallbacks<Training>,
        OnDialogPositiveListener, OnDialogNegativeListener {

    private static final int REQUEST_DIALOG_FINISHED = 0;

    private static final String STATE_CURRENT_INDEX = "state_current_index";
    private static final String STATE_DISPLAY_ANSWER = "state_display_answer";
    private static final String STATE_TARGET_TOP = "state_target_top";

    private TrainingDataRetainedFragment mDataFragment;
    private Training mData;

    private CardView mCardViewTop;
    private CardView mCardViewBottom;
    private TextView mTextTop;
    private TextView mTextBottom;
    private ProgressBar mProgressBar;

    private View mLayoutLoading;
    private View mLayoutContent;

    private int mCurrentIndex = 0;
    private boolean mIsDisplayedAnswer = false;
    private boolean mIsTargetTop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(STATE_CURRENT_INDEX);
            mIsDisplayedAnswer = savedInstanceState.getBoolean(STATE_DISPLAY_ANSWER);
            mIsTargetTop = savedInstanceState.getBoolean(STATE_TARGET_TOP);
        }

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);

        mTextTop = (TextView)findViewById(R.id.textTop);
        mTextBottom = (TextView)findViewById(R.id.textBottom);
        mCardViewTop = (CardView)findViewById(R.id.cardTop);
        mCardViewBottom = (CardView)findViewById(R.id.cardBottom);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        mLayoutLoading = findViewById(R.id.layout_loading);
        mLayoutContent = findViewById(R.id.layout_content);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mDataFragment = (TrainingDataRetainedFragment) fragmentManager.findFragmentByTag("data");

        mCardViewBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mData != null) {
                    if (mIsDisplayedAnswer) {
                        nextCard();
                        showCard();
                        hideAnswer();
                        mProgressBar.setProgress(mCurrentIndex);
                    } else {
                        showAnswer();
                    }

                    if (mData.getData().size() == mCurrentIndex) {

                        DialogInfoFragment dialog = DialogInfoFragment.newInstance(REQUEST_DIALOG_FINISHED, false, null,
                                getString(R.string.dialog_training_finish_content), getString(R.string.dialog_training_finish_positive), getString(R.string.dialog_training_finish_negative));
                        dialog.setTargetFragment(mDataFragment, 0);
                        dialog.show(getSupportFragmentManager(), "dialog_finished");

                        mCardViewTop.setVisibility(View.GONE);
                        mCardViewBottom.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_training_1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_swap:
                if (mData.getData().size() > mCurrentIndex) {
                    mIsTargetTop = !mIsTargetTop;
                    if (mIsDisplayedAnswer) {
                        showCard();
                        showAnswer();
                    } else {
                        showCard();
                        hideAnswer();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (mDataFragment != null) {
                onLoadFinished(null, mDataFragment.getData());
            } else {
                getSupportLoaderManager().initLoader(0, extras, this);
            }
        }
    }

    @Override
    protected void onPause() {
        mDataFragment.setData(mData);
        super.onPause();
    }

    private void showCard() {
        if (mData.getData().size() > mCurrentIndex) {
            if (mIsTargetTop) {
                mTextTop.setText(mData.getData().get(mCurrentIndex).getTarget());
            } else {
                mTextTop.setText(mData.getData().get(mCurrentIndex).getSource());
            }
        }
    }

    private void nextCard() {
        if (mData.getData().size() > mCurrentIndex) {
            mCurrentIndex++;
        }
    }

    private void hideAnswer() {
        mIsDisplayedAnswer = false;
        mTextBottom.setText(getString(R.string.txt_showAnswer));
    }

    private void showAnswer() {
        mIsDisplayedAnswer = true;
        if (mData.getData().size() > mCurrentIndex) {
            if (mIsTargetTop) {
                mTextBottom.setText(mData.getData().get(mCurrentIndex).getSource());
            } else {
                mTextBottom.setText(mData.getData().get(mCurrentIndex).getTarget());
            }
        }
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
        switch (requestCode) {
            case REQUEST_DIALOG_FINISHED:
            finish();
            break;
        }
    }

    @Override
    public void onPositiveButtonClicked(int requestCode, long id) {
        switch (requestCode) {
            case REQUEST_DIALOG_FINISHED:
                mCurrentIndex = 0;
                mProgressBar.setProgress(0);
                showCard();
                hideAnswer();
                mCardViewTop.setVisibility(View.VISIBLE);
                mCardViewBottom.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public Loader<Training> onCreateLoader(int id, Bundle args) {
        mLayoutLoading.setVisibility(View.VISIBLE);
        if (args != null && args.containsKey("deck_id")) {
            long deckId = args.getLong("deck_id");
            FlashcardLoader loader = new FlashcardLoader(this, deckId);
            loader.forceLoad();
            return loader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Training> loader, Training data) {

        mLayoutLoading.setVisibility(View.GONE);
        mLayoutContent.setVisibility(View.VISIBLE);

        mData = data;

        if (mDataFragment == null) {
            mDataFragment = new TrainingDataRetainedFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(mDataFragment, "data").commitAllowingStateLoss();
        }

        if (mData != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null && mData.getDeck() != null) {
                actionBar.setTitle(mData.getDeck().getName());
            }

            showCard();

            if (mIsDisplayedAnswer) {
                showAnswer();
            } else {
                hideAnswer();
            }

            mProgressBar.setMax(mData.getData().size());
            mProgressBar.setProgress(mCurrentIndex);

            if (mData.getData().size() == mCurrentIndex) {
                mCardViewTop.setVisibility(View.GONE);
                mCardViewBottom.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Training> loader) {

    }

    static class FlashcardLoader extends AsyncTaskLoader<Training> {

        private long mDeckId;

        public FlashcardLoader(Context context, long deckId) {
            super(context);
            mDeckId = deckId;
        }

        @Override
        public Training loadInBackground() {
            Training training = new Training();

            FlashcardController flashcardController = MainApplication.getInstance().getFlashcardController();
            List<Flashcard> flashcards = flashcardController.getAllByDeckIdNotEmpty(mDeckId);
            Collections.shuffle(flashcards, new Random(System.nanoTime()));

            DeckController deckController = MainApplication.getInstance().getDeckController();
            Deck deck = deckController.getById(mDeckId);

            training.setDeck(deck);
            training.setData(flashcards);
            return training;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_INDEX, mCurrentIndex);
        outState.putBoolean(STATE_DISPLAY_ANSWER, mIsDisplayedAnswer);
        outState.putBoolean(STATE_TARGET_TOP, mIsTargetTop);
    }
}
