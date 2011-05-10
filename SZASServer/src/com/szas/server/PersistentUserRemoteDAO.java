package com.szas.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.annotation.Serialized;
import com.googlecode.objectify.util.DAOBase;
import com.szas.server.PersistentRemoteDAO.PersistentRemoteTuple;
import com.szas.sync.DAOObserverProviderImpl;
import com.szas.sync.RightsTuple;
import com.szas.sync.RightsTuple.Right;
import com.szas.sync.WrongObjectThrowable;
import com.szas.sync.local.LocalTuple;
import com.szas.sync.remote.RemoteDAO;
import com.szas.sync.remote.RemoteTuple;

public class PersistentUserRemoteDAO<T extends RightsTuple> extends DAOObserverProviderImpl implements RemoteDAO<T> {
	private static final long serialVersionUID = 1L;
	private Class<T> tupleClass;
	
	public PersistentUserRemoteDAO(Class<T> tupleClass, String userId) {
		this.tupleClass = tupleClass;
		databaseUser = dao.ofy().query(DatabaseUser.class)
		.filter("uid", userId).getKey();
	}
	
	private static class DatabaseUser {
		public String uid;
		public String email;
	}
	
	private static class UserRights {
		Key<DatabaseUser> user;
		long id;
	}
	
	private static class PersistentRemoteTuple {
		@Id Long id;
		long timestamp;
		boolean deleted;
		@Serialized
		Serializable element;
		public long insertionTimestamp;
		public String className;
		public List<UserRights> usersRights;
	}
	
	private static class DAO extends DAOBase {
		static {
			ObjectifyService.register(PersistentRemoteTuple.class);
		}
	}
	
	DAO dao = new DAO();
	private Key<DatabaseUser> databaseUser;

	@Override
	public Collection<T> getAll() {
		ArrayList<T> ret = new ArrayList<T>();
		
		Query<PersistentRemoteTuple> results =
			dao.ofy().query(PersistentRemoteTuple.class)
			.filter("className",tupleClass.getName())
			.filter("usersRights.user", databaseUser)
			.filter("deleted", false);
		
		HashMap<Key<DatabaseUser>, String> usersIds = new HashMap<Key<DatabaseUser>, String>();

		for (PersistentRemoteTuple remoteTuple : results) {
			if (remoteTuple.deleted)
				continue;			
			for (UserRights userRights : remoteTuple.usersRights) {
				usersIds.put(userRights.user, null);
			}
		}
		Map<Key<DatabaseUser>, DatabaseUser> users = dao.ofy().get(usersIds.keySet());
		for (PersistentRemoteTuple remoteTuple : results) {
			if (remoteTuple.deleted)
				continue;
			T element = (T) remoteTuple.element;
			fixEmails(remoteTuple, users);
			
			
			ret.add(element);
		}
		return ret;
	}
	
	private void fixEmails(PersistentRemoteTuple remoteTuple, Map<Key<DatabaseUser>, DatabaseUser> users) {
		boolean changed = false;
		T element = (T) remoteTuple.element;
		ArrayList<Right> rights = element.getRights();
		Iterator<Right> iterator = rights.iterator();
		while (iterator.hasNext()) {
			Right right = iterator.next();
			String email = findEmail(users, remoteTuple.usersRights, right.id);
			if (email == null) {
				rights.remove(right);
				changed = true;
			} else if ( ! right.email.equals(email)) {
				right.email = email;
				changed = true;
			}
		}
		
		if (changed) {
			dao.ofy().put(remoteTuple);
		}
	}

	private String findEmail(Map<Key<DatabaseUser>, DatabaseUser> users,
			List<UserRights> usersRights, long id) {
		for (UserRights userRights : usersRights) {
			if (userRights.id == id) {
				DatabaseUser databaseUser = users.get(userRights.user);
				return databaseUser.email;
			}
		}
		return null;
	}

	@Override
	public T getById(long id) {
		PersistentRemoteTuple remoteTuple =
			getPersistentById(id);
		if (remoteTuple == null)
			return null;
		if (remoteTuple.deleted == true)
			return null;
		@SuppressWarnings("unchecked")
		Collection<Key<DatabaseUser>> usersIds = CollectionUtils.collect(remoteTuple.usersRights, new Transformer() {
			@Override
			public Object transform(Object arg0) {
				return ((UserRights) arg0).user;
			}
		});
		
		Map<Key<DatabaseUser>, DatabaseUser> users =
			dao.ofy().get(usersIds);
		fixEmails(remoteTuple, users);
		return (T) remoteTuple.element;
	}
	
	private long getNextTimestamp() {
		return getTimestamp() + 1;
	}

	@Override
	public void insert(T element) {
		long nextTimestamp = getNextTimestamp();
		PersistentRemoteTuple remoteTuple = new PersistentRemoteTuple();
		remoteTuple.deleted = false;
		remoteTuple.element = element;
		remoteTuple.timestamp = nextTimestamp;
		remoteTuple.insertionTimestamp = nextTimestamp;
		remoteTuple.className = tupleClass.getName();
		remoteTuple.id = element.getId();
		dao.ofy().put(remoteTuple);
		notifyContentObservers(false);
	}

	@Override
	public void delete(T element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(T element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<RemoteTuple<T>> syncElements(
			ArrayList<LocalTuple<T>> elements, long lastTimestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> syncUnknownElements(ArrayList<Object> elements,
			long lastTimestamp) throws WrongObjectThrowable {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTimestamp() {
		PersistentRemoteTuple result =
			dao.ofy().query(PersistentRemoteTuple.class)
			.filter("className", tupleClass.getName())
			.order("-timestamp").get();
		if (result == null)
			return -1;
		return result.timestamp;
	}

	private PersistentRemoteTuple getPersistentById(long id) {
		PersistentRemoteTuple remoteTuple = dao.ofy().query(PersistentRemoteTuple.class)
		.filter("className", tupleClass.getName())
		.filter("id", id)
		.get();
		return remoteTuple;
	}
}
