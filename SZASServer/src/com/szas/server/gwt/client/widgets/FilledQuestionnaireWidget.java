package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.szas.data.FilledQuestionnaireTuple;
import com.szas.server.gwt.client.sync.StaticGWTSyncer;
import com.szas.server.gwt.client.universalwidgets.UniversalWidget;
import com.szas.sync.local.LocalDAO;

public class FilledQuestionnaireWidget extends UniversalWidget<FilledQuestionnaireTuple> {

	public FilledQuestionnaireWidget(FilledQuestionnaireTuple tuple) {
		super(tuple);
	}

	public static final String NAME = "filledQuestionnaire";
	public static final String NAME_NEW = "newFilledQuestionnaire";
	private static FilledQuestionnaireWidgetUiBinder uiBinder = GWT
			.create(FilledQuestionnaireWidgetUiBinder.class);
	@UiField Label questionnaireName;
	@UiField Button deleteButton;

	interface FilledQuestionnaireWidgetUiBinder extends
			UiBinder<Widget, FilledQuestionnaireWidget> {
	}

	@Override
	protected void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected LocalDAO<FilledQuestionnaireTuple> getLocalDAO() {
		return StaticGWTSyncer.getFilledquestionnairedao();
	}

	@Override
	protected void updateWidgets() {
		questionnaireName.setText(tuple.getName());
		// TODO Auto-generated method stub
	}

	@Override
	protected void updateTuple() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void setDeleteable(boolean deletable) {
		deleteButton.setVisible(deletable);
	}


}
