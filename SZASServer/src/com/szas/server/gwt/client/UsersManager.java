package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.szas.sync.ContentObserver;

public class UsersManager implements EntryPoint {

	private final UsersServiceSync usersServiceSync = new UsersServiceSync();
	private final UsersService usersService = usersServiceSync;

	private FlexTable usersFlexTable = new FlexTable();

	@Override
	public void onModuleLoad() {
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(usersFlexTable);

		final Button sendButton = new Button("Send");
		sendButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				usersServiceSync.fetchUsers();
			}
		});
		mainPanel.add(sendButton);

		RootPanel.get("sendButtonContainer").add(mainPanel);
		
		usersServiceSync.registerContentObserver(new ContentObserver() {
			
			@Override
			public void onChange() {
				usersChanged();
			}
		});
		usersServiceSync.fetchUsers();

	}

	protected void usersChanged() {
		usersFlexTable.removeAllRows();
		usersFlexTable.setText(0, 0, "UserName");
		ArrayList<String> users = usersService.getUsers();
		for (String user : users) {
			int row = usersFlexTable.getRowCount();
			usersFlexTable.setText(row, 0, user);
		}
	}
}
