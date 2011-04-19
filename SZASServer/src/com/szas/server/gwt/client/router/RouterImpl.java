package com.szas.server.gwt.client.router;

import java.util.ArrayList;
import java.util.HashMap;

public class RouterImpl<T> implements Router<T> {
	private static class ActionsList<T> extends ArrayList<RouteAction<T>> {
		private static final long serialVersionUID = 1L;
	};

	HashMap<String, ActionsList<T>> actions =
		new HashMap<String, RouterImpl.ActionsList<T>>();

	@Override
	public T route(String token) {
		String command = null;
		String params = null;
		String separator = ",";
		int separatorPosition = token.indexOf(separator);
		if (separatorPosition == -1) {
			command = token;
		} else {
			command = token.substring(0, separatorPosition);
			if (token.length() > separatorPosition + 1)
				params = token.substring(separatorPosition+1);
		}
		T ret = null;
		ActionsList<T> actionsList = actions.get(command);
		if (actionsList != null) {
			for (RouteAction<T> action : actionsList) {
				ret = action.run(command, params);
				if (ret != null)
					return ret;
			}
		}
		return ret;
	}

	@Override
	public void addRoute(String command, RouteAction<T> action) {
		ActionsList<T> actionsList;
		actionsList = actions.get(command);
		if (actionsList == null) {
			actionsList = new ActionsList<T>();
			actions.put(command, actionsList);
		}
		actionsList.add(action);
	}

	@Override
	public boolean removeRoute(String command, RouteAction<T> routeAction) {
		ActionsList<T> actionsList;
		actionsList = actions.get(command);
		if (actionsList == null)
			return false;
		return actionsList.remove(routeAction);
	}

}
