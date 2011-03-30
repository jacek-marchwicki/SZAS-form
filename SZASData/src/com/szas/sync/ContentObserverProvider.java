package com.szas.sync;

public interface ContentObserverProvider {

	void addContentObserver(ContentObserver contentObserver);

	boolean removeContentObserver(ContentObserver contentObserver);

}
