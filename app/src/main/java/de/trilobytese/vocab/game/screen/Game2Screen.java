package de.trilobytese.vocab.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.trilobytese.vocab.MainPreferences;
import de.trilobytese.vocab.dialog.listener.OnDialogConfirmListener;
import de.trilobytese.vocab.game.DragItem;
import de.trilobytese.vocab.game.GameApp;
import de.trilobytese.vocab.game.GameData;
import de.trilobytese.vocab.model.Flashcard;

import java.util.*;

public class Game2Screen extends AbstractScreen {

    private static final int NUM_ROWS = 3;
    private static final int NUM_COLS = 5;

    private GameData gameData;
    private Table table;
    private List<DragItem> theItems;
    private List<DragItem> selectedItems;
    private int levelCounter = 0;
    private int progressCounter = 0;

    private Sound soundClick;
    private Sound soundNoMatch;

    public Game2Screen(GameApp app) {
        super(app);

        soundClick = app.assetManager.get("audio/click.m4a");
        soundNoMatch = app.assetManager.get("audio/wrong.m4a");

        selectedItems = new ArrayList<>(2);

        gameData = new GameData(app);
        gameData.init();
        app.requestHandler.progressMax(gameData.getSize());

        createItems();
        createGrid();
    }

    private void createItems() {

        Label.LabelStyle style = new Label.LabelStyle();
        style.background = getSkin().getDrawable("label_color_3");
        style.fontColor = color1;
        style.font = getFont();

        theItems = new ArrayList<>();
        Iterator<Flashcard> it = gameData.getNextItems().iterator();
        while(it.hasNext()) {
            Flashcard item = it.next();
            DragItem source = createItem(item.getSource());
            source.setTarget(false);
            source.setUserObject(item);
            theItems.add(source);
            DragItem target = createItem(item.getTarget());
            target.setTarget(true);
            target.setUserObject(item);
            theItems.add(target);
            it.remove();
        }

        Collections.shuffle(theItems, new Random(System.nanoTime()));
    }

    private void createGrid() {

        table = new Table();
        table.defaults().pad(2f);
        table.setFillParent(true);
        table.setDebug(false);

        List<Integer> rci = createRandomCellIndices();

        int index = 0;
        int c = 0;

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                if (rci.contains(c)) {
                    table.add(theItems.get(index)).width(getCellWidth()).height(getCellHeight());
                    index++;
                } else {
                    table.add(createEmptyItem()).width(getCellWidth()).height(getCellHeight());
                }
                c++;
            }
            if (i < NUM_ROWS - 1) {
                table.row();
            }
        }
        stage.addActor(table);
    }

    private DragItem createItem(String text) {

        Label.LabelStyle style = new Label.LabelStyle();
        style.background = getSkin().getDrawable("label_color_3");
        style.fontColor = color1;
        style.font = getFont();

        final DragItem item = new DragItem(text, style);
        item.setAlignment(Align.center);
        item.setWidth(getCellWidth());
        item.setHeight(getCellHeight());
        item.setWrap(true);
        item.setEllipse(true);
        item.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    item.getStyle().background = getSkin().getDrawable("label_color_3");
                } else {
                    selectedItems.add(item);
                    item.getStyle().background = getSkin().getDrawable("label_color_4");
                }

                if (selectedItems.size() == 2) {
                    DragItem itemA = selectedItems.get(0);
                    DragItem itemB = selectedItems.get(1);
                    if (itemA.getUserObject().equals(itemB.getUserObject())) {
                        handleMatch(itemA, itemB);
                        selectedItems.clear();
                    } else {
                        handleNoMatch(itemA, itemB);
                        selectedItems.clear();
                    }
                }

                if (MainPreferences.isSoundOn()) {
                    soundClick.play();
                }
            }
        });
        return item;
    }

    private Label createEmptyItem() {

        Label.LabelStyle style = new Label.LabelStyle();
        style.background = getSkin().getDrawable("label_color_1");
        style.fontColor = color1;
        style.font = getFont();

        Label label = new Label("", style);
        label.setWidth(getCellWidth());
        label.setHeight(getCellHeight());

        return label;
    }

    private void handleMatch(DragItem itemA, DragItem itemB) {
        theItems.remove(itemA);
        theItems.remove(itemB);
        itemA.remove();
        itemB.remove();

        levelCounter++;
        if (levelCounter == gameData.getNextItemsCount()) {
            levelCounter = 0;
            table.remove();
            showConfirmDialog();
        }

        progressCounter++;
        app.requestHandler.progress(progressCounter);
    }

    private void handleNoMatch(final DragItem itemA, final DragItem itemB) {
        if (MainPreferences.isSoundOn()) {
            soundNoMatch.play();
        }
        itemA.getStyle().background = getSkin().getDrawable("label_color_2");
        itemB.getStyle().background = getSkin().getDrawable("label_color_2");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (itemA != null) {
                    itemA.getStyle().background = getSkin().getDrawable("label_color_3");
                }
                if (itemB != null) {
                    itemB.getStyle().background = getSkin().getDrawable("label_color_3");
                }
            }
        }, 1000);
    }

    private void showConfirmDialog() {

        app.requestHandler.confirm(gameData.getSize(), new OnDialogConfirmListener() {
            @Override
            public void onPositiveButtonClicked(int requestCode, long id) {
                if (gameData.getSize() == 0) {
                    progressCounter = 0;
                    app.requestHandler.progress(progressCounter);
                    gameData.init();
                }

                createItems();
                createGrid();
            }

            @Override
            public void onNegativeButtonClicked(int requestCode) {
                Gdx.app.exit();
            }
        });
    }

    private int getCellWidth() {
        int width = getScreenWidth() / NUM_COLS;
        if (width % 2 != 0) {
            width -= 1;
        }
        return width;
    }

    private int getCellHeight() {
        int height = getScreenHeight() / NUM_ROWS;
        if (height % 2 != 0) {
            height -= 1;
        }
        return height;
    }

    private List<Integer> createRandomCellIndices() {
        int size = NUM_COLS * NUM_ROWS;
        List<Integer> arr = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            arr.add(i);
        }
        Collections.shuffle(arr, new Random(System.nanoTime()));
        return arr.subList(0, gameData.getNextItemsCount() * 2);
    }
}
