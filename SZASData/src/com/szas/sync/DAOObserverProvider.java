package com.szas.sync;

import java.io.Serializable;

public interface DAOObserverProvider extends Serializable{

	void addDAOObserver(DAOObserver daoObserver);

	boolean removeDAOObserver(DAOObserver daoObserver);

}
