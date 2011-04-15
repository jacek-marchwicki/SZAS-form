package com.szas.server.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.UserTuple;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;

public class MainWidget extends Composite {

	private static MainWidgetUiBinder uiBinder = GWT
	.create(MainWidgetUiBinder.class);

	@UiField SimplePanel simplePanel;
	@UiField Button refreshButton;
	@UiField Label syncStatusLabel;

	private AutoSyncer.AutoSyncerObserver autoSyncerObserver;

	

	private Widget widget;

	interface MainWidgetUiBinder extends UiBinder<Widget, MainWidget> {
	}

	public MainWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		ValueChangeHandler<String> valueChangeHandler =
			new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String historyToken = event.getValue();
				parseToken(historyToken);
			}
		};
		History.addValueChangeHandler(valueChangeHandler);
		History.fireCurrentHistoryState();
	}

	protected void switchWidget(Widget newWidget) {
		if (widget != null)
			simplePanel.remove(widget);
		widget = newWidget;
		simplePanel.add(widget);
	}
	
	@Override
	protected void onAttach() {
		super.onAttach();
		autoSyncerObserver = new AutoSyncer.AutoSyncerObserver() {

			@Override
			public void onStarted() {
				syncStatusLabel.setText("Syncing...");
			}

			@Override
			public void onSuccess() {
				syncStatusLabel.setText("");
			}

			@Override
			public void onFail() {
				syncStatusLabel.setText("FAIL");
			}

			@Override
			public void onWait(int waitTime) {
				syncStatusLabel.setText("Waiting: " + waitTime);
			}
			
		};
		StaticGWTSyncer.getAutosyncer().addAutoSyncerObserver(autoSyncerObserver);
		
	}
	protected void parseToken(String historyToken) {
		String command = null;
		String params = null;
		String separator = ",";
		int separatorPosition = historyToken.indexOf(separator);
		if (separatorPosition == -1) {
			command = historyToken;
		} else {
			command = historyToken.substring(0, separatorPosition);
			if (historyToken.length() > separatorPosition + 1)
				params = historyToken.substring(separatorPosition+1);
		}
		System.out.println("command: '"+command+"'");
		if (params != null) {
			System.out.println("params: "+params);
		}
		if (command.equals("") || command.equals("users")) {
			System.out.println("Users!!!!");
			switchWidget(new UsersList());
		} else if (command.equals("user")) {
			if (params == null) {
				UserTuple userTuple = new UserTuple();
				switchWidget(new UserWidget(userTuple));
			} else {
				try {
					int id = Integer.parseInt(params);
					UserTuple userTuple = UserWidget.findTuple(id);
					if (userTuple == null) {
						// NONE
					} else {
						switchWidget(new UserWidget(userTuple));
					}
				} catch (NumberFormatException ex) {
					// NONE
				}
			}
		}

	}

	@Override
	protected void onDetach() {
		if (autoSyncerObserver != null)
			StaticGWTSyncer.getAutosyncer().removeAutoSyncerObserver(autoSyncerObserver);
		autoSyncerObserver = null;
		super.onDetach();
	}
	@UiHandler("refreshButton")
	void handleClick(ClickEvent e) {
		StaticGWTSyncer.getAutosyncer().syncNow();
	}

}
