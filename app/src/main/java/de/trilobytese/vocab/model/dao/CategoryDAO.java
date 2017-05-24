package de.trilobytese.vocab.model.dao;

import de.trilobytese.vocab.model.Category;

public interface CategoryDAO extends DAO<Category> {

    Category get(long categoryId);

}
