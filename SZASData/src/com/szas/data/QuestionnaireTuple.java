package com.szas.data;

import java.util.ArrayList;

import com.szas.sync.Tuple;
/**
 * class represent empty questionnaire
 * @author Jacek Marchwicki (jacek@3made.eu)
 *
 */
public class QuestionnaireTuple extends Tuple  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private ArrayList<FieldDataTuple> fields;
	
	/**
	 * create new empty questionnaire
	 */
	public QuestionnaireTuple() {
		fields = new ArrayList<FieldDataTuple>();
		name = "";
	}
	
	/**
	 * Set name of questionnaire
	 * @param name name of questionnaire
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Get name of questionnaire
	 * @return name of questionnaire
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set fields which contain questionnaire
	 * @param fields fields of questionnaire
	 */
	public void setFields(ArrayList<FieldDataTuple> fields) {
		this.fields = fields;
	}
	
	/**
	 * Return set of fields which contain questionnaire
	 * @return set of fields
	 */
	public ArrayList<FieldDataTuple> getFields() {
		return fields;
	}
	
	/**
	 * Return new {@link FilledQuestionnaireTuple} to fill with blank fields
	 * @return new {@link FilledQuestionnaireTuple} with blank fields
	 */
	public FilledQuestionnaireTuple getFilled() {
		FilledQuestionnaireTuple tuple = 
			new FilledQuestionnaireTuple();
		tuple.setName(name);
		ArrayList<FieldTuple> fieldTuples = 
			new ArrayList<FieldTuple>();
		for (FieldDataTuple fieldDataTuple : fields) {
			FieldTuple fieldTuple;
			fieldTuple = fieldDataTuple.getTuple();
			fieldTuples.add(fieldTuple);
		}
		tuple.setFilledFields(fieldTuples);
		return tuple;
	}
}
