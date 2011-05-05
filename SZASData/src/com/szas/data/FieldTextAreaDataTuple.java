package com.szas.data;

public class FieldTextAreaDataTuple extends FieldTextAreaTuple implements FieldDataTuple {

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
		FieldTextAreaTuple tuple = 
			new FieldTextAreaTuple();
		tuple.setName(name);
		tuple.setValue(value);
		return tuple;
	}
}
