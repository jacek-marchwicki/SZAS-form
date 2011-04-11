package com.szas.server;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import com.szas.sync.remote.RemoteDAOImplTest;

public class PersistentRemoteDAOTest extends RemoteDAOImplTest {
	private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	@Before
	public void setUp() {
		helper.setUp();
		PersistentRemoteDAO<MockElement> persistentRemoteDAO = 
			new PersistentRemoteDAO<MockElement>(MockElement.class);
		universalDAO = persistentRemoteDAO;
		contentObserverProviderImpl = persistentRemoteDAO;
		remoteDAO = persistentRemoteDAO;
	}
	@After
	public void tearDown() {
		helper.tearDown();
	}
}
