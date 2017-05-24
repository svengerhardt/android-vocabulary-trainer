package de.trilobytese.vocab.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import de.trilobytese.vocab.MainPreferences;
import de.trilobytese.vocab.dialog.listener.OnDialogConfirmListener;
import de.trilobytese.vocab.game.DragItem;
import de.trilobytese.vocab.game.GameApp;
import de.trilobytese.vocab.game.GameData;
import de.trilobytese.vocab.game.GameUtilities;
import de.trilobytese.vocab.model.Flashcard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game1Screen extends AbstractScreen {

    private final float minX = 0;
    private final float maxX = getScreenWidth();
    private final float minY = 0;
    private final float maxY = getScreenHeight();

    private GameApp app;
    private Sound soundMatch;
    private Sound soundNoMatch;

    private List<DragItem> dragItems;
    private int dropCounter = 0;
    private int progressCounter = 0;

    private GameData gameData;

    public Game1Screen(GameApp app) {
        super(app);
        this.app = app;
        this.gameData = new GameData(app);

        soundMatch = app.assetManager.get("audio/click.m4a");
        soundNoMatch = app.assetManager.get("audio/wrong.m4a");

        gameData.init();
        app.requestHandler.progressMax(gameData.getSize());
        createItems();
    }

    private void createItems() {
        dragItems = new ArrayList<>();
        List<Flashcard> items = gameData.getNextItems();
        Iterator<Flashcard> it = items.iterator();
        while(it.hasNext()) {
            Flashcard item = it.next();
            DragItem source = createDragItem(item.getSource());
            source.setTarget(false);
            source.setUserObject(item);
            dragItems.add(source);
            stage.addActor(source);
            DragItem target = createDragItem(item.getTarget());
            target.setTarget(true);
            target.setUserObject(item);
            dragItems.add(target);
            stage.addActor(target);
            it.remove();
        }
    }

    private DragItem createDragItem(String text) {

        Label.LabelStyle style = new Label.LabelStyle();
        style.background = getSkin().getDrawable("label_color_3");
        style.fontColor = color1;
        style.font = getFont();

        float itemWidth = GameUtilities.getItemWidth();
        float itemHeight = GameUtilities.getItemHeight();

        final DragItem dragItem = new DragItem(text, style);

        float x = minX + (int)(Math.random() * (maxX - minX + 1));
        float y = minY + (int)(Math.random() * (maxY - minY + 1));

        if (x + itemWidth > getScreenWidth()) {
            x = x - itemWidth;
        }

        if (y + itemHeight > getScreenHeight()) {
            y = y - itemHeight;
        }

        dragItem.setX(x);
        dragItem.setY(y);
        dragItem.setOrigin(x, y);
        dragItem.setSize(itemWidth, itemHeight);
        dragItem.setWrap(true);
        dragItem.setEllipse(true);
        dragItem.setAlignment(Align.center);

        dragItem.addListener(new DragListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                dragItem.getStyle().background = getSkin().getDrawable("label_color_4");
                dragItem.toFront();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                dragItem.getStyle().background = getSkin().getDrawable("label_color_3");
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {

                float dx = x - dragItem.getWidth() * 0.5f;
                float dy = y - dragItem.getHeight() * 0.5f;
                float posX = dragItem.getX() + dx;
                float posY = dragItem.getY() + dy;

                if (posX <= 0) {
                    posX = 0;
                } else if (posX >= getScreenWidth() - dragItem.getWidth()) {
                    posX = getScreenWidth() - dragItem.getWidth();
                }

                if (posY <= 0) {
                    posY = 0;
                } else if (posY >= getScreenHeight() - dragItem.getHeight()) {
                    posY = getScreenHeight() - dragItem.getHeight();
                }

                dragItem.setPosition(posX, posY);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                boolean f = false;
                for (DragItem item : dragItems) {
                    if (dragItem.getBounds().overlaps(item.getBounds())) {
                        if (dragItem.getUserObject().equals(item.getUserObject())) {
                            if ((dragItem.isTarget() && !item.isTarget()) || !dragItem.isTarget() && item.isTarget()) {
                                f = false;
                                handleMatch(dragItem, item);
                                break;
                            }
                        } else {
                            f = true;
                        }
                    }
                }

                if (f) {
                    handleNoMatch(dragItem);
                }

                dragItem.setOrigin(dragItem.getX(), dragItem.getY());
            }
        });

        return dragItem;
    }

    private void handleMatch(DragItem dragItemA, DragItem dragItemB) {

        dragItems.remove(dragItemA);
        dragItems.remove(dragItemB);
        dragItemA.remove();
        dragItemB.remove();

        dropCounter++;
        if (dropCounter == gameData.getNextItemsCount()) {
            dropCounter = 0;
            showConfirmDialog();
        } else {
            if (MainPreferences.isSoundOn()) {
                soundMatch.play();
            }
        }

        progressCounter++;
        app.requestHandler.progress(progressCounter);
    }

    private void handleNoMatch(DragItem dragItem) {
        dragItem.setPosition(dragItem.getOriginX(), dragItem.getOriginY());
        if (MainPreferences.isSoundOn()) {
            soundNoMatch.play();
        }
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
            }

            @Override
            public void onNegativeButtonClicked(int requestCode) {
                Gdx.app.exit();
            }
        });
    }
}
