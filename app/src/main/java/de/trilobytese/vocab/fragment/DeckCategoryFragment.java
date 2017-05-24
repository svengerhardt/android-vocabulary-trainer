package de.trilobytese.vocab.fragment;

import android.os.Bundle;
import android.view.View;
import de.trilobytese.vocab.MainApplication;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.controller.CategoryController;
import de.trilobytese.vocab.model.Category;
import de.trilobytese.vocab.model.Deck;

import java.util.List;

public class DeckCategoryFragment extends DeckBaseFragment {

    private static final String ARG_CATEGORY_ID = "category_id";

    private CategoryController mCategoryController;
    private long mCategoryId;

    public static DeckCategoryFragment newInstance(long categoryId) {
        DeckCategoryFragment fragment = new DeckCategoryFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoryController = MainApplication.getInstance().getCategoryController();
        if (getArguments() != null) {
            mCategoryId = getArguments().getLong(ARG_CATEGORY_ID);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected String getActionBarTitle() {
        if (mCategoryId == Category.PRIMARY_CATEGORY_ID) {
            return getString(R.string.menu_navigation_primary);
        } else {
            return mCategoryController.getById(mCategoryId).getName();
        }
    }

    @Override
    protected List<Deck> getListItems() {
        return mDeckController.getAllByCategoryId(mCategoryId);
    }

    @Override
    protected int getLayoutEmptyView() {
        return R.layout.layout_empty_deck_category;
    }

    @Override
    protected void onItemCreate() {
        Deck deck = new Deck(getString(R.string.txt_unnamed_deck), mCategoryId);
        deck.setStarred(false);
        mDeckController.add(deck);
        onItemCreated(deck);
    }

    @Override
    protected void onItemUpdated(Deck deck) {
        mListAdapter.update(deck);
    }
}
