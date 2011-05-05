package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class NotFoundWidget extends Composite {

	private static NotFoundWidgetUiBinder uiBinder = GWT
			.create(NotFoundWidgetUiBinder.class);

	interface NotFoundWidgetUiBinder extends UiBinder<Widget, NotFoundWidget> {
	}

	public NotFoundWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
