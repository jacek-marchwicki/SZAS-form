package com.szas.server.gwt.client;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class UniversalServiceImplTest {
	private class MockElement extends Tuple {
		int data = 0;
	}
	private static final int EXAMPLE_DATA = 5;
	UniversalService<MockElement> universalService;
	@Before
	public void setUp() {
		universalService =
			new UniversalServiceImpl<UniversalServiceImplTest.MockElement>();
	}
	
	@Test
	public void testEmptyList() {
		int size = universalService.getAll().size();
		assertEquals("Empty list schould be 0",size,0);
	}
	
	@Test
	public void testInsertElement() {
		MockElement mockElement = new MockElement();
		mockElement.data = EXAMPLE_DATA;
		universalService.insert(mockElement);
		int size = universalService.getAll().size();
		assertEquals("Size of list after insertion schould be 1",size,1);
		ArrayList<MockElement> mockElements = 
			universalService.getAll();
		MockElement receivedMockElement = mockElements.get(0);
		assertEquals("Received value schould be equals as inserted", 
				EXAMPLE_DATA, receivedMockElement.data);
	}
	
	@Test
	public void testUpdateElement() {
		MockElement mockElement = new MockElement();
		universalService.insert(mockElement);
		mockElement.data = EXAMPLE_DATA;
		universalService.update(mockElement);
		
		ArrayList<MockElement> mockElements = 
			universalService.getAll();
		MockElement receivedMockElement = mockElements.get(0);
		assertEquals("Received value schould be equals as inserted", 
				receivedMockElement.data, EXAMPLE_DATA);
	}
	
}
