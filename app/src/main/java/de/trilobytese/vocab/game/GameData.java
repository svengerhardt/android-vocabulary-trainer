package de.trilobytese.vocab.game;

import de.trilobytese.vocab.MainPreferences;
import de.trilobytese.vocab.model.Flashcard;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameData {

    private int maxFlashcards;
    private GameApp app;
    private List<Flashcard> flashCards;
    private int count;

    public GameData(GameApp app) {
        this.app = app;
        this.maxFlashcards = MainPreferences.getMaxFlashcards();
    }

    public void init() {
        flashCards = app.application.getFlashcardController().getAllByDeckIdNotEmpty(app.deckId);
        Collections.shuffle(flashCards, new Random(System.nanoTime()));
    }

    public List<Flashcard> getNextItems() {
        int size = flashCards.size();
        if (size >= maxFlashcards) {
            count = maxFlashcards;
        } else {
            count = size;
        }

        return flashCards.subList(0, count);
    }

    public int getNextItemsCount() {
        return count;
    }

    // returns the rest size not the complete size!
    public int getSize() {
        return flashCards.size();
    }
}
