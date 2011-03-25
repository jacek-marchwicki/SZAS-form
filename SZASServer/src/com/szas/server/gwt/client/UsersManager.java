package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UsersManager implements EntryPoint {

	private static final int REFRESH_INTERVAL = 2000; //ms

	private final UsersServiceAsync usersServiceAsync = GWT
	.create(UsersService.class);

	private FlexTable usersFlexTable = new FlexTable();

	@Override
	public void onModuleLoad() {
		VerticalPanel mainPanel = new VerticalPanel();
		usersFlexTable.setText(0, 0, "UserName");
		mainPanel.add(usersFlexTable);

		final Button sendButton = new Button("Send");
		mainPanel.add(sendButton);

		RootPanel.get("sendButtonContainer").add(mainPanel);

		Timer refreshTimer = new Timer() {
			@Override
			public void run() {
				fetchUsersList();
			}
		};
		//refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

	}

	protected void fetchUsersList() {
		usersServiceAsync.getUsers(new AsyncCallback<ArrayList<String>>() {
			
			@Override
			public void onSuccess(ArrayList<String> result) {
				refresUsersList(result);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	protected void refresUsersList(ArrayList<String> users) {
		for (String user : users) {
			int row = usersFlexTable.getRowCount();
			usersFlexTable.setText(row, 0, user);
		}
	}

}
