package de.trilobytese.vocab.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.game.GameApp;
import de.trilobytese.vocab.game.GameUtilities;

public abstract class AbstractScreen implements Screen {

    protected GameApp app;
    protected Stage stage;

    private Skin skin;
    private BitmapFont font;

    private int screenWidth;
    private int screenHeight;

    protected Color color1;
    protected Color color2;
    protected Color color3;
    protected Color color4;

    public AbstractScreen(GameApp app) {
        this.app = app;
        stage = new Stage();
        stage.setViewport(new ScreenViewport(stage.getCamera()));

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        color1 = GameUtilities.colorFromHex(app.application.getResources().getColor(R.color.game_color_1));
        color2 = GameUtilities.colorFromHex(app.application.getResources().getColor(R.color.game_color_2));
        color3 = GameUtilities.colorFromHex(app.application.getResources().getColor(R.color.game_color_3));
        color4 = GameUtilities.colorFromHex(app.application.getResources().getColor(R.color.game_color_4));
    }

    @Override
    public void render(float v) {
        Color color = color1;
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        log("resize: " + width + "x" + height + ", graphics says: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        if (skin != null) skin.dispose();
        if (font != null) font.dispose();
        app.assetManager.dispose();
    }

    protected Skin getSkin() {
        if(skin == null) {
            skin = new Skin();
            skin.add("label", new NinePatch(new Texture(Gdx.files.internal("graphics/label.png")), 2, 2, 2, 2));
            skin.add("label_color_1", getSkin().newDrawable("label", color1), Drawable.class);
            skin.add("label_color_2", getSkin().newDrawable("label", color2), Drawable.class);
            skin.add("label_color_3", getSkin().newDrawable("label", color3), Drawable.class);
            skin.add("label_color_4", getSkin().newDrawable("label", color4), Drawable.class);
        }
        return skin;
    }

    protected BitmapFont getFont() {
        if (font == null) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto_black.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = Math.round(GameUtilities.getItemFontSize());
            font = generator.generateFont(parameter);
        }
        return font;
    }

    protected int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    protected String getName() {
        return ((Object) this).getClass().getSimpleName();
    }

    protected void log(String message) {
        Gdx.app.log(getName(), message);
    }
}
