package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.FieldIntegerBoxDataTuple;
import com.szas.server.gwt.client.universalwidgets.FieldDataWidget;

public class FieldIntegerBoxDataWidget extends FieldDataWidget {

	private static FieldIntegerBoxDataWidgetUiBinder uiBinder = GWT
			.create(FieldIntegerBoxDataWidgetUiBinder.class);
	private final FieldIntegerBoxDataTuple field;

	interface FieldIntegerBoxDataWidgetUiBinder extends
			UiBinder<Widget, FieldIntegerBoxDataWidget> {
	}

	public FieldIntegerBoxDataWidget(FieldIntegerBoxDataTuple field) {
		this.field = field;
		initWidget(uiBinder.createAndBindUi(this));
		setStyleWhileIsOk();
	}
	
	@UiField TextBox nameTextBox;
	@UiField TextBox valueTextBox;
	@UiField TextBox minTextBox;
	@UiField TextBox maxTextBox;
	@UiField SimpleCheckBox nullableCheckBox;
	@UiField SimpleCheckBox onListCheckBox;

	@Override
	public void updateField() {
		field.setName(nameTextBox.getText());
		field.setOnList(onListCheckBox.getValue());
		try {
			int value = Integer.parseInt(valueTextBox.getText());
			field.setValue(value);
		} catch (NumberFormatException ex) {
		}
	}

	@Override
	public void updateWidget() {
		nameTextBox.setText(field.getName());
		valueTextBox.setText(Integer.toString(field.getValue()));
		onListCheckBox.setValue(field.isOnList());
	}
	private boolean isValueOk() {
		if (!valueTextBox.getText().equals(""))
			return true;
		try {
			Integer.parseInt(valueTextBox.getText());
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
	
	private boolean isMinOk() {
		try {
			Integer.parseInt(minTextBox.getText());
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
	
	private boolean isMaxOk() {
		try {
			int min = Integer.parseInt(minTextBox.getText());
			int max = Integer.parseInt(maxTextBox.getText());
			if (min >= max) {
				return false;
			}
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
		
	private void setStyleWhileIsOk() {
		if (isMinOk()) {
			minTextBox.removeStyleName("wrong");
		} else {
			minTextBox.addStyleName("wrong");
		}
		if (isValueOk()) {
			valueTextBox.removeStyleName("wrong");
		} else {
			valueTextBox.addStyleName("wrong");
		}
		if (isMaxOk()) {
			maxTextBox.removeStyleName("wrong");
		} else {
			maxTextBox.addStyleName("wrong");
		}
	}

	@UiHandler("valueTextBox")
	void onValueTextBoxChange(ChangeEvent event) {
		setStyleWhileIsOk();
	}
	
	@UiHandler("valueTextBox")
	void onValueTextBoxChange(KeyUpEvent event) {
		setStyleWhileIsOk();
	}
	
	@UiHandler("minTextBox")
	void onMinTextBoxChange(ChangeEvent event) {
		setStyleWhileIsOk();
	}
	
	@UiHandler("minTextBox")
	void onMinTextBoxChange(KeyUpEvent event) {
		setStyleWhileIsOk();
	}
	
	@UiHandler("maxTextBox")
	void onMaxTextBoxChange(ChangeEvent event) {
		setStyleWhileIsOk();
	}
	
	@UiHandler("maxTextBox")
	void onMaxTextBoxChange(KeyUpEvent event) {
		setStyleWhileIsOk();
	}
}
