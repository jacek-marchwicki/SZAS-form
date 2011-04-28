package com.szas.sync;

/**
 * Interface of class which can watch for changes on database
 * @author Jacek Marchwicki
 *
 */
public interface DAOObserver {
	public void onChange(boolean whileSync);
}
