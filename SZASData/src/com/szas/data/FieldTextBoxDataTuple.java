package com.szas.data;

public class FieldTextBoxDataTuple extends FieldTextBoxTuple implements FieldDataTuple {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean nullable;
	private boolean onList;
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	public boolean isNullable() {
		return nullable;
	}
	@Override
	public FieldTuple getTuple() {
		FieldTextBoxTuple tuple = 
			new FieldTextBoxTuple();
		tuple.setName(name);
		tuple.setValue(value);
		return tuple;
	}
	@Override
	public void setOnList(boolean onList) {
		this.onList = onList;
	}
	@Override
	public boolean isOnList() {
		return onList;
	}
}
