package com.szas.server.gwt.client;

import org.junit.Before;

public class RemoteDAOImplTest extends UniversalDAOImplTest {
	@Before
	public void setUp() {
		universalDAO =
			new RemoteDAOImpl<MockElement>();
	}
}
