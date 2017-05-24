package de.trilobytese.vocab;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import de.trilobytese.vocab.controller.*;
import de.trilobytese.vocab.model.dao.*;

public class MainApplication extends Application {

    private static MainApplication mInstance;

    private CategoryControllerImpl mCategoryController;
    private DeckControllerImpl mDeckController;
    private FlashcardControllerImpl mFlashcardController;

    public static String APP_VERSION;

    public MainApplication() {
        mInstance = this;
    }

    public static MainApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DBAdapter.initializeInstance(this);

        mCategoryController = new CategoryControllerImpl(new CategoryDAOImpl());
        mDeckController = new DeckControllerImpl(new DeckDAOImpl());
        mFlashcardController = new FlashcardControllerImpl(new FlashcardDAOImpl());

        // app version
        setAppVersion();
    }

    protected void setAppVersion() {
        if (APP_VERSION == null) {
            PackageManager pm = getPackageManager();
            try {
                PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
                APP_VERSION = pi.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                // ignore
            }
        }
    }

    public CategoryController getCategoryController() {
        return mCategoryController;
    }

    public DeckController getDeckController() {
        return mDeckController;
    }

    public FlashcardController getFlashcardController() {
        return mFlashcardController;
    }

}
