package com.szas.sync;

import java.io.Serializable;

public interface ContentObserverProvider extends Serializable{

	void addContentObserver(ContentObserver contentObserver);

	boolean removeContentObserver(ContentObserver contentObserver);

}
