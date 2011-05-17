package com.szas.data;

public class FieldTextBoxDataTuple extends FieldTextBoxTuple implements FieldDataTuple {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean nullable;
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
		tuple.setOnList(onList);
		return tuple;
	}
}
