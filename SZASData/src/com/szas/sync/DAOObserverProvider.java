package com.szas.sync;

import java.io.Serializable;

/**
 * DAOObserverProvider allow add observer to watch changes on database and act after changes made
 * @author Jacek Marchwicki
 *
 */
public interface DAOObserverProvider extends Serializable{

	/**
	 * Add observer to database
	 * @param daoObserver implementation of DAOObserver to be added
	 */
	void addDAOObserver(DAOObserver daoObserver);

	/**
	 * Remove observer from database
	 * @param daoObserver implementation of DAOObserver to be removed
	 * @return true if observer was in database
	 */
	boolean removeDAOObserver(DAOObserver daoObserver);

}
