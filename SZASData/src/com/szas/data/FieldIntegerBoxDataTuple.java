package com.szas.data;

public class FieldIntegerBoxDataTuple extends FieldIntegerBoxTuple implements
		FieldDataTuple {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int min;
	private int max;

	private boolean onList;
	public void setMin(int min) {
		this.min = min;
	}
	public int getMin() {
		return min;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getMax() {
		return max;
	}
	@Override
	public FieldTuple getTuple() {
		FieldIntegerBoxTuple tuple = 
			new FieldIntegerBoxTuple();
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
