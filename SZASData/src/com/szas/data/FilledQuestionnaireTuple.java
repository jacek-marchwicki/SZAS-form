package com.szas.data;

import java.util.ArrayList;

import com.szas.sync.Tuple;

public class FilledQuestionnaireTuple extends Tuple {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private ArrayList<FieldTuple> filledFields;
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setFilledFields(ArrayList<FieldTuple> filledFields) {
		this.filledFields = filledFields;
	}
	public ArrayList<FieldTuple> getFilledFields() {
		return filledFields;
	} 

}
