package com.szas.data;

import java.util.ArrayList;

import com.szas.sync.Tuple;

public class QuestionnaireTuple extends Tuple  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private ArrayList<FieldDataTuple> fields;
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setFields(ArrayList<FieldDataTuple> fields) {
		this.fields = fields;
	}
	public ArrayList<FieldDataTuple> getFields() {
		return fields;
	}
}
