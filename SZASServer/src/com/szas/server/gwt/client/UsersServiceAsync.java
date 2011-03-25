package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UsersServiceAsync {

	void getUsers(AsyncCallback<ArrayList<String>> callback);

}
