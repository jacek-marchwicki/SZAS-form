package com.szas.sync.remote;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.szas.sync.UniversalDAOImplTest;
import com.szas.sync.WrongObjectThrowable;

public class RemoteDAOImplTest extends UniversalDAOImplTest {
	private static final long EXAMPLE_TIMESTAMP = 5;
	protected RemoteDAO<MockElement> remoteDAO;

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
		long timestamp = remoteDAO.getTimestamp();
		assertEquals(-1, timestamp);
		
		MockElement mockElement = new MockElement();
		mockElement.setData(EXAMPLE_DATA);
		universalDAO.insert(mockElement);
		timestamp = remoteDAO.getTimestamp();
		assertEquals(0, timestamp);
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
