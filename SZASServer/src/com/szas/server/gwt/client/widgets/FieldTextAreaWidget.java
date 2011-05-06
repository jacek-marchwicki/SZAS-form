package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.FieldTextAreaDataTuple;
import com.szas.data.FieldTextAreaTuple;
import com.szas.server.gwt.client.universalwidgets.FieldWidget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextArea;

public class FieldTextAreaWidget extends FieldWidget {

	private static FieldTextAreaWidgetUiBinder uiBinder = GWT
			.create(FieldTextAreaWidgetUiBinder.class);
	@UiField TextArea valueTextArea;
	@UiField Label nameLabel;
	private final FieldTextAreaTuple field;
	private final FieldTextAreaDataTuple fieldData;

	interface FieldTextAreaWidgetUiBinder extends
			UiBinder<Widget, FieldTextAreaWidget> {
	}

	public FieldTextAreaWidget(FieldTextAreaTuple field, FieldTextAreaDataTuple fieldData) {
		this.field = field;
		this.fieldData = fieldData;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void updateField() {
		field.setName(fieldData.getName());
		field.setValue(valueTextArea.getText());
	}

	@Override
	public void updateWidget() {
		nameLabel.setText(fieldData.getName());
		valueTextArea.setText(field.getValue());
	}

}
