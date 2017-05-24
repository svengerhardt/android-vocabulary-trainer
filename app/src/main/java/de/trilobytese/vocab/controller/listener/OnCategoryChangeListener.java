package de.trilobytese.vocab.controller.listener;

import de.trilobytese.vocab.model.Category;

public interface OnCategoryChangeListener {

    void onAdded(Category category);

    void onRemoved(Category category);

    void onUpdated(Category category);
}
