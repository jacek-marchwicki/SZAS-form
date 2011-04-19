package com.szas.server.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.QuestionnaireTuple;
import com.szas.sync.local.LocalDAO;

public class QuestionnariesList extends UniversalList<QuestionnaireTuple> {
	
	@UiField(provided=true) CellTable<QuestionnaireTuple> cellTable = createTable();
	@UiField Button addButton;
	
	@UiHandler("addButton")
	void onAddButtonClick(ClickEvent event) {
		addButtonClicked();
	}

	private static QuestionnariesListUiBinder uiBinder = GWT
			.create(QuestionnariesListUiBinder.class);

	interface QuestionnariesListUiBinder extends
			UiBinder<Widget, QuestionnariesList> {
	}

	public QuestionnariesList() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected LocalDAO<QuestionnaireTuple> getLocalDAO() {
		return StaticGWTSyncer.getQuestionnairedao();
	}

	@Override
	protected String getListName() {
		return "questionnarie";
	}

	@Override
	protected void addColumns(CellTable<QuestionnaireTuple> cellTable2) {
		TextColumn<QuestionnaireTuple> nameColumn;
		nameColumn = new TextColumn<QuestionnaireTuple>() {
			@Override
			public String getValue(QuestionnaireTuple object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);
		cellTable2.addColumn(nameColumn, "Questionnaire");
	}

}
