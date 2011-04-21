package com.szas.server.gwt.client.router;

public abstract class LongRouteAction<T> implements RouteAction<T> {
	@Override
	public T run(String command, String params) {
		try {
			long param = Long.parseLong(params);
			return run(command,param);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	protected abstract T run(String command, long param);

}
