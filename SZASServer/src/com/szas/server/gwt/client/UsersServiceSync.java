package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.szas.sync.ContentObserver;

public class UsersServiceSync implements UsersService {

	private final UsersServiceAsync usersServiceAsync = GWT
	.create(UsersService.class);

	private ArrayList<ContentObserver> contentObservers =
	new ArrayList<ContentObserver>();

	ArrayList<String> users = new ArrayList<String>();

	@Override
	public ArrayList<String> getUsers() {
		return users;
	}

	public void fetchUsers() {
		usersServiceAsync.getUsers(new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onSuccess(ArrayList<String> result) {
				users = result;
				notifyContentObserver();
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	private void notifyContentObserver() {
		for (ContentObserver contentObserver : contentObservers) {
			contentObserver.onChange();
		}
	}
	
	public void registerContentObserver(ContentObserver contentObserver) {
		contentObservers.add(contentObserver);
	}
	public void unregisterContentObserver(ContentObserver contentObserver) {
		contentObservers.remove(contentObserver);
	}

}
