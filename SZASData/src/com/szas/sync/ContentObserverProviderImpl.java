package com.szas.sync;

import java.util.ArrayList;
import java.util.Collection;

public class ContentObserverProviderImpl implements ContentObserverProvider {
	Collection<ContentObserver> contentObservers =
		new ArrayList<ContentObserver>();
	
	@Override
	public void addContentObserver(ContentObserver contentObserver) {
		contentObservers.add(contentObserver);
	}
	
	@Override
	public boolean removeContentObserver(ContentObserver contentObserver) {
		return contentObservers.remove(contentObserver);
	}
	
	protected void notifyContentObservers() {
		for (ContentObserver contentObserver : contentObservers) {
			contentObserver.onChange();
		}
	}
}
