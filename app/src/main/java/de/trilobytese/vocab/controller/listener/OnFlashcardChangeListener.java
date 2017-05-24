package de.trilobytese.vocab.controller.listener;

import de.trilobytese.vocab.model.Flashcard;

public interface OnFlashcardChangeListener {

    void onAdded(Flashcard flashCard);

    void onRemoved(Flashcard flashCard);

    void onUpdated(Flashcard flashCard);
}
