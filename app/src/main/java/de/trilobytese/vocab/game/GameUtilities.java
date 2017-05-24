package de.trilobytese.vocab.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class GameUtilities {

    public static Color colorFromHex(long hex) {
        float a = (hex & 0xFF000000L) >> 24;
        float r = (hex & 0xFF0000L) >> 16;
        float g = (hex & 0xFF00L) >> 8;
        float b = (hex & 0xFFL);
        return new Color(r/255f, g/255f, b/255f, a/255f);
    }

    public static float getItemWidth() {

        int dsw = 800;
        int dw = 180;

        float itemWidth = Gdx.graphics.getWidth() * dw / dsw;
        if (itemWidth % 2 != 0) {
            itemWidth += 1;
        }

        return itemWidth;
    }

    public static float getItemHeight() {

        int dsh = 480;
        int dh = 80;

        float itemHeight = Gdx.graphics.getHeight() * dh / dsh;
        if (itemHeight % 2 != 0) {
            itemHeight += 1;
        }

        return itemHeight;
    }

    public static int getItemFontSize() {

        int dsw = 800;
        int fs = 16;

        return Gdx.graphics.getWidth() * fs / dsw;
    }
}
