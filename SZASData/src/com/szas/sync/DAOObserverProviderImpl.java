package com.szas.sync;

import java.util.ArrayList;
import java.util.Collection;

public class DAOObserverProviderImpl implements DAOObserverProvider {
	private static final long serialVersionUID = 1L;
	Collection<DAOObserver> daoObservers =
		new ArrayList<DAOObserver>();
	
	@Override
	public void addDAOObserver(DAOObserver daoObserver) {
		daoObservers.add(daoObserver);
	}
	
	@Override
	public boolean removeDAOObserver(DAOObserver daoObserver) {
		return daoObservers.remove(daoObserver);
	}
	
	protected void notifyContentObservers(boolean whileSync) {
		for (DAOObserver daoObserver : daoObservers) {
			daoObserver.onChange(whileSync);
		}
	}
}
