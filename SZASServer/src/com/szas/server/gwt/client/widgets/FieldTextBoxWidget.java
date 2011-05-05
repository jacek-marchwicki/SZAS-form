package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.FieldTextBoxDataTuple;
import com.szas.server.gwt.client.universalwidgets.FieldWidget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.SimpleCheckBox;

public class FieldTextBoxWidget extends FieldWidget {

	private static FieldTextBoxWidgetUiBinder uiBinder = GWT
			.create(FieldTextBoxWidgetUiBinder.class);
	@UiField TextBox nameTextBox;
	@UiField TextBox valueTextBox;
	@UiField SimpleCheckBox nullableCheckBox;

	interface FieldTextBoxWidgetUiBinder extends
			UiBinder<Widget, FieldTextBoxWidget> {
	}

	private FieldTextBoxDataTuple field;

	public FieldTextBoxWidget(FieldTextBoxDataTuple field) {
		this.field = field;
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void updateWidget() {
		nameTextBox.setText(field.getName());
		valueTextBox.setText(field.getValue());
		nullableCheckBox.setValue(field.isNullable());
	}

	@Override
	public void updateField() {
		field.setName(nameTextBox.getText());
		field.setValue(valueTextBox.getText());
		field.setNullable(nullableCheckBox.getValue());
	}

}
