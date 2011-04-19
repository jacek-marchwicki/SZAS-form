package com.szas.server.gwt.client.router;

public interface RouteAction<T> {
	T run(String command, String params);
}
