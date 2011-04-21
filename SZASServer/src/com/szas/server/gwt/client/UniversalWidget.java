package com.szas.server.gwt.client;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.szas.sync.Tuple;
import com.szas.sync.local.LocalDAO;

public abstract class UniversalWidget<T extends Tuple> extends Composite {
	protected T tuple;
	protected boolean update;
	
	protected abstract LocalDAO<T> getLocalDAO();
	protected abstract void updateWidgets();
	protected abstract void initWidget();
	protected abstract void updateTuple();
	protected abstract void setDeleteable(boolean deletable);
	
	public UniversalWidget(T tuple) {
		this.tuple = tuple;
		update = getLocalDAO().getById(tuple.getId()) != null;
		initWidget();
		setDeleteable(update);
		updateWidgets();
	}
	
	protected void onSave() {
		updateTuple();
		if (update)
			getLocalDAO().update(tuple);
		else
			getLocalDAO().insert(tuple);
		History.back();
	}
	
	protected void onDelete() {
		if (!update)
			return;
		getLocalDAO().delete(tuple);
		History.back();
	}

}
