package com.szas.server.gwt.client;

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
import com.szas.data.FilledQuestionnaireTuple;
import com.szas.data.QuestionnaireTuple;
import com.szas.sync.local.LocalDAO;
import com.google.gwt.user.client.ui.Label;

public class QuestinnairesWidget extends UniversalWidget<QuestionnaireTuple> {
	
	public static String NAME = "questionnaire";
	
	SimpleTupleList<FilledQuestionnaireTuple> simpleTupleList = 
		new SimpleTupleList<FilledQuestionnaireTuple>() {
			
			@Override
			protected LocalDAO<FilledQuestionnaireTuple> getLocalDAO() {
				return StaticGWTSyncer.getFilledquestionnairedao();
			}
			
			@Override
			protected String getListName() {
				return "filledquestionnaire";
			}
			
			@Override
			protected void addColumns(CellTable<FilledQuestionnaireTuple> cellTable) {
				TextColumn<FilledQuestionnaireTuple> nameColumn;
				nameColumn = new TextColumn<FilledQuestionnaireTuple>() {
					@Override
					public String getValue(FilledQuestionnaireTuple object) {
						return object.getName();
					}
				};
				nameColumn.setSortable(true);
				cellTable.addColumn(nameColumn, "Questionnaire");
			}
		};

	private static QuestinnairesWidgetUiBinder uiBinder = GWT
			.create(QuestinnairesWidgetUiBinder.class);

	interface QuestinnairesWidgetUiBinder extends
			UiBinder<Widget, QuestinnairesWidget> {
	}
	@UiField Button deleteButton;
	@UiField Label questionnaireName;
	@UiField Button editButton;
	
	
	public QuestinnairesWidget(QuestionnaireTuple questionnaireTuple) {
		super(questionnaireTuple);
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
}
