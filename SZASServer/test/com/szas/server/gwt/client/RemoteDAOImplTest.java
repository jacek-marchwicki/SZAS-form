package com.szas.server.gwt.client;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class RemoteDAOImplTest extends UniversalDAOImplTest {
	private static final long EXAMPLE_TIMESTAMP = 5;
	private RemoteDAO<MockElement> remoteDAO;

	@Before
	public void setUp() {
		RemoteDAOImpl<MockElement> remoteDAOImpl =
			new RemoteDAOImpl<MockElement>();
		universalDAO = remoteDAOImpl;
		contentObserverProviderImpl = remoteDAOImpl;
		remoteDAO = remoteDAOImpl;
	}
	
	@Test
	public void testSyncElements() {
		assertTrue("not implemented test", false);
	}
	
	@Test
	public void testGetTimestamp() {
		assertTrue("not implemented test", false);
	}
	private class WrongClass {
	};
	@Test(expected=WrongObjectThrowable.class)
	public void testWrongUnknownElements() throws WrongObjectThrowable {	
		ArrayList<Object> elements = 
			new ArrayList<Object>();
		elements.add(new WrongClass());
		remoteDAO.syncUnknownElements(elements, EXAMPLE_TIMESTAMP);
	}
}
