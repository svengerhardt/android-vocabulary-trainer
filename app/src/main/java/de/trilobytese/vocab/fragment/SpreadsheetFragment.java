package de.trilobytese.vocab.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.PopupMenu;
import de.trilobytese.vocab.Constants;
import de.trilobytese.vocab.MainApplication;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.adapter.CardAdapter;
import de.trilobytese.vocab.controller.DeckController;
import de.trilobytese.vocab.controller.FlashcardController;
import de.trilobytese.vocab.dialog.DialogTextInputFragment;
import de.trilobytese.vocab.dialog.listener.OnDialogTextInputListener;
import de.trilobytese.vocab.model.Deck;
import de.trilobytese.vocab.model.Flashcard;
import de.trilobytese.vocab.view.BackAwareEditText;

import java.util.List;

public class SpreadsheetFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Flashcard>>, CardAdapter.Callbacks, OnDialogTextInputListener {

    private static final String TAG = SpreadsheetFragment.class.getSimpleName();

    private static final int REQUEST_DIALOG_RENAME = 1;

    private static final String STATE_ROW_SELECTED_POSITION = "state_row_selected_position";
    private static final String STATE_CELL_SELECTED_POSITION = "state_cell_selected_position";
    private static final String STATE_CELL_SELECTED_TYPE = "state_cell_selected_type";
    private static final String STATE_UPDATED = "state_updated";

    private Toolbar mToolbar;
    private FlashcardController mFlashcardController;
    private DeckController mDeckController;

    private View mLayoutLoading;
    private View mLayoutContent;
    private View mLayoutFooter;

    private ListView mListView;
    private CardAdapter mListAdapter;

    private long mDeckId;
    private Deck mDeck;

    private BackAwareEditText mEditText;
    private int mCurrentRowSelectedPosition = -1;
    private int mCurrentCellSelectedPosition = -1;
    private int mCurrentCellSelectedType;

    private boolean mUpdated = false;

