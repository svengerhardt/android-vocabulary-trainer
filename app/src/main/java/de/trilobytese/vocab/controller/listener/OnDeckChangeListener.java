package de.trilobytese.vocab.controller.listener;

import de.trilobytese.vocab.model.Deck;

public interface OnDeckChangeListener {

    void onAdded(Deck deck);

    void onRemoved(Deck deck);

    void onUpdated(Deck deck);
}
