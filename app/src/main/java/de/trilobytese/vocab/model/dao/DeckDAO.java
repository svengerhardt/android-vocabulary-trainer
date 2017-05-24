package de.trilobytese.vocab.model.dao;

import de.trilobytese.vocab.model.Deck;

import java.util.List;

public interface DeckDAO extends DAO<Deck> {

    Deck get(long deckId);

    List<Deck> getByCategoryId(long categoryId);

    List<Deck> getStarred();

    boolean update(Deck toUpdate);
}
