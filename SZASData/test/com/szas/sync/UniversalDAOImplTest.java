package com.szas.sync;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class UniversalDAOImplTest extends ContentObserverProviderImplTest {
	protected static class MockElement extends Tuple {
		private static final long serialVersionUID = 1L;
		private int data = 0;
		public MockElement() {
		}
		public void testEuality(MockElement other) {
			assertEquals(getData(), other.getData());
			assertEquals(getId(), other.getId());
		}
		public MockElement copy(MockElement element) {
			MockElement mockElement = new MockElement();
			mockElement.id = element.getId();
			mockElement.setData(element.getData());
			return mockElement;
		}
		public void setData(int data) {
			this.data = data;
		}
		public int getData() {
			return data;
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
		contentObserverProviderImpl.addDAOObserver(myContentObserver);
		
		MockElement mockElement = new MockElement();
		mockElement.setData(EXAMPLE_DATA);
		universalDAO.insert(mockElement);
		
		assertTrue("Content observer schould be notiffied", myContentObserver.notiffied);
		
		int size = universalDAO.getAll().size();
		assertEquals("Size of list after insertion schould be 1",1,size);
		Collection<MockElement> mockElements = 
			universalDAO.getAll();
		MockElement receivedMockElement = mockElements.iterator().next();
		assertEquals("Received value schould be equals as inserted", 
				EXAMPLE_DATA, receivedMockElement.getData());
		
		long id = mockElement.getId();
		MockElement elementById = universalDAO.getById(id);
		assertNotNull(elementById);
		assertEquals(mockElement.getData(), elementById.getData());
	}
	
	@Test
	public void testUpdateElement() {
		MyContentObserver myContentObserver = new MyContentObserver();
		contentObserverProviderImpl.addDAOObserver(myContentObserver);
		
		MockElement mockElement = new MockElement();
		universalDAO.insert(mockElement);
		mockElement.setData(EXAMPLE_DATA);
		myContentObserver.notiffied = false;
		
		universalDAO.update(mockElement);
		
		assertTrue("Content observer schould be notiffied", myContentObserver.notiffied);
		
		Collection<MockElement> mockElements = 
			universalDAO.getAll();
		assertEquals("Size of list after insertion schould be 1",1,mockElements.size());
		MockElement receivedMockElement = mockElements.iterator().next();
		assertEquals("Received value schould be equals as inserted", 
				EXAMPLE_DATA, receivedMockElement.getData());
		
		long id = mockElement.getId();
		MockElement elementById = universalDAO.getById(id);
		assertNotNull(elementById);
		assertEquals(mockElement.getData(), elementById.getData());
	}
	
	@Test
	public void testDeleteElement() {
		MyContentObserver myContentObserver = new MyContentObserver();
		contentObserverProviderImpl.addDAOObserver(myContentObserver);
		
		MockElement mockElement = new MockElement();
		universalDAO.insert(mockElement);
		myContentObserver.notiffied = false;
		
		universalDAO.delete(mockElement);
		
		assertTrue("Content observer schould be notiffied", myContentObserver.notiffied);
		
		assertEquals("Element schould be deleted", 0, universalDAO.getAll().size());
		
		long id = mockElement.getId();
		MockElement elementById = universalDAO.getById(id);
		assertNull(elementById);
	}
	
}
