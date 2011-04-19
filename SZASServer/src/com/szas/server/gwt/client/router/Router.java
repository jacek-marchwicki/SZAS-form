package com.szas.server.gwt.client.router;

public interface Router<T> {
	public T route(String token);
	public void addRoute(String command, RouteAction<T> action);
	public boolean removeRoute(String command, RouteAction<T> routeAction);

}
