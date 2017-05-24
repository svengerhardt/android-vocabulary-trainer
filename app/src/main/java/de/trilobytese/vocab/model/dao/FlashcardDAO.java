package de.trilobytese.vocab.model.dao;

import de.trilobytese.vocab.model.Flashcard;

import java.util.List;

public interface FlashcardDAO extends DAO<Flashcard> {

    List<Flashcard> getByDeckId(long deckId);

    List<Flashcard> getByDeckIdNotEmpty(long deckId);

    boolean update(Flashcard toUpdate);

    boolean deleteAllByDeckId(long deckId);

    void insertEmptyRows(long deckId, int count);

    void insert(List<Flashcard> items);

    void insert(long deckId, String[][] items);

    int count(long deckId);
}
