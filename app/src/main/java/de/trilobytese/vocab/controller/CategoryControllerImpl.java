package de.trilobytese.vocab.controller;

import de.trilobytese.vocab.controller.listener.OnCategoryChangeListener;
import de.trilobytese.vocab.model.Category;
import de.trilobytese.vocab.model.dao.CategoryDAO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CategoryControllerImpl implements CategoryController {

    private CategoryDAO dao;

    private CopyOnWriteArrayList<OnCategoryChangeListener> listeners;

    public CategoryControllerImpl(CategoryDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Category> findAll() {
        return dao.findAll();
    }

    @Override
    public Category getById(long id) {
        return dao.get(id);
    }

    @Override
    public boolean add(Category category) {
        boolean added = dao.persist(category);
        if (added) {
            if (listeners != null && listeners.size() > 0) {
                for (OnCategoryChangeListener listener : listeners) {
                    listener.onAdded(category);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Category category) {
        boolean removed = dao.delete(category);
        if (removed) {
            if (listeners != null && listeners.size() > 0) {
                for (OnCategoryChangeListener listener : listeners) {
                    listener.onRemoved(category);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void addOnChangedListener(OnCategoryChangeListener listener) {
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
        }
        listeners.add(listener);
    }

    @Override
    public void removeOnChangedListener(OnCategoryChangeListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
    }
}
