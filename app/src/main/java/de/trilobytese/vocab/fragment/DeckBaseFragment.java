package de.trilobytese.vocab.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.PopupMenu;
import au.com.bytecode.opencsv.CSVWriter;
import de.trilobytese.vocab.MainApplication;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.activity.*;
import de.trilobytese.vocab.adapter.DeckAdapter;
import de.trilobytese.vocab.controller.DeckController;
import de.trilobytese.vocab.controller.FlashcardController;
import de.trilobytese.vocab.dialog.DialogGameChooserFragment;
import de.trilobytese.vocab.dialog.DialogRemoveFragment;
import de.trilobytese.vocab.dialog.listener.OnDialogGameChooseListener;
import de.trilobytese.vocab.dialog.listener.OnDialogPositiveListener;
import de.trilobytese.vocab.game.GameApp;
import de.trilobytese.vocab.model.Deck;
import de.trilobytese.vocab.model.Flashcard;
import de.trilobytese.vocab.util.TimeUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class DeckBaseFragment extends Fragment implements DeckAdapter.OnItemClickListener,
        DeckAdapter.OnMenuClickListener, OnDialogPositiveListener, OnDialogGameChooseListener {

    private static final String TAG = DeckBaseFragment.class.getSimpleName();

    private static final int REQUEST_CODE_EDIT = 1000;

    private static final int REQUEST_DIALOG_REMOVE = 0;
    private static final int REQUEST_DIALOG_CHOOSE_GAME = 1;

    protected ViewStub mViewStub;
    protected RecyclerView mListView;
    protected GridLayoutManager mLayoutManager;
    protected DeckAdapter mListAdapter;
    protected DeckController mDeckController;
    protected FlashcardController mFlashcardController;

    private Handler mHandler = new Handler();

    public DeckBaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDeckController = MainApplication.getInstance().getDeckController();
        mFlashcardController = MainApplication.getInstance().getFlashcardController();
    }

    @Override
    public void onDestroy() {
        if (mListAdapter != null) {
            mListAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_deck_list, container, false);
        mViewStub = (ViewStub)rootView.findViewById(R.id.stub);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (RecyclerView)view.findViewById(R.id.deck_list);
        mListView.setHasFixedSize(true);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.deck_span_count));
        mListView.setLayoutManager(mLayoutManager);
        mListAdapter = new DeckAdapter(getListItems(), this, this);
        mListAdapter.registerAdapterDataObserver(mAdapterDataObserver);
        mListView.setAdapter(mListAdapter);
        setActionBarTitle(getActionBarTitle());
        checkIfEmptyList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        long id = extras.getLong("deck_id");
                        Log.d(TAG, "updating deck with id " + id);
                        final Deck deck = mDeckController.getById(id);
                        if (mListAdapter.exists(deck)) {
                            mListAdapter.update(deck);
                        } else {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mListView.scrollToPosition(0);
                                    mListAdapter.addItem(deck);
                                }
                            }, 300);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_deck, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                onItemCreate();
                mListView.scrollToPosition(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, Deck deck, int position) {
        DialogGameChooserFragment dialog = DialogGameChooserFragment.newInstance(REQUEST_DIALOG_CHOOSE_GAME, deck.getId());
        dialog.setTargetFragment(DeckBaseFragment.this, 0);
        dialog.show(getFragmentManager(), "dialog_game_chooser");
    }

    @Override
    public void onMenuClick(final View view, final Deck deck, final int position) {
        view.post(new Runnable() {
            @Override
            public void run() {
                showPopupMenu(view, deck, position);
            }
        });
    }

    private void showPopupMenu(View view, final Deck deck, final int position) {

        PopupMenu mPopupMenu = new PopupMenu(getActivity(), view);

        try {
            Field[] fields = mPopupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(mPopupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }

        } catch (Exception e) {
            //ignore
        }

        mPopupMenu.inflate(R.menu.menu_deck_item);
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_edit:
                        Intent intent = new Intent(getActivity(), SpreadsheetActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("deck_id", deck.getId());
                        startActivityForResult(intent, REQUEST_CODE_EDIT);
                        break;
                    case R.id.menu_star:
                        if (deck.isStarred()) {
                            deck.setStarred(false);
                            mDeckController.update(deck);
                            onItemUpdated(deck);
                        } else {
                            deck.setStarred(true);
                            mDeckController.update(deck);
                            onItemUpdated(deck);
                        }
                        break;
                    case R.id.menu_share:
                        share(deck.getId());
                        break;
                    case R.id.menu_delete:
                        DialogRemoveFragment dialog = DialogRemoveFragment.newInstance(
                                REQUEST_DIALOG_REMOVE, getString(R.string.dialog_remove_deck_title),
                                getString(R.string.dialog_remove_deck_content, deck.getName()),
                                deck.getId());
                        dialog.setTargetFragment(DeckBaseFragment.this, 0);
                        dialog.show(getFragmentManager(), "dialog_remove");
                        break;
                }
                return true;
            }
        });

        MenuItem starMenuItem = mPopupMenu.getMenu().findItem(R.id.menu_star);
        if (starMenuItem != null) {
            if (deck.isStarred()) {
                starMenuItem.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_toggle_star));
                starMenuItem.setTitle(getString(R.string.txt_unstar));
            } else {
                starMenuItem.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_toggle_star_outline));
                starMenuItem.setTitle(getString(R.string.txt_star));
            }
        }

        mPopupMenu.show();
    }

    protected void setActionBarTitle(String title) {
        ActionBarActivity activity = ((ActionBarActivity) getActivity());
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void onPositiveButtonClicked(int requestCode, long id) {
        switch (requestCode) {
            case REQUEST_DIALOG_REMOVE:
                Deck deckToRemove = mDeckController.getById(id);
                if (mDeckController.delete(deckToRemove)) {
                    mListAdapter.removeItem(deckToRemove);
                }
                break;

        }
    }

    @Override
    public void onGameClicked(int requestCode, long deckId, int type) {
        switch (requestCode) {
            case REQUEST_DIALOG_CHOOSE_GAME:
                switch (type) {
                    case DialogGameChooserFragment.GAME_TYPE_1:
                        Intent intentGame1 = new Intent(getActivity(), Training1Activity.class);
                        intentGame1.putExtra("deck_id", deckId);
                        startActivity(intentGame1);
                        break;
                    /*case DialogGameChooserFragment.GAME_TYPE_2:
                        Intent intentGame2 = new Intent(getActivity(), GameActivity.class);
                        intentGame2.putExtra("deck_id", deckId);
                        startActivity(intentGame2);
                        break;*/
                    case DialogGameChooserFragment.GAME_TYPE_3:
                        Intent intentGame3 = new Intent(getActivity(), GameActivity.class);
                        intentGame3.putExtra("deck_id", deckId);
                        intentGame3.putExtra("game_type", GameApp.GAME_TYPE_1);
                        startActivity(intentGame3);
                        break;
                    case DialogGameChooserFragment.GAME_TYPE_4:
                        Intent intentGame4 = new Intent(getActivity(), GameActivity.class);
                        intentGame4.putExtra("deck_id", deckId);
                        intentGame4.putExtra("game_type", GameApp.GAME_TYPE_2);
                        startActivity(intentGame4);
                        break;
                }
                break;
        }
    }

    public void onItemCreated(Deck deck) {
        Intent intent = new Intent(getActivity(), SpreadsheetActivity.class);
        intent.putExtra("deck_id", deck.getId());
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    protected void checkIfEmptyList() {
        if (mListAdapter.getItemCount() == 0) {
            mListView.setVisibility(View.GONE);
            mViewStub.setLayoutResource(getLayoutEmptyView());
            mViewStub.setVisibility(View.VISIBLE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            mViewStub.setVisibility(View.GONE);
        }
    }

    private RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmptyList();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmptyList();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmptyList();
        }
    };

    private void share(long deckId) {

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/csv");

        String deckName;
        Deck deck = mDeckController.getById(deckId);
        if (deck != null && !deck.getName().isEmpty()) {
            deckName = deck.getName();
            deckName = deckName.replaceAll("[_[^\\w\\däüöÄÜÖß\\+\\- ]]", " ");
        } else {
            deckName = "vocab_" + TimeUtils.getDate(System.currentTimeMillis());
        }

        File csvFile = new File(getActivity().getExternalCacheDir(), deckName + ".csv");

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csvFile), ',');
            List<String[]> data = new ArrayList<>();
            List<Flashcard> flashCards = mFlashcardController.getAllByDeckIdNotEmpty(deckId);
            for (Flashcard flashCard : flashCards) {
                String[] arr = new String[2];
                arr[0] = flashCard.getSource();
                arr[1] = flashCard.getTarget();
                data.add(arr);
            }

            writer.writeAll(data);
            writer.close();

            Uri uri = Uri.parse("file://" + csvFile.getAbsolutePath());
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, getString(R.string.dialog_share_deck_title)));

        } catch (IOException e) {
            Log.e(TAG, "Error sharing flashcards!", e);
        }
    }

    protected abstract String getActionBarTitle();
    protected abstract List<Deck> getListItems();
    protected abstract int getLayoutEmptyView();
    protected abstract void onItemCreate();
    protected abstract void onItemUpdated(Deck deck);

}
