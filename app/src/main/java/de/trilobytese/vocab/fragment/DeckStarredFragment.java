package de.trilobytese.vocab.fragment;

import android.os.Bundle;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.model.Category;
import de.trilobytese.vocab.model.Deck;

import java.util.List;

public class DeckStarredFragment extends DeckBaseFragment {

    public static DeckStarredFragment newInstance() {
        DeckStarredFragment fragment = new DeckStarredFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_navigation_starred);
    }

    @Override
    protected List<Deck> getListItems() {
        return mDeckController.getStarred();
    }

    @Override
    protected int getLayoutEmptyView() {
        return R.layout.layout_empty_deck_starred;
    }

    @Override
    protected void onItemCreate() {
        Deck deck = new Deck(getString(R.string.txt_unnamed_deck), Category.PRIMARY_CATEGORY_ID);
        deck.setStarred(true);
        mDeckController.add(deck);
        onItemCreated(deck);
    }

    @Override
    protected void onItemUpdated(Deck deck) {
        if (!deck.isStarred()) {
            mListAdapter.removeItem(deck);
        }
    }
}
