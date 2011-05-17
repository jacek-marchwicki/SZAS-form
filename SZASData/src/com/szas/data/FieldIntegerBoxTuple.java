package com.szas.data;

public class FieldIntegerBoxTuple extends FieldTuple {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int value;
	public void setValue(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	@Override
	public String getText() {
		return Integer.toString(value);
	}

}
