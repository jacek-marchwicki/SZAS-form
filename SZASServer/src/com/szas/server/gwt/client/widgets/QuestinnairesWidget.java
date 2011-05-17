package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.FieldTuple;
import com.szas.data.FilledQuestionnaireTuple;
import com.szas.data.QuestionnaireTuple;
import com.szas.server.gwt.client.sync.StaticGWTSyncer;
import com.szas.server.gwt.client.universalwidgets.SimpleTupleList;
import com.szas.server.gwt.client.universalwidgets.UniversalWidget;
import com.szas.sync.local.LocalDAO;
import com.google.gwt.user.client.ui.Label;

public class QuestinnairesWidget extends UniversalWidget<QuestionnaireTuple> {

	public static String NAME = "questionnaire";

	static class MyTupleList extends SimpleTupleList<FilledQuestionnaireTuple> {

		private final QuestionnaireTuple questionnaireTuple;

		public MyTupleList(QuestionnaireTuple questionnaireTuple) {
			this.questionnaireTuple = questionnaireTuple;
		}

		@Override
		protected LocalDAO<FilledQuestionnaireTuple> getLocalDAO() {
			return StaticGWTSyncer.getFilledquestionnairedao();
		}

		@Override
		protected String getListName() {
			return FilledQuestionnaireWidget.NAME;
		}

		@Override
		protected boolean filter(FilledQuestionnaireTuple filledTuple) {
			if (filledTuple.getName().equals(questionnaireTuple.getName()))
				return true;
			return false;
		}

		@Override
		protected void addColumns(CellTable<FilledQuestionnaireTuple> cellTable) {
			TextColumn<FilledQuestionnaireTuple> nameColumn;
			nameColumn = new TextColumn<FilledQuestionnaireTuple>() {
				@Override
				public String getValue(FilledQuestionnaireTuple object) {
					String value = "";
					for (FieldTuple fieldTuple : object.getFilledFields()) {
						if (!fieldTuple.isOnList())
							continue;
						value += fieldTuple.getText() + " ";
					}
					if (value.equals("")) {
						value = object.getName();
					}
					return value;
				}
			};
			nameColumn.setSortable(true);
			cellTable.addColumn(nameColumn, "Filled");
		}
	};


	@UiField(provided=true)
	MyTupleList cellTable;
	@UiField Button deleteButton;
	@UiField Label questionnaireName;
	@UiField Button editButton;
	@UiField Button fillButton;

	private static QuestinnairesWidgetUiBinder uiBinder = GWT
	.create(QuestinnairesWidgetUiBinder.class);

	interface QuestinnairesWidgetUiBinder extends
	UiBinder<Widget, QuestinnairesWidget> {
	}


	public QuestinnairesWidget(QuestionnaireTuple questionnaireTuple) {
		super(questionnaireTuple);
		cellTable = new MyTupleList(tuple);
		initWidget();
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
		questionnaireName.setText(tuple.getName());
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void updateTuple() {
		tuple.setName(questionnaireName.getText());
	}

	@Override
	protected void setDeleteable(boolean deletable) {
		deleteButton.setVisible(update);
	}

	@UiHandler("editButton")
	void onEditButtonClick(ClickEvent event) {
		History.newItem(EditQuesionnaireWidget.NAME+"," + tuple.getId(),true);
	}

	@UiHandler("fillButton")
	void onFillButtonClick(ClickEvent event) {
		History.newItem(FilledQuestionnaireWidget.NAME_NEW + "," + tuple.getId(),true);
	}
}
