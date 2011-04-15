package com.szas.server.gwt.client;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.widgetideas.client.GlassPanel;
import com.szas.data.UserTuple;
import com.szas.sync.ContentObserver;
import com.szas.sync.local.SyncObserver;

public class UsersManager implements EntryPoint {

	private Button refreshButton;
	private CellTable<UserTuple> table;
	private Button editButton;
	private SingleSelectionModel<UserTuple> selectionModel;
	private Button deleteButton;
	private Button newButton;
	private List<UserTuple> list;
	private TextColumn<UserTuple> nameColumn;

	private static class UserTupleKeyProvider implements ProvidesKey<UserTuple> {

		@Override
		public Object getKey(UserTuple item) {
			return (item == null) ? null : item.getId();
		}

	}

	@Override
	public void onModuleLoad() {
		VerticalPanel mainPanel = new VerticalPanel();
		RootPanel.get("sendButtonContainer").add(mainPanel);

		HorizontalPanel menuPanel = new HorizontalPanel();
		mainPanel.add(menuPanel);

		refreshButton = new Button("Refresh");
		menuPanel.add(refreshButton);
		refreshButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				StaticGWTSyncer.getSynchelper().sync();
			}
		});
		newButton = new Button("New");
		menuPanel.add(newButton);
		newButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				newUser();
			}
		});

		editButton = new Button("Edit");
		menuPanel.add(editButton);
		editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				editUser();
			}
		});
		deleteButton = new Button("Delete");
		menuPanel.add(deleteButton);
		deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				deleteUser();
			}
		});

		//UserTupleCell userTupleCell = new UserTupleCell();
		ScrollPanel scrollPanel = new ScrollPanel();
		mainPanel.add(scrollPanel);
		scrollPanel.setHeight("200px");
		scrollPanel.setWidth("320px");
		
		UserTupleKeyProvider userTupleKeyProvider = new UserTupleKeyProvider();
		table = new CellTable<UserTuple>(userTupleKeyProvider);
		table.setWidth("300px");
		scrollPanel.add(table);

		selectionModel = new SingleSelectionModel<UserTuple>(
				userTupleKeyProvider);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				changeSellection();
			}
		});
		table.setSelectionModel(selectionModel);	
		changeSellection();

		nameColumn = new TextColumn<UserTuple>() {
			@Override
			public String getValue(UserTuple userTuple) {
				return userTuple.getName();
			}
		};
		nameColumn.setSortable(true);
		table.addColumn(nameColumn, "User name");

		ListDataProvider<UserTuple> dataProvider = new ListDataProvider<UserTuple>();
		dataProvider.addDataDisplay(table);

		list = dataProvider.getList();

		ListHandler<UserTuple> columnSortHandler = new ListHandler<UserTuple>(
				list);
		columnSortHandler.setComparator(nameColumn,
				new Comparator<UserTuple>() {
			public int compare(UserTuple o1, UserTuple o2) {
				if (o1 == o2) {
					return 0;
				}

				// Compare the name columns.
				if (o1 != null) {
					return (o2 != null) ? o1.getName().compareTo(o2.getName()) : 1;
				}
				return -1;
			}
		});
		table.addColumnSortHandler(columnSortHandler);


		StaticGWTSyncer.getUsersdao().addContentObserver(new ContentObserver() {

			@Override
			public void onChange() {
				usersUpdated();
			}
		});
		StaticGWTSyncer.getSynchelper().addSyncObserver(new SyncObserver() {

			@Override
			public void onSucces() {
				activateButton();
			}

			@Override
			public void onStart() {
				deactivateButton();
			}

			@Override
			public void onFail(Throwable caught) {
				activateButton();
			}
		});
		StaticGWTSyncer.getSynchelper().sync();
	}

	protected void newUser() {
		UserTuple userTuple = new UserTuple();
		MyDialog myDialog = new MyDialog(userTuple,false);
		myDialog.show();
	}

	protected void deleteUser() {
		UserTuple userTuple = selectionModel.getSelectedObject();
		StaticGWTSyncer.getUsersdao().delete(userTuple);
	}

	protected void editUser() {
		UserTuple userTuple = selectionModel.getSelectedObject();
		if (userTuple == null)
			return;
		MyDialog myDialog = new MyDialog(userTuple,true);
		myDialog.show();
	}

	protected void changeSellection() {
		boolean selected = selectionModel.getSelectedObject() != null;
		editButton.setEnabled(selected);
		deleteButton.setEnabled(selected);
	}

	private void usersUpdated() {
		Collection<UserTuple> users = StaticGWTSyncer.getUsersdao().getAll();
		table.setRowCount(users.size(), true);
		while (list.size() != 0)
			list.remove(0);
		for (UserTuple user : users) {
			list.add(user);
		}
	}

	protected void deactivateButton() {
		refreshButton.setEnabled(false);
	}

	protected void activateButton() {
		refreshButton.setEnabled(true);
	}

	private static class MyDialog extends DialogBox {

		private TextBox textBox;
		private UserTuple user;
		private GlassPanel glassPanel;
		private Boolean update;

		public MyDialog(UserTuple user,Boolean update) {
			this.update = update;
			this.glassPanel = new GlassPanel(false);
			this.user = user;
			setText("Edit element");

			textBox = new TextBox();
			textBox.setText(user.getName());
			Button saveButton = new Button("Save");
			Button cancelButton = new Button("Cancel");
			VerticalPanel verticalPanel = new VerticalPanel();
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			horizontalPanel.add(saveButton);
			horizontalPanel.add(cancelButton);
			verticalPanel.add(textBox);
			verticalPanel.add(horizontalPanel);
			saveButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					save();
				}
			});
			cancelButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					cancel();
				}
			});
			setWidget(verticalPanel);
			center();
		}

		@Override
		public void show() {
			DOM.setStyleAttribute(glassPanel.getElement(), "zIndex", "1");
			RootPanel.get().add(glassPanel, 0, 0);
			DOM.setStyleAttribute(this.getElement(), "zIndex", "2");
			super.show();
		}

		@Override
		public void hide() {
			super.hide();
			RootPanel.get().remove(glassPanel);
		}

		protected void cancel() {
			hide();
		}

		protected void save() {
			user.setName(textBox.getText());
			if (update)
				StaticGWTSyncer.getUsersdao().update(user);
			else
				StaticGWTSyncer.getUsersdao().insert(user);
			hide();
		}
	}
}
