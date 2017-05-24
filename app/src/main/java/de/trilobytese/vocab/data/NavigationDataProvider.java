package de.trilobytese.vocab.data;

import de.trilobytese.vocab.MainApplication;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.controller.CategoryController;
import de.trilobytese.vocab.model.Category;

import java.util.LinkedList;
import java.util.List;

public class NavigationDataProvider {

    public static final int ITEM_VIEW_TYPE_NAVIGATION = 0;
    public static final int ITEM_VIEW_TYPE_NAVIGATION_DIVIDER = 1;

    public static final int ITEM_TYPE_DIVIDER = 0;
    public static final int ITEM_TYPE_CATEGORY = 1;
    public static final int ITEM_TYPE_STARRED = 2;
    public static final int ITEM_TYPE_SETTINGS = 4;
    public static final int ITEM_TYPE_HELP = 5;

    private List<Data> mData;

    public NavigationDataProvider() {

        mData = new LinkedList<>();

        CategoryController categoryController = MainApplication.getInstance().getCategoryController();

        Category cat1 = categoryController.getById(Category.PRIMARY_CATEGORY_ID);
        mData.add(new Data(ITEM_TYPE_CATEGORY,
            R.drawable.ic_action_flashcards,
            MainApplication.getInstance().getString(R.string.menu_navigation_primary),
            cat1.getId()));

        mData.add(new Data(ITEM_TYPE_STARRED,
            R.drawable.ic_action_toggle_star,
            MainApplication.getInstance().getString(R.string.menu_navigation_starred)));

        mData.add(new Data(ITEM_TYPE_DIVIDER));

        mData.add(new Data(ITEM_TYPE_SETTINGS,
            R.drawable.ic_action_settings,
            MainApplication.getInstance().getString(R.string.menu_navigation_settings)));

        mData.add(new Data(ITEM_TYPE_HELP,
            R.drawable.ic_action_help,
            MainApplication.getInstance().getString(R.string.menu_navigation_help)));
    }

    public int getCount() {
        return mData.size();
    }

    public Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return mData.get(index);
    }

    public static final class Data {

        public final int mType;
        public int mResId;
        public String mName;
        public long mId;

        public Data(int mType) {
            this.mType = mType;
        }

        public Data(int type, int resId, String name) {
            mType = type;
            mResId = resId;
            mName = name;
        }

        public Data(int type, int resId, String name, long id) {
            mType = type;
            mResId = resId;
            mName = name;
            mId = id;
        }

        public int getType() {
            return mType;
        }

        public int getResId() {
            return mResId;
        }

        public String getName() {
            return mName;
        }

        public long getId() {
            return mId;
        }

        public int getViewType() {
            if (mType == ITEM_TYPE_DIVIDER) {
                return ITEM_VIEW_TYPE_NAVIGATION_DIVIDER;
            } else {
                return ITEM_VIEW_TYPE_NAVIGATION;
            }
        }
    }
}
