package com.szas.data;

import com.szas.sync.Tuple;

public abstract class FieldTuple extends Tuple {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String name = "";
	protected boolean onList = false;
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public abstract String getText();
	public void setOnList(boolean onList) {
		this.onList = onList;
	}
	public boolean isOnList() {
		return onList;
	}
}
