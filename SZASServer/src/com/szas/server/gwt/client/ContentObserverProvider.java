package com.szas.server.gwt.client;

public interface ContentObserverProvider {

	void addContentObserver(ContentObserver contentObserver);

	boolean removeContentObserver(ContentObserver contentObserver);

}
