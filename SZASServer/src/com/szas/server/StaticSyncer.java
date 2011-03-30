package com.szas.server;

import com.szas.data.UserTuple;
import com.szas.sync.remote.RemoteDAO;
import com.szas.sync.remote.RemoteDAOImpl;
import com.szas.sync.remote.RemoteSyncHelper;
import com.szas.sync.remote.RemoteSyncHelperImpl;

public final class StaticSyncer {
	private final static RemoteSyncHelper syncHelper;
	private final static RemoteDAO<UserTuple> usersDAO;
	static {
		syncHelper = new RemoteSyncHelperImpl();
		usersDAO = new RemoteDAOImpl<UserTuple>();
		syncHelper.append("users", usersDAO);
		
		UserTuple user;
		user = new UserTuple();
		user.setName("Jacek");
		usersDAO.insert(user);
		
		user = new UserTuple();
		user.setName("Paweł");
		usersDAO.insert(user);
		
		user = new UserTuple();
		user.setName("Tomek");
		usersDAO.insert(user);
	}
	public static RemoteSyncHelper getSyncHelper() {
		return syncHelper;
	}
	public static RemoteDAO<UserTuple> getUsersDAO() {
		return usersDAO;
	}
}