    private ActionMode mActionMode;
    private InputMethodManager mInputMethodManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeckController = MainApplication.getInstance().getDeckController();
        mFlashcardController = MainApplication.getInstance().getFlashcardController();
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            mDeckId = extras.getLong("deck_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_spreadsheet, container, false);
        mLayoutLoading = rootView.findViewById(R.id.layout_loading);
        mLayoutContent = rootView.findViewById(R.id.layout_content);
        mLayoutFooter = rootView.findViewById(R.id.footer);
        mEditText = (BackAwareEditText)rootView.findViewById(R.id.editText);

        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            update();
                            mActionMode.finish();
                            return false;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        mEditText.setBackPressedListener(new BackAwareEditText.BackPressedListener() {
            @Override
            public void onImeBack(BackAwareEditText editText) {
                update();
                mEditText.clearFocus();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView)view.findViewById(R.id.list);

        if (savedInstanceState != null) {
            mCurrentRowSelectedPosition = savedInstanceState.getInt(STATE_ROW_SELECTED_POSITION);
            mCurrentCellSelectedPosition = savedInstanceState.getInt(STATE_CELL_SELECTED_POSITION);
            mCurrentCellSelectedType = savedInstanceState.getInt(STATE_CELL_SELECTED_TYPE);
            mUpdated = savedInstanceState.getBoolean(STATE_UPDATED);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mDeck = mDeckController.getById(mDeckId);
        if (mDeck != null) {
            String title = mDeck.getName();
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            getLoaderManager().initLoader(1000, extras, this);
        }
    }

    @Override
    public Loader<List<Flashcard>> onCreateLoader(int id, Bundle args) {
        mLayoutLoading.setVisibility(View.VISIBLE);
        if (args != null && args.containsKey("deck_id")) {
            long deckId = args.getLong("deck_id");
            FlashcardLoader loader = new FlashcardLoader(getActivity(), deckId);
            loader.forceLoad();
            return loader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Flashcard>> loader, List<Flashcard> data) {
        mUpdated = ((FlashcardLoader)loader).isUpdated();
        mLayoutLoading.setVisibility(View.GONE);
        mLayoutContent.setVisibility(View.VISIBLE);
        mListAdapter = new CardAdapter(getActivity(), data, this);
        mListView.setAdapter(mListAdapter);

        try {
            if (mCurrentCellSelectedPosition != -1) {
                mLayoutFooter.setVisibility(View.VISIBLE);
                mListAdapter.setCellSelected(mCurrentCellSelectedPosition, mCurrentCellSelectedType);
                mActionMode = mToolbar.startActionMode(mActionModeCallback);
            } else if (mCurrentRowSelectedPosition != -1) {
                mListAdapter.setRowSelected(mCurrentRowSelectedPosition);
                mActionMode = mToolbar.startActionMode(mActionModeCallback);
            }
        } catch (IndexOutOfBoundsException ex) {
                // ignore
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Flashcard>> loader) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_spreadsheet, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.menu_rename:
                DialogTextInputFragment dialog = DialogTextInputFragment.newInstance(REQUEST_DIALOG_RENAME,
                        null, mDeck.getName());
                dialog.setTargetFragment(SpreadsheetFragment.this, 0);
                dialog.show(getFragmentManager(), "dialog_rename");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSourceClicked(View view, Flashcard flashcard, int position) {
        update();
        mListAdapter.clearRowSelection();
        mCurrentRowSelectedPosition = -1;
        mCurrentCellSelectedPosition = position;
        mCurrentCellSelectedType = CardAdapter.TYPE_SOURCE;
        mListAdapter.setCellSelected(position, mCurrentCellSelectedType);
        updateEditText(flashcard.getSource());
    }

    @Override
    public void onItemTargetClicked(View view, Flashcard flashcard, int position) {
        update();
        mListAdapter.clearRowSelection();
        mCurrentRowSelectedPosition = -1;
        mCurrentCellSelectedPosition = position;
        mCurrentCellSelectedType = CardAdapter.TYPE_TARGET;
        mListAdapter.setCellSelected(position, mCurrentCellSelectedType);
        updateEditText(flashcard.getTarget());
    }

    @Override
    public void onItemMenuClicked(View view, final Flashcard flashcard, int position) {
        mCurrentRowSelectedPosition = position;
        mListAdapter.clearCellSelection();
        mListAdapter.setRowSelected(mCurrentRowSelectedPosition);
        mLayoutFooter.setVisibility(View.GONE);
        if (mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
        if (mActionMode == null) {
            mActionMode = mToolbar.startActionMode(mActionModeCallback);
        }

        PopupMenu mPopupMenu = new PopupMenu(getActivity(), view);
        mPopupMenu.inflate(R.menu.menu_spreadsheet_row);
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_delete:
                        if (mFlashcardController.delete(flashcard)) {
                            mListAdapter.removeItem(flashcard);
                            mListAdapter.clearRowSelection();
                            mCurrentCellSelectedPosition = -1;
                            mCurrentRowSelectedPosition = -1;
                            Flashcard tmpFlashcard = new Flashcard("", "", mDeckId);
                            if (mFlashcardController.add(tmpFlashcard)) {
                                mListAdapter.addItem(tmpFlashcard);
                            }
                        }
                        break;
                }
                return true;
            }
        });
        mPopupMenu.show();
    }

    @Override
    public void onItemDoubleClicked() {
        mEditText.requestFocus();
        mEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
        mEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
        mEditText.setSelection(mEditText.getText().length());
    }

    private void updateEditText(String text) {
        mLayoutFooter.setVisibility(View.VISIBLE);
        if (mActionMode == null) {
            mActionMode = mToolbar.startActionMode(mActionModeCallback);
        }
        mEditText.setText(text);
        mEditText.setSelection(mEditText.getText().length());
    }

    private void update() {
        if (mCurrentCellSelectedPosition != -1) {
            Flashcard flashcard = mListAdapter.getItem(mCurrentCellSelectedPosition);
            String text = String.valueOf(mEditText.getText());
            if (text != null) {
                boolean doUpdate = false;
                if (mCurrentCellSelectedType == CardAdapter.TYPE_SOURCE) {
                    if (!flashcard.getSource().equals(text)) {
                        flashcard.setSource(text);
                        doUpdate = true;
                    }
                } else if (mCurrentCellSelectedType == CardAdapter.TYPE_TARGET) {
                    if (!flashcard.getTarget().equals(text)) {
                        flashcard.setTarget(text);
                        doUpdate = true;
                    }
                }
                if (doUpdate) {
                    if (mFlashcardController.update(flashcard)) {
                        mUpdated = true;
                        mDeck.setTimestampModified(System.currentTimeMillis());
                        mDeckController.update(mDeck);
                        Log.i(TAG, "update successful!");
                    } else {
                        Log.e(TAG, "update failed!");
                    }
                }
            }
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.menu_spreadsheet_action_mode, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            update();
            mLayoutFooter.setVisibility(View.GONE);
            if (mInputMethodManager.isActive()) {
                mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            }
            mActionMode = null;
            mListAdapter.clearRowSelection();
            mListAdapter.clearCellSelection();
            mEditText.clearFocus();
            mCurrentCellSelectedPosition = -1;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ROW_SELECTED_POSITION, mCurrentRowSelectedPosition);
        outState.putInt(STATE_CELL_SELECTED_POSITION, mCurrentCellSelectedPosition);
        outState.putInt(STATE_CELL_SELECTED_TYPE, mCurrentCellSelectedType);
        outState.putBoolean(STATE_UPDATED, mUpdated);
    }

    public void setToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
    }

    static class FlashcardLoader extends AsyncTaskLoader<List<Flashcard>> {

        private long mDeckId;
        private boolean mUpdate;

        public FlashcardLoader(Context context, long deckId) {
            super(context);
            mDeckId = deckId;
        }

        @Override
        public List<Flashcard> loadInBackground() {
            FlashcardController flashcardController = MainApplication.getInstance().getFlashcardController();
            int count = flashcardController.count(mDeckId);
            if (count < Constants.DATABASE_MAX_ROWS) {
                flashcardController.insertEmptyRows(mDeckId, Constants.DATABASE_MAX_ROWS - count);
                mUpdate = true;
            }
            return flashcardController.getAllByDeckId(mDeckId);
        }

        public boolean isUpdated() {
            return mUpdate;
        }
    }

    @Override
    public void onTextInputButtonClicked(int requestCode, String text) {
        switch (requestCode) {
            case REQUEST_DIALOG_RENAME:
                if (!mDeck.getName().equals(text)) {
                    mDeck.setName(text);
                    if (mDeckController.update(mDeck)) {
                        mUpdated = true;
                        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
                        if (actionBar != null) {
                            actionBar.setTitle(text);
                        }
                    }
                }
                break;
        }
    }

    public boolean isUpdated() {
        return mUpdated;
    }
}
