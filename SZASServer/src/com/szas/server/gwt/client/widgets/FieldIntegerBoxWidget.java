package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
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
	}
	
	@UiHandler("valueTextBox")
	void onAddItemButtonClick(ChangeEvent event) {
		setStyleWhileIsOk();
	}

	private void setStyleWhileIsOk() {
		try {
			Integer.parseInt(valueTextBox.getText());
			valueTextBox.removeStyleName("wrong");
		} catch (NumberFormatException ex) {
			valueTextBox.addStyleName("wrong");
		}
	}

}
