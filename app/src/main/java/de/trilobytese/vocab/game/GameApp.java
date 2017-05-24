package de.trilobytese.vocab.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import de.trilobytese.vocab.MainApplication;
import de.trilobytese.vocab.dialog.listener.OnDialogConfirmListener;
import de.trilobytese.vocab.game.screen.LoadingScreen;

public class GameApp extends Game {

    public static final int GAME_TYPE_1 = 1;
    public static final int GAME_TYPE_2 = 2;

    public RequestHandler requestHandler;
    public MainApplication application;
    public long deckId;
    public int gameType;

    public interface RequestHandler {
        void confirm(int size, OnDialogConfirmListener listener);
        void progressMax(int value);
        void progress(int value);
    }

    public AssetManager assetManager = new AssetManager();

    public GameApp(RequestHandler requestHandler, MainApplication application, long deckId, int gameType) {
        this.requestHandler = requestHandler;
        this.application = application;
        this.deckId = deckId;
        this.gameType = gameType;
    }

    @Override
    public void create() {
        setScreen(new LoadingScreen(this));
    }
}
