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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.QuestionnaireTuple;
import com.szas.server.gwt.client.sync.StaticGWTSyncer;
import com.szas.server.gwt.client.universalwidgets.SimpleTupleList;
import com.szas.sync.local.LocalDAO;

public class QuestionnairesList extends Composite {
	
	public static String NAME = "questionnaires";
	
	SimpleTupleList<QuestionnaireTuple> simpleTupleList= new SimpleTupleList<QuestionnaireTuple>() {

		@Override
		protected void addColumns(CellTable<QuestionnaireTuple> cellTable) {
			TextColumn<QuestionnaireTuple> nameColumn;
			nameColumn = new TextColumn<QuestionnaireTuple>() {
				@Override
				public String getValue(QuestionnaireTuple object) {
					return object.getName();
				}
			};
			nameColumn.setSortable(true);
			cellTable.addColumn(nameColumn, "Questionnaire");
		}

		@Override
		protected LocalDAO<QuestionnaireTuple> getLocalDAO() {
			return StaticGWTSyncer.getQuestionnairedao();
		}

		@Override
		protected String getListName() {
			return QuestionnairesWidget.NAME;
		}
	};
	
	@UiField(provided=true)
	CellTable<QuestionnaireTuple> cellTable = simpleTupleList;
	
	@UiField Button addButton;
	
	@UiHandler("addButton")
	void onAddButtonClick(ClickEvent event) {
		History.newItem(EditQuestionnaireWidget.NAME,true);
	}

	private static QuestionnairesListUiBinder uiBinder = GWT
			.create(QuestionnairesListUiBinder.class);

	interface QuestionnairesListUiBinder extends
			UiBinder<Widget, QuestionnairesList> {
	}

	public QuestionnairesList() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
