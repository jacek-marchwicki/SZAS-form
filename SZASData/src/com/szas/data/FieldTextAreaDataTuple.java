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
	/**
	 * This method do nothing because {@link FieldTextAreaTuple} could not be on the list
	 * @see FieldDataTuple#setOnList(boolean)
	 */
	@Override
	public void setOnList(boolean onList) {
		// FieldTextAreaTuple could not be on the list
	}
	/**
	 * This method always return false because {@link FieldTextAreaDataTuple} could not be on the list
	 * @see FieldDataTuple#isOnList()
	 * @return false
	 */
	@Override
	public boolean isOnList() {
		return false;
	}
}
