package de.trilobytese.vocab.controller;

import de.trilobytese.vocab.controller.listener.OnDeckChangeListener;
import de.trilobytese.vocab.model.Deck;

import java.util.List;

public interface DeckController extends Controller {

    List<Deck> findAll();

    List<Deck> getAllByCategoryId(long categoryId);

    List<Deck> getStarred();

    Deck getById(long id);

    boolean add(Deck deck);

    boolean delete(Deck deck);

    boolean update(Deck deck);

    void addOnChangedListener(OnDeckChangeListener listener);

    void removeOnChangedListener(OnDeckChangeListener listener);

}
