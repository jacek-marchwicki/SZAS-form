package com.szas.data;

import java.util.ArrayList;

import com.szas.sync.Tuple;

/**
 * Represent of filled questionnaire
 * instance of this class should be created by {@link QuestionnaireTuple#getFilled()}
 * @author Jacek Marchwicki (jacek@3made.eu)
 */
public class FilledQuestionnaireTuple extends Tuple {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private ArrayList<FieldTuple> filledFields;
	
	/**
	 * Name of filled questionnaire
	 * name schould be equal with {@link QuestionnaireTuple}
	 * @param name name of questionnaire
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Return name of filled questionnaire
	 * @return name of filled questionnaire
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set new set of filled fields ({@link FieldTuple} to {@link FilledQuestionnaireTuple}
	 * @param filledFieldset of filled fields ({@link FieldTuple}
	 */
	public void setFilledFields(ArrayList<FieldTuple> filledFields) {
		this.filledFields = filledFields;
	}
	
	/**
	 * Get array of filled fields ({@link FieldTuple} from {@link FilledQuestionnaireTuple}
	 * @param filledFields
	 */
	public ArrayList<FieldTuple> getFilledFields() {
		return filledFields;
	} 

}
