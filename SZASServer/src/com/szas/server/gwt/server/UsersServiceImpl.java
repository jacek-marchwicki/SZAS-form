package com.szas.server.gwt.server;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.szas.server.gwt.client.UsersService;

@SuppressWarnings("serial")
public class UsersServiceImpl extends RemoteServiceServlet implements UsersService {

	@Override
	public ArrayList<String> getUsers() {
		ArrayList<String> userList = new ArrayList<String>();
		userList.add("jacek");
		userList.add("Franek");
		userList.add("mikołaj");
		return userList;
	}

}
