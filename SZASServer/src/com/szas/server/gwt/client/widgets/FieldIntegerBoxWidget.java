package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.FieldIntegerBoxDataTuple;
import com.szas.data.FieldIntegerBoxTuple;
import com.szas.server.gwt.client.universalwidgets.FieldWidget;

public class FieldIntegerBoxWidget extends FieldWidget {

	private static FieldIntegerBoxWidgetUiBinder uiBinder = GWT
	.create(FieldIntegerBoxWidgetUiBinder.class);
	private final FieldIntegerBoxDataTuple fieldData;
	private final FieldIntegerBoxTuple field;

	interface FieldIntegerBoxWidgetUiBinder extends
	UiBinder<Widget, FieldIntegerBoxWidget> {
	}

	public FieldIntegerBoxWidget(FieldIntegerBoxTuple field, FieldIntegerBoxDataTuple fieldData) {
		this.field = field;
		this.fieldData = fieldData;
		initWidget(uiBinder.createAndBindUi(this));
		setStyleWhileIsOk();
	}

	@UiField TextBox valueTextBox;
	@UiField Label nameLabel;
	@UiField Label rangeLabel;

	@Override
	public void updateField() {
		field.setName(fieldData.getName());
		try {
			int value = Integer.parseInt(valueTextBox.getText());
			field.setValue(value);
		} catch (NumberFormatException ex) {
		}
	}

	@Override
	public void updateWidget() {
		nameLabel.setText(fieldData.getName());
		valueTextBox.setText(Integer.toString(field.getValue()));
		String text = 
			Integer.toString(fieldData.getMin()) +
			"-" +
			Integer.toString(fieldData.getMax());
		rangeLabel.setText(text);
		setStyleWhileIsOk();
	}

	@UiHandler("valueTextBox")
	void onAddItemButtonClick(ChangeEvent event) {
		setStyleWhileIsOk();
	}
	
	private boolean isValueOk() {
		try {
			int value = Integer.parseInt(valueTextBox.getText());
			if (value > fieldData.getMax())
				return false;
			if (value < fieldData.getMin())
				return false;
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	private void setStyleWhileIsOk() {
		if (isValueOk())
			valueTextBox.removeStyleName("wrong");
		else 
			valueTextBox.addStyleName("wrong");
	}
	
	@UiHandler("valueTextBox")
	void onValueTextBoxChange(ChangeEvent event) {
		setStyleWhileIsOk();
	}
	
	@UiHandler("valueTextBox")
	void onValueTextBoxChange(KeyUpEvent event) {
		setStyleWhileIsOk();
	}
}
