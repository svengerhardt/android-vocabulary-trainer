package de.trilobytese.vocab.model.dao;

import java.util.List;

public interface DAO<T> {

	List<T> findAll();
	
	boolean persist(T toPersist);
	
    boolean delete(T toDelete);

}
