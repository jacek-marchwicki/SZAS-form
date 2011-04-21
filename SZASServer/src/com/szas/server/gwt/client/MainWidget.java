package com.szas.server.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.QuestionnaireTuple;
import com.szas.data.UserTuple;
import com.szas.server.gwt.client.router.LongRouteAction;
import com.szas.server.gwt.client.router.RouteAction;
import com.szas.server.gwt.client.router.Router;
import com.szas.server.gwt.client.router.RouterImpl;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;

public class MainWidget extends Composite {

	private static MainWidgetUiBinder uiBinder = GWT
	.create(MainWidgetUiBinder.class);

	@UiField SimplePanel simplePanel;
	@UiField Button refreshButton;
	@UiField Label syncStatusLabel;
	private Router<Widget> router = new RouterImpl<Widget>();

	private AutoSyncer.AutoSyncerObserver autoSyncerObserver;

	

	private Widget widget;

	interface MainWidgetUiBinder extends UiBinder<Widget, MainWidget> {
	}
	
	private void addRoutes() {
		RouteAction<Widget> usersRouteAction = new RouteAction<Widget>() {
			@Override
			public Widget run(String command, String params) {
				return new UsersList();
			}
		};
		router.addRoute("users", usersRouteAction);
		router.addRoute("", usersRouteAction);
		router.addRoute("user", new LongRouteAction<Widget>() {
			@Override
			protected Widget run(String command, long id) {
				UserTuple userTuple = StaticGWTSyncer.getUsersdao().getById(id);
				if (userTuple == null)
					return null;
				return new UserWidget(userTuple);
			}
		});
		router.addRoute("user", new RouteAction<Widget>() {
			@Override
			public Widget run(String command, String params) {
				UserTuple userTuple = new UserTuple();
				return new UserWidget(userTuple);
			}
		});
		router.addRoute("questionnaries", new RouteAction<Widget>() {
			@Override
			public Widget run(String command, String params) {
				return new QuestionnariesList();
			}
		});
		router.addRoute("questionnarie", new LongRouteAction<Widget>() {

			@Override
			protected Widget run(String command, long param) {
				QuestionnaireTuple questionnaireTuple =
					StaticGWTSyncer.getQuestionnairedao().getById(param);
				if (questionnaireTuple == null)
					return null;
				return new QuestinnairesWidget(questionnaireTuple);
			}
		});
		router.addRoute("questionnarie", new RouteAction<Widget>() {

			@Override
			public Widget run(String command, String params) {
				QuestionnaireTuple questionnaireTuple =
					new QuestionnaireTuple();
				return new QuestinnairesWidget(questionnaireTuple);
			}
		});
	}

	public MainWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		
		addRoutes();		
		
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
		
		Window.addWindowClosingHandler(new Window.ClosingHandler() {		
			@Override
			public void onWindowClosing(ClosingEvent event) {
				if (StaticGWTSyncer.getAutosyncer().isSynced())
					return;
				event.setMessage("Tere are sync in progress - are you sure to exit?");
			}
		});
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
		Widget newWidget = router.route(historyToken);
		if (newWidget != null) {
			switchWidget(newWidget);
		} else {
			switchWidget(new NotFoundWidget());
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
