package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.szas.data.FieldTuple;
import com.szas.server.gwt.client.universalwidgets.FieldWidget;

public class FieldUniversalWidget extends FieldWidget {
	
	@UiField
	Label nameLabel;
	@UiField
	Label valueLabel;

	private static FieldUniversalWidgetUiBinder uiBinder = GWT
			.create(FieldUniversalWidgetUiBinder.class);
	private final FieldTuple tuple;

	interface FieldUniversalWidgetUiBinder extends
			UiBinder<Widget, FieldUniversalWidget> {
	}

	public FieldUniversalWidget(FieldTuple tuple) {
		this.tuple = tuple;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void updateField() {
	}

	@Override
	public void updateWidget() {
		nameLabel.setText(tuple.getName());
		valueLabel.setText(tuple.getText());
	}

}
