package com.szas.sync;

import java.util.Collection;

/**
 * Provides access to some type of data stored in tuple type database
 * @author Jacek Marchwicki
 *
 * @param <T> type of stored tuple
 */
public interface UniversalDAO<T extends Tuple> extends DAOObserverProvider {
	/**
	 * Return all elements form database
	 * @return all elements from database
	 */
	public Collection<T> getAll();
	
	/**
	 * Get element by id
	 * @param id - of element to receive
	 * @return element or null
	 */
	public T getById(long id);
	
	/**
	 * Insert new element to DAO
	 * @param element element to insertion
	 */
	public void insert(T element);
	
	/**
	 * Delete element from DAO
	 * @param element to delete
	 */
	public void delete(T element);
	
	/**
	 * Update element in DAO
	 * @param element to update
	 */
	public void update(T element);
}
