package de.trilobytese.vocab.util;

import android.util.DisplayMetrics;
import de.trilobytese.vocab.MainApplication;
import de.trilobytese.vocab.R;

public class Utilities {

    public static boolean isTablet() {
        return MainApplication.getInstance().getResources().getBoolean(R.bool.isTablet);
    }

    public static boolean isLandscape() {
        return MainApplication.getInstance().getResources().getBoolean(R.bool.isLandscape);
    }

    public static DisplayMetrics getDisplayMetrics() {
        return MainApplication.getInstance().getResources().getDisplayMetrics();
    }

    public static float convertPixelsToDp(float pixels) {
        return pixels / getDisplayMetrics().density;
    }

    public static float convertDpToPixels(float dp) {
        return dp * getDisplayMetrics().density;
    }


}
