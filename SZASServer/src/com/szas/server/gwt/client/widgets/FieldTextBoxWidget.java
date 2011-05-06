package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Label;
import com.szas.data.FieldTextBoxDataTuple;
import com.szas.data.FieldTextBoxTuple;
import com.szas.server.gwt.client.universalwidgets.FieldWidget;

public class FieldTextBoxWidget extends FieldWidget {

	private static FieldTextBoxWidgetUiBinder uiBinder = GWT
			.create(FieldTextBoxWidgetUiBinder.class);
	@UiField TextBox valueTextBox;
	@UiField Label nameLabel;
	private final FieldTextBoxTuple field;
	private final FieldTextBoxDataTuple fieldData;

	interface FieldTextBoxWidgetUiBinder extends
			UiBinder<Widget, FieldTextBoxWidget> {
	}

	public FieldTextBoxWidget(FieldTextBoxTuple field, FieldTextBoxDataTuple fieldData) {
		this.field = field;
		this.fieldData = fieldData;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void updateField() {
		field.setName(fieldData.getName());
		field.setValue(valueTextBox.getText());
	}

	@Override
	public void updateWidget() {
		nameLabel.setText(fieldData.getName());
		valueTextBox.setText(field.getValue());
	}

}
