package com.szas.data;

import com.szas.sync.Tuple;

public abstract class FieldTuple extends Tuple {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String name = "";
	
	/**
	 * Set name of field
	 * this name should be a question
	 * @param name name of field
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Get name of field
	 * this is a question to answer
	 * @return name of field
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * get shortened message to display on list
	 * @return shortened message
	 */
	public abstract String getText();
	

	/**
	 * return field value converted to string
	 * @return field value converted to string
	 */
	public abstract String toString();
}
