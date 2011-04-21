package com.szas.server.gwt.client.router;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RouterTest {
	

	protected static class MockClass {
		int data = 0;
		public MockClass(int data) {
			this.data = data;
		}
	}
	private static final String USER = "user";
	private static final String USER_PARAMS = "SDFSDF";
	protected static final int USER_DATA = 0;
	
	private static final String GROUP = "group";
	protected static final int GROUP_DATA = 1;
	
	private static final String OTHER = "other";
	
	
	protected Router<MockClass> router;
	@Before
	public void setUp() {
		router = new RouterImpl<MockClass>();
	}
	private boolean executedAfterCorrectMatch;
	
	@Test
	public void testSimpleRoute() {
		router.addRoute(USER, new RouteAction<MockClass>() {
			@Override
			public MockClass run(String command, String params) {
				assertEquals(USER_PARAMS, params);
				return new MockClass(USER_DATA);
			}
		});
		router.addRoute(GROUP, new RouteAction<MockClass>() {
			@Override
			public MockClass run(String command, String params) {
				return null;
			}
		});
		router.addRoute(GROUP, new RouteAction<MockClass>() {
			@Override
			public MockClass run(String command, String params) {
				return new MockClass(GROUP_DATA);
			}
		});
		executedAfterCorrectMatch = false;
		router.addRoute(GROUP, new RouteAction<MockClass>() {
			@Override
			public MockClass run(String command, String params) {
				executedAfterCorrectMatch = true;
				return null;
			}
		});
		MockClass mock;
		mock = router.route(USER+","+USER_PARAMS);
		assertEquals(USER_DATA, mock.data);
		mock = router.route(GROUP);
		assertEquals(GROUP_DATA, mock.data);
		assertFalse(executedAfterCorrectMatch);
		mock = router.route(OTHER);
		assertNull("schoud return null if not found", mock);
	}
	
	@Test
	public void testRemoveRoute() {
		RouteAction<MockClass> routeAction = new RouteAction<MockClass>() {
			@Override
			public MockClass run(String command, String params) {
				return new MockClass(GROUP_DATA);
			}
		};
		router.addRoute(GROUP, routeAction);
		MockClass mock;
		mock = router.route(GROUP);
		assertNotNull(mock);
		boolean removed = router.removeRoute(GROUP, routeAction);
		assertTrue(removed);
		mock = router.route(GROUP);
		assertNull(mock);
		removed = router.removeRoute(GROUP, routeAction);
		assertFalse(removed);
	}
}
