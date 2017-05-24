package de.trilobytese.vocab.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class DragItem extends Label {

    private boolean isTarget = false;

    private Rectangle bounds = new Rectangle();

    public DragItem(CharSequence text, LabelStyle style) {
        super(text, style);
    }

    @Override
    public void act(float delta) {
        updateBounds();
    }

    private void updateBounds() {
        bounds.set(getX(), getY(), getWidth() - 10, getHeight() - 10);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public void setTarget(boolean isTarget) {
        this.isTarget = isTarget;
    }
}
