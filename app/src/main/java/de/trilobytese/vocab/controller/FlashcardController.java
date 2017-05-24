package de.trilobytese.vocab.controller;

import de.trilobytese.vocab.controller.listener.OnFlashcardChangeListener;
import de.trilobytese.vocab.model.Flashcard;

import java.util.List;

public interface FlashcardController extends Controller {

    List<Flashcard> getAllByDeckId(long deckId);

    List<Flashcard> getAllByDeckIdNotEmpty(long deckId);

    boolean add(Flashcard flashCard);

    boolean update(Flashcard flashCard);

    boolean delete(Flashcard flashCard);

    boolean deleteAllByDeckId(long deckId);

    void addOnChangedListener(OnFlashcardChangeListener listener);

    void removeOnChangedListener(OnFlashcardChangeListener listener);

    void insertEmptyRows(long deckId, int count);

    void insert(List<Flashcard> items);

    void insert(long deckId, String[][] items);

    int count(long deck_id);

}
