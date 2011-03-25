package com.szas.server.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class UserManager implements EntryPoint {

	@Override
	public void onModuleLoad() {
		// TODO Auto-generated method stub
		final Button sendButton = new Button("Send");
		RootPanel.get("sendButtonContainer").add(sendButton);
	}

}
