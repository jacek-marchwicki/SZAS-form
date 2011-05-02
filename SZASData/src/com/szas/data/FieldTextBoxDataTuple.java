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
}
