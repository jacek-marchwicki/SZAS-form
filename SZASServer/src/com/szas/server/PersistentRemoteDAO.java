package com.szas.server;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;
import com.szas.sync.DAOObserverProviderImpl;
import com.szas.sync.Tuple;
import com.szas.sync.WrongObjectThrowable;
import com.szas.sync.local.LocalTuple;
import com.szas.sync.remote.RemoteDAO;
import com.szas.sync.remote.RemoteTuple;

public class PersistentRemoteDAO<T extends Tuple> extends DAOObserverProviderImpl implements RemoteDAO<T> {
	public static class DAO extends DAOBase {
		static {
			ObjectifyService.register(PersistentRemoteTuple.class);
		}
	}
	
	DAO dao = new DAO();

	private static final Logger log =
		Logger.getLogger(PersistentRemoteDAO.class.getName());

	private static final long serialVersionUID = 1L;

	private Class<T> tupleClass;

	public PersistentRemoteDAO(Class<T> tupleClass) {
		this.tupleClass = tupleClass;
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

	private long getNextTimestamp() {
		return getTimestamp() + 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<T> getAll() {
		ArrayList<T> ret = new ArrayList<T>();

		Query<PersistentRemoteTuple> results =
			dao.ofy().query(PersistentRemoteTuple.class)
			.filter("className",tupleClass.getName())
			.filter("deleted", false);

		for (PersistentRemoteTuple remoteTuple : results) {
			if (remoteTuple.deleted)
				continue;
			T element = (T) remoteTuple.element;
			ret.add(element);
		}
		return ret;
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
		PersistentRemoteTuple remoteTuple = getPersistentById(element.getId());
		if (remoteTuple == null)
			return;
		remoteTuple.timestamp = getNextTimestamp();
		remoteTuple.deleted = true;
		dao.ofy().put(remoteTuple);
		notifyContentObservers(false);
	}

	private PersistentRemoteTuple getPersistentById(long id) {
		PersistentRemoteTuple remoteTuple = dao.ofy().query(PersistentRemoteTuple.class)
		.filter("className", tupleClass.getName())
		.filter("id", id)
		.get();
		return remoteTuple;
	}

	@Override
	public void update(T element) {
		PersistentRemoteTuple remoteTuple = getPersistentById(element.getId());
		if (remoteTuple == null)
			return;
		remoteTuple.element = element;
		remoteTuple.timestamp = getNextTimestamp();
		dao.ofy().put(remoteTuple);
		notifyContentObservers(false);
	}

	private void syncElement(LocalTuple<T> localTuple, long lastTimestamp) {
		T localElement = localTuple.getElement();
		PersistentRemoteTuple remoteTuple = getPersistentById(localElement.getId());

		if (remoteTuple != null) {
			// not found
			if (remoteTuple.timestamp > lastTimestamp) {
				log.warning("Item changed on server");
				return;
			}
			if (remoteTuple.deleted) {
				log.severe("Item already deleted on server");
				return;
			}
			if (localTuple.getStatus() ==  LocalTuple.Status.INSERTING) {
				log.warning("Already inserted");
				return;
			}
			log.info("Updating/Deleting element");
			remoteTuple.deleted = localTuple.getStatus() == LocalTuple.Status.DELETING;
			remoteTuple.element = localElement;
			remoteTuple.timestamp = getNextTimestamp();
			dao.ofy().put(remoteTuple);
		} else {
			if (localTuple.getStatus() ==  LocalTuple.Status.UPDATING) {
				log.warning("Nothing to update");
				return;
			}
			if (localTuple.getStatus() ==  LocalTuple.Status.DELETING) {
				log.severe("Nothing to delete");
				return;
			}
			if (localTuple.getStatus() ==  LocalTuple.Status.INSERTING) {
				log.info("inserting");

				long nextTimestamp = getNextTimestamp();

				remoteTuple = new PersistentRemoteTuple();
				remoteTuple.deleted = false;
				remoteTuple.timestamp = nextTimestamp;
				remoteTuple.insertionTimestamp = nextTimestamp;
				remoteTuple.className = tupleClass.getName();
				remoteTuple.id = localElement.getId();
				remoteTuple.element = localElement;
				dao.ofy().put(remoteTuple);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<RemoteTuple<T>> syncElements(ArrayList<LocalTuple<T>> elements, long lastTimestamp) {
		ArrayList<RemoteTuple<T>> ret = 
			new ArrayList<RemoteTuple<T>>();

		for (LocalTuple<T> localTuple : elements) {
			syncElement(localTuple, lastTimestamp);
		}

		Query<PersistentRemoteTuple> results = 
			dao.ofy().query(PersistentRemoteTuple.class)
			.filter("className", tupleClass.getName())
			.filter("timestamp >", lastTimestamp);


		for (PersistentRemoteTuple remoteTuple : results) {
			if (remoteTuple.deleted) {
				if (remoteTuple.insertionTimestamp > lastTimestamp)
					continue;
			}
			RemoteTuple<T> remoteTuple2 = new RemoteTuple<T>();
			remoteTuple2.setDeleted(remoteTuple.deleted);
			remoteTuple2.setElement((T) remoteTuple.element);
			remoteTuple2.setTimestamp(remoteTuple.timestamp);
			ret.add(remoteTuple2);
		}
		if (elements.size() > 0)
			notifyContentObservers(true);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Object> syncUnknownElements(ArrayList<Object> elements, long lastTimestamp) throws WrongObjectThrowable {
		ArrayList<LocalTuple<T>> knownElements = 
			new ArrayList<LocalTuple<T>>();
		ArrayList<Object> ret = 
			new ArrayList<Object>();

		for (Object element : elements) {
			try {
				knownElements.add((LocalTuple<T>) element);
			} catch (ClassCastException e) {
				throw new WrongObjectThrowable();
			}
		}

		ArrayList<RemoteTuple<T>> returnList =
			syncElements(knownElements, lastTimestamp);
		for (RemoteTuple<T> element: returnList) {
			ret.add((Object)element);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getById(long id) {
		PersistentRemoteTuple persistentRemoteTuple =
			getPersistentById(id);
		if (persistentRemoteTuple == null)
			return null;
		if (persistentRemoteTuple.deleted == true)
			return null;
		return (T) persistentRemoteTuple.element;
	}
}
