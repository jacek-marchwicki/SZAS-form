package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.UserTuple;
import com.szas.server.gwt.client.sync.StaticGWTSyncer;
import com.szas.server.gwt.client.universalwidgets.UniversalList;
import com.szas.sync.local.LocalDAO;

public class UsersList extends UniversalList<UserTuple>  {
	
	public static String NAME = "users";

	private static UsersListUiBinder uiBinder =
		GWT.create(UsersListUiBinder.class);
	@UiField(provided=true) CellTable<UserTuple> cellTable = createTable();
	@UiField Button addButton;

	
	@UiHandler("addButton")
	void onAddButtonClick(ClickEvent event) {
		addButtonClicked();
	}

	interface UsersListUiBinder extends UiBinder<Widget, UsersList> {
	}

	public UsersList() {
		initWidget(uiBinder.createAndBindUi(this));
		daoUpdated();
	}

	@Override
	protected LocalDAO<UserTuple> getLocalDAO() {
		return StaticGWTSyncer.getUsersdao();
	}

	@Override
	protected String getListName() {
		return UserWidget.NAME;
	}

	@Override
	protected void addColumns(CellTable<UserTuple> cellTable2) {
		TextColumn<UserTuple> nameColumn;
		nameColumn = new TextColumn<UserTuple>() {
			@Override
			public String getValue(UserTuple userTuple) {
				return userTuple.getName();
			}
		};
		nameColumn.setSortable(true);
		cellTable2.addColumn(nameColumn, "User name");
	}
}
