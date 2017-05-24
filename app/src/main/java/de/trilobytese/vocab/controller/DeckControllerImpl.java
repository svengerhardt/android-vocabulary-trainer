package de.trilobytese.vocab.controller;

import de.trilobytese.vocab.controller.listener.OnDeckChangeListener;
import de.trilobytese.vocab.model.Deck;
import de.trilobytese.vocab.model.dao.DeckDAO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DeckControllerImpl implements DeckController {

    private DeckDAO dao;

    private CopyOnWriteArrayList<OnDeckChangeListener> listeners;

    public DeckControllerImpl(DeckDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Deck> findAll() {
        return dao.findAll();
    }

    @Override
    public List<Deck> getAllByCategoryId(long categoryId) {
        return dao.getByCategoryId(categoryId);
    }

    @Override
    public List<Deck> getStarred() {
        return dao.getStarred();
    }

    @Override
    public Deck getById(long id) {
        return dao.get(id);
    }

    @Override
    public boolean add(Deck deck) {
        boolean added = dao.persist(deck);
        if (added) {
            if (listeners != null && listeners.size() > 0) {
                for (OnDeckChangeListener listener : listeners) {
                    listener.onAdded(deck);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Deck deck) {
        boolean removed = dao.delete(deck);
        if (removed) {
            if (listeners != null && listeners.size() > 0) {
                for (OnDeckChangeListener listener : listeners) {
                    listener.onRemoved(deck);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean update(Deck deck) {
        boolean updated = dao.update(deck);
        if (updated) {
            if (listeners != null && listeners.size() > 0) {
                for (OnDeckChangeListener listener : listeners) {
                    listener.onUpdated(deck);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void addOnChangedListener(OnDeckChangeListener listener) {
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
        }
        listeners.add(listener);
    }

    @Override
    public void removeOnChangedListener(OnDeckChangeListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
    }
}
