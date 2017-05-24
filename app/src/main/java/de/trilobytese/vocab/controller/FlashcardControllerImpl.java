package de.trilobytese.vocab.controller;

import de.trilobytese.vocab.controller.listener.OnFlashcardChangeListener;
import de.trilobytese.vocab.model.Flashcard;
import de.trilobytese.vocab.model.dao.FlashcardDAO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FlashcardControllerImpl implements FlashcardController {

    private FlashcardDAO dao;

    private CopyOnWriteArrayList<OnFlashcardChangeListener> listeners;

    public FlashcardControllerImpl(FlashcardDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Flashcard> getAllByDeckId(long deckId) {
        return dao.getByDeckId(deckId);
    }

    @Override
    public List<Flashcard> getAllByDeckIdNotEmpty(long deckId) {
        return dao.getByDeckIdNotEmpty(deckId);
    }

    @Override
    public boolean add(Flashcard flashCard) {
        boolean added = dao.persist(flashCard);
        if (added) {
            if (listeners != null && listeners.size() > 0) {
                for (OnFlashcardChangeListener listener : listeners) {
                    listener.onAdded(flashCard);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Flashcard flashCard) {
        boolean updated = dao.update(flashCard);
        if (updated) {
            if (listeners != null && listeners.size() > 0) {
                for (OnFlashcardChangeListener listener : listeners) {
                    listener.onUpdated(flashCard);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Flashcard flashCard) {
        boolean removed = dao.delete(flashCard);
        if (removed) {
            if (listeners != null && listeners.size() > 0) {
                for (OnFlashcardChangeListener listener : listeners) {
                    listener.onRemoved(flashCard);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteAllByDeckId(long deckId) {
        return dao.deleteAllByDeckId(deckId);
    }

    public void insertEmptyRows(long deckId, int count) {
        dao.insertEmptyRows(deckId, count);
    }

    public void insert(List<Flashcard> items) {
        dao.insert(items);
    }

    public void insert(long deckId, String[][] items) {
        dao.insert(deckId, items);
    }

    @Override
    public int count(long deck_id) {
        return dao.count(deck_id);
    }

    @Override
    public void addOnChangedListener(OnFlashcardChangeListener listener) {
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
        }
        listeners.add(listener);
    }

    @Override
    public void removeOnChangedListener(OnFlashcardChangeListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
    }
}
