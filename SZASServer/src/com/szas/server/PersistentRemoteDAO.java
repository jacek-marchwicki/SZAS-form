package com.szas.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.szas.sync.ContentObserver;
import com.szas.sync.ContentObserverProviderImpl;
import com.szas.sync.Tuple;
import com.szas.sync.WrongObjectThrowable;
import com.szas.sync.local.LocalTuple;
import com.szas.sync.remote.RemoteDAO;
import com.szas.sync.remote.RemoteTuple;

public class PersistentRemoteDAO<T extends Tuple> extends ContentObserverProviderImpl implements RemoteDAO<T> {
	private static final long serialVersionUID = 1L;

	private Class<T> tupleClass;

	public PersistentRemoteDAO(Class<T> tupleClass) {
		this.tupleClass = tupleClass;
	}

	@Override
	public long getTimestamp() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(PersistentRemoteTuple.class);
			query.setFilter("className == currentClassName");
			query.setOrdering("timestamp desc");
			query.setRange(0,1);
			query.declareParameters("String lastNameParam");
			@SuppressWarnings("unchecked")
			List<PersistentRemoteTuple> results =
				(List<PersistentRemoteTuple>) query.execute(tupleClass.getName());
			if (results == null || results.isEmpty())
				return -1;
			Iterator<PersistentRemoteTuple> iterator = results.iterator();
			for (;iterator.hasNext();) {
				PersistentRemoteTuple persistentRemoteTuple =
					iterator.next();
				return persistentRemoteTuple.getTimestamp();
			}
		} finally {
			pm.close();
		}
		return -1;
	}

	private long getNextTimestamp() {
		return getTimestamp() + 1;
	}

	@Override
	public ArrayList<T> getAll() {
		ArrayList<T> ret = new ArrayList<T>();

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(PersistentRemoteTuple.class);
			query.setFilter("className == currentClassName");
			query.setFilter("deleted == false");
			query.declareParameters("String lastNameParam");
			@SuppressWarnings("unchecked")
			List<PersistentRemoteTuple> results =
				(List<PersistentRemoteTuple>) query.execute(tupleClass.getName());
			if (results == null || results.isEmpty())
				return ret;

			for (PersistentRemoteTuple remoteTuple : results) {
				if (remoteTuple.isDeleted())
					continue;
				T element = (T) remoteTuple.getElement();
				ret.add(element);
			}
		} finally {
			pm.close();
		}
		return ret;
	}

	@Override
	public void insert(T element) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			PersistentRemoteTuple remoteTuple = new PersistentRemoteTuple();
			remoteTuple.setClassName(tupleClass.getName());
			remoteTuple.setElement(element);
			remoteTuple.setDeleted(false);
			remoteTuple.setTimestamp(getNextTimestamp());
			pm.makePersistent(remoteTuple);
			notifyContentObservers();
		} finally {
			pm.close();
		}
	}

	@Override
	public void delete(T element) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(PersistentRemoteTuple.class);
			query.setFilter("className == currentClassName");
			query.declareParameters("String lastNameParam");
			@SuppressWarnings("unchecked")
			List<PersistentRemoteTuple> results =
				(List<PersistentRemoteTuple>) query.execute(tupleClass.getName());

			if (results == null || results.isEmpty())
				return;

			for (PersistentRemoteTuple remoteTuple : results) {
				T listElement = (T) remoteTuple.getElement();
				if (listElement.getId() != element.getId())
					continue;
				remoteTuple.setTimestamp(getNextTimestamp());
				remoteTuple.setDeleted(true);
				break;
			}
			notifyContentObservers();
		} finally {
			pm.close();
		}
	}

	@Override
	public void update(T element) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(PersistentRemoteTuple.class);
			query.setFilter("className == currentClassName");
			query.declareParameters("String lastNameParam");
			@SuppressWarnings("unchecked")
			List<PersistentRemoteTuple> results =
				(List<PersistentRemoteTuple>) query.execute(tupleClass.getName());

			if (results == null || results.isEmpty())
				return;

			for (PersistentRemoteTuple remoteTuple : results) {
				T listElement = (T) remoteTuple.getElement();
				if (listElement.getId() != element.getId())
					continue;
				remoteTuple.setElement(element);
				remoteTuple.setTimestamp(getNextTimestamp());
				break;
			}
			notifyContentObservers();
		} finally {
			pm.close();
		}
	}

	@Override
	public ArrayList<RemoteTuple<T>> syncElements(ArrayList<LocalTuple<T>> elements, long lastTimestamp) {
		ArrayList<RemoteTuple<T>> ret = 
			new ArrayList<RemoteTuple<T>>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(PersistentRemoteTuple.class);
			query.setFilter("className == currentClassName");
			query.declareParameters("String lastNameParam");
			@SuppressWarnings("unchecked")
			List<PersistentRemoteTuple> results =
				(List<PersistentRemoteTuple>) query.execute(tupleClass.getName());

			for (LocalTuple<T> localTuple : elements) {
				T localElement = localTuple.getElement();
				boolean found = false;
				for (PersistentRemoteTuple remoteTuple : results) {
					T remoteElement = (T) remoteTuple.getElement();
					if (localElement.getId() != remoteElement.getId()) 
						continue;
					if (remoteTuple.getTimestamp() > lastTimestamp) {
						// inserting was synced before changes
						// TODO return exception
						found = true;
						break;
					}
					remoteTuple.setElement(localElement);
					remoteTuple.setDeleted(localTuple.getStatus() == LocalTuple.Status.DELETING);
					remoteTuple.setTimestamp(getNextTimestamp());
					found = true;
					break;
				}
				if (!found) {
					PersistentRemoteTuple remoteTuple = new PersistentRemoteTuple();
					remoteTuple.setElement(localElement);
					remoteTuple.setDeleted(localTuple.getStatus() == LocalTuple.Status.DELETING);
					remoteTuple.setTimestamp(getNextTimestamp());
					remoteTuple.setClassName(tupleClass.getName());
					pm.makePersistent(remoteTuple);
				}
			}
		} finally {
			pm.close();
		}
		pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(PersistentRemoteTuple.class);
			query.setFilter("className == currentClassName");
			query.setFilter("timestamp >= lastTimestamp");
			query.declareParameters("String lastNameParam, long lastTimestamp");
			List<PersistentRemoteTuple> results =
				(List<PersistentRemoteTuple>) query.execute(tupleClass.getName(),lastTimestamp);


			for (PersistentRemoteTuple remoteTuple : results) {
				RemoteTuple<T> remoteTuple2 = new RemoteTuple<T>();
				remoteTuple2.setDeleted(remoteTuple.isDeleted());
				remoteTuple2.setElement((T) remoteTuple.getElement());
				remoteTuple2.setTimestamp(remoteTuple.getTimestamp());
				ret.add(remoteTuple2);
			}
			notifyContentObservers();
		} finally {
			pm.close();
		}
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
}
