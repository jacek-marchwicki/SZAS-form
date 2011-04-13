package com.szas.server.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.szas.sync.local.SyncObserver;

public class Questionnaries implements EntryPoint {

	@Override
	public void onModuleLoad() {
		MainWidget mainWidget = new MainWidget();
		RootPanel.get("body").add(mainWidget);
		SyncObserver syncObserver = new SyncObserver() {

			@Override
			public void onSucces() {
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFail(Throwable caught) {
				Window.alert("Error while getting data: " + caught.getMessage());
			}
		};
		StaticGWTSyncer.getSynchelper().addSyncObserver(syncObserver);
	}
}
