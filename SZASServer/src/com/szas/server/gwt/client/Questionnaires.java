package com.szas.server.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.szas.server.gwt.client.widgets.MainWidget;

public class Questionnaires implements EntryPoint {

	@Override
	public void onModuleLoad() {
		MainWidget mainWidget = new MainWidget();
		RootPanel.get("body").add(mainWidget);
	}
}
