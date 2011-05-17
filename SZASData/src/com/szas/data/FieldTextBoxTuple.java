package com.szas.data;

public class FieldTextBoxTuple extends FieldTuple {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String value;
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	@Override
	public String getText() {
		return value;
	}
}
