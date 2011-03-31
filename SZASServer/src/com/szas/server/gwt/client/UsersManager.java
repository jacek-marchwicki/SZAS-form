package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.szas.data.UserTuple;
import com.szas.sync.ContentObserver;

public class UsersManager implements EntryPoint {

	private FlexTable usersFlexTable = new FlexTable();

	@Override
	public void onModuleLoad() {
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(usersFlexTable);

		final Button refreshButton = new Button("Refresh");
		refreshButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				StaticGWTSyncer.getSynchelper().sync();
			}
		});
		mainPanel.add(refreshButton);
		
		HorizontalPanel addPanel = new HorizontalPanel();
		final TextBox nameBox = new TextBox();
		addPanel.add(nameBox);
		Button button = new Button("Add");
		addPanel.add(button);
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				UserTuple userTuple = new UserTuple();
				userTuple.setName(nameBox.getText());
				StaticGWTSyncer.getUsersdao().insert(userTuple);
			}
		});
		mainPanel.add(addPanel);

		RootPanel.get("sendButtonContainer").add(mainPanel);
		
		StaticGWTSyncer.getUsersdao().addContentObserver(new ContentObserver() {
			
			@Override
			public void onChange() {
				usersChanged();
			}
		});
	}

	protected void usersChanged() {
		usersFlexTable.removeAllRows();
		usersFlexTable.setText(0, 0, "UserName");
		ArrayList<UserTuple> users = StaticGWTSyncer.getUsersdao().getAll();
		for (final UserTuple user : users) {
			final int row = usersFlexTable.getRowCount();
			usersFlexTable.setText(row, 0, user.getName());
			Button deleteButton = new Button("X");
			usersFlexTable.setWidget(row, 1, deleteButton);
			Button editButton = new Button("E");
			usersFlexTable.setWidget(row, 2, editButton);
			
			deleteButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					StaticGWTSyncer.getUsersdao().delete(user);
					
				}
			});
			editButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					final TextBox textBox = new TextBox();
					textBox.setText(user.getName());
					usersFlexTable.setWidget(row, 0, textBox);
					Button saveButton = new Button("S");
					usersFlexTable.setWidget(row, 1, saveButton);
					Button cancelButton = new Button("C");
					usersFlexTable.setWidget(row, 2, cancelButton);
					saveButton.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							String name = textBox.getText();
							user.setName(name);
							StaticGWTSyncer.getUsersdao().update(user);
						}
					});
					cancelButton.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							usersChanged();
						}
					});
					
				}
			});
		}
		
	}
}
