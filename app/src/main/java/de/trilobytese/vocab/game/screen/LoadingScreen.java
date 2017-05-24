package de.trilobytese.vocab.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.game.GameApp;
import de.trilobytese.vocab.game.GameUtilities;

public class LoadingScreen implements Screen {

    private GameApp app;

    public LoadingScreen(GameApp app) {
        this.app = app;
    }

    @Override
    public void show() {
        app.assetManager.load("audio/click.m4a", Sound.class);
        app.assetManager.load("audio/wrong.m4a", Sound.class);
    }

    @Override
    public void render(float v) {

        Color color = GameUtilities.colorFromHex(app.application.getResources().getColor(R.color.color_1));
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (app.assetManager.update()) {
            switch (app.gameType) {
                case GameApp.GAME_TYPE_1:
                    app.setScreen(new Game1Screen(app));
                    break;
                case GameApp.GAME_TYPE_2:
                    app.setScreen(new Game2Screen(app));
                    break;
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
