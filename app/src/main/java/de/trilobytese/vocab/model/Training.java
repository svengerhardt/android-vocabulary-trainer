package de.trilobytese.vocab.model;

import java.util.List;

public class Training {

    private Deck deck;

    private List<Flashcard> data;

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public List<Flashcard> getData() {
        return data;
    }

    public void setData(List<Flashcard> data) {
        this.data = data;
    }
}
