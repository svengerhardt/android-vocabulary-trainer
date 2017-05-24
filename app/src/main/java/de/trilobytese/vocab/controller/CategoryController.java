package de.trilobytese.vocab.controller;

import de.trilobytese.vocab.controller.listener.OnCategoryChangeListener;
import de.trilobytese.vocab.model.Category;

import java.util.List;

public interface CategoryController extends Controller {

    List<Category> findAll();

    Category getById(long id);

    boolean add(Category category);

    boolean delete(Category category);

    void addOnChangedListener(OnCategoryChangeListener listener);

    void removeOnChangedListener(OnCategoryChangeListener listener);

}
