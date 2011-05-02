package com.szas.server.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.FieldTextAreaDataTuple;

public class FieldTextAreaWidget extends FieldWidget {

	private static FieldTextAreaWidgetUiBinder uiBinder = GWT
			.create(FieldTextAreaWidgetUiBinder.class);
	
	@UiField
	TextBox nameTextBox;
	@UiField
	TextArea valueTextArea;
	@UiField
	SimpleCheckBox nullableCheckBox;
	
	private FieldTextAreaDataTuple field;

	interface FieldTextAreaWidgetUiBinder extends
			UiBinder<Widget, FieldTextAreaWidget> {
	}

	public FieldTextAreaWidget(FieldTextAreaDataTuple field) {
		this.field = field;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void updateField() {
		field.setName(nameTextBox.getText());
		field.setNullable(nullableCheckBox.getValue());
		field.setValue(valueTextArea.getText());
	}

	@Override
	public void updateWidget() {
		nameTextBox.setText(field.getName());
		nullableCheckBox.setValue(field.isNullable());
		valueTextArea.setText(field.getValue());
	}

}
