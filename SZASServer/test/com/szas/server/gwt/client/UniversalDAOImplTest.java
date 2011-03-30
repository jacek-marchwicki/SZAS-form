package com.szas.server.gwt.client;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class UniversalDAOImplTest extends ContentObserverProviderImplTest {
	protected class MockElement extends Tuple {
		int data = 0;
		public void testEuality(MockElement other) {
			assertEquals(data, other.data);
			assertEquals(getId(), other.getId());
		}
		public MockElement copy(MockElement element) {
			MockElement mockElement = new MockElement();
			mockElement.id = element.getId();
			mockElement.data = element.data;
			return mockElement;
		}
	}
	protected static final int EXAMPLE_DATA = 5;
	protected static final int NEW_EXAMPLE_DATA = 6;
	protected UniversalDAO<MockElement> universalDAO;
	@Before
	public void setUp() {
		UniversalDAOImpl<MockElement> universalDAOImpl =
			new UniversalDAOImpl<MockElement>();
		universalDAO =
			universalDAOImpl;
		contentObserverProviderImpl = universalDAOImpl;
	}
	
	@Test
	public void testEmptyList() {
		int size = universalDAO.getAll().size();
		assertEquals("Empty list schould be 0",size,0);
	}
	
	@Test
	public void testInsertElement() {
		MyContentObserver myContentObserver = new MyContentObserver();
		contentObserverProviderImpl.addContentObserver(myContentObserver);
		
		MockElement mockElement = new MockElement();
		mockElement.data = EXAMPLE_DATA;
		universalDAO.insert(mockElement);
		
		assertTrue("Content observer schould be notiffied", myContentObserver.notiffied);
		
		int size = universalDAO.getAll().size();
		assertEquals("Size of list after insertion schould be 1",size,1);
		ArrayList<MockElement> mockElements = 
			universalDAO.getAll();
		MockElement receivedMockElement = mockElements.get(0);
		assertEquals("Received value schould be equals as inserted", 
				EXAMPLE_DATA, receivedMockElement.data);
	}
	
	@Test
	public void testUpdateElement() {
		MyContentObserver myContentObserver = new MyContentObserver();
		contentObserverProviderImpl.addContentObserver(myContentObserver);
		
		MockElement mockElement = new MockElement();
		universalDAO.insert(mockElement);
		mockElement.data = EXAMPLE_DATA;
		myContentObserver.notiffied = false;
		
		universalDAO.update(mockElement);
		
		assertTrue("Content observer schould be notiffied", myContentObserver.notiffied);
		
		ArrayList<MockElement> mockElements = 
			universalDAO.getAll();
		MockElement receivedMockElement = mockElements.get(0);
		assertEquals("Received value schould be equals as inserted", 
				receivedMockElement.data, EXAMPLE_DATA);
	}
	
	@Test
	public void testDeleteElement() {
		MyContentObserver myContentObserver = new MyContentObserver();
		contentObserverProviderImpl.addContentObserver(myContentObserver);
		
		MockElement mockElement = new MockElement();
		universalDAO.insert(mockElement);
		myContentObserver.notiffied = false;
		
		universalDAO.delete(mockElement);
		
		assertTrue("Content observer schould be notiffied", myContentObserver.notiffied);
		
		assertEquals("Element schould be deleted", 0, universalDAO.getAll().size());
	}
	
}
