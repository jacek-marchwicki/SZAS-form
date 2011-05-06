package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.FieldTextBoxDataTuple;
import com.szas.server.gwt.client.universalwidgets.FieldDataWidget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.SimpleCheckBox;

public class FieldTextBoxDataWidget extends FieldDataWidget {

	private static FieldTextBoxDataWidgetUiBinder uiBinder = GWT
			.create(FieldTextBoxDataWidgetUiBinder.class);
	@UiField TextBox nameTextBox;
	@UiField TextBox valueTextBox;
	@UiField SimpleCheckBox nullableCheckBox;

	interface FieldTextBoxDataWidgetUiBinder extends
			UiBinder<Widget, FieldTextBoxDataWidget> {
	}

	private FieldTextBoxDataTuple field;

	public FieldTextBoxDataWidget(FieldTextBoxDataTuple field) {
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
