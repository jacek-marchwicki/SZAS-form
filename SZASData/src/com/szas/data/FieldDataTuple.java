package com.szas.data;

public interface FieldDataTuple {
	public long getId();
	
	/**
	 * @see FieldTuple#getName()
	 * @param name
	 */
	public String getName();
	
	/**
	 * @see FieldTuple#setName()
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * create FieldTuple of that Type
	 * @return new FieldTuple
	 */
	public FieldTuple getTuple();
	
	/**
	 * set when field should be displayed on the list
	 * @param onList
	 */
	public void setOnList(boolean onList);
	
	/**
	 * is field displayed on the list
	 * @return
	 */
	public boolean isOnList();
}
