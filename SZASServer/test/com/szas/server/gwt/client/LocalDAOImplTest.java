package com.szas.server.gwt.client;

import org.junit.Before;

public class LocalDAOImplTest extends UniversalDAOImplTest {
	@Before
	public void setUp() {
		universalDAO =
			new LocalDAOImpl<MockElement>();
	}
}
