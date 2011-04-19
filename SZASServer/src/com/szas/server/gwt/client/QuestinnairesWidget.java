package com.szas.server.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.QuestionnaireTuple;
import com.szas.sync.local.LocalDAO;

public class QuestinnairesWidget extends UniversalWidget<QuestionnaireTuple> {

	private static QuestinnairesWidgetUiBinder uiBinder = GWT
			.create(QuestinnairesWidgetUiBinder.class);

	interface QuestinnairesWidgetUiBinder extends
			UiBinder<Widget, QuestinnairesWidget> {
	}

	@UiField Button saveButton;
	@UiField TextBox nameTextBox;
	@UiField Button deleteButton;
	
	
	public QuestinnairesWidget(QuestionnaireTuple questionnaireTuple) {
		super(questionnaireTuple);
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		onSave();
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		onDelete();
	}

	@Override
	protected LocalDAO<QuestionnaireTuple> getLocalDAO() {
		return StaticGWTSyncer.getQuestionnairedao();
	}

	@Override
	protected void updateWidgets() {
		nameTextBox.setText(tuple.getName());
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void updateTuple() {
		tuple.setName(nameTextBox.getText());
	}

	@Override
	protected void setDeleteable(boolean deletable) {
		deleteButton.setVisible(update);
	}

}
