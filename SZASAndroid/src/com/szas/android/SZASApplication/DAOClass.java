/**
 * 
 */
package com.szas.android.SZASApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.content.Context;

import com.szas.data.FilledQuestionnaireTuple;
import com.szas.data.QuestionnaireTuple;
import com.szas.sync.Tuple;
import com.szas.sync.local.LocalDAO;

/**
 * @author pszafer@gmail.com
 *
 */
public class DAOClass<T extends Tuple> {

	public static class LocalDAOContener{
		private static LocalDAO<QuestionnaireTuple> questionnaireDAO;
		private static LocalDAO<FilledQuestionnaireTuple> filledQuestionnaireDAO;
		private static Collection<QuestionnaireTuple> questionnaireTuples;
		private static Collection<FilledQuestionnaireTuple> filledQuestionnaireTuples;
		
		public static void loadContext(Context context){
			questionnaireDAO = new SQLLocalDAO<QuestionnaireTuple>(context, "com.szas.data.QuestionnaireTuple");
			filledQuestionnaireDAO = new SQLLocalDAO<FilledQuestionnaireTuple>(context, "com.szas.data.FilledQuestionnaireTuple");
			refreshQuestionnaireTuples();
			refreshFilledQuestionnaireTuples();
		}
		
		public static void refreshQuestionnaireTuples(){
			setQuestionnaireTuples(getAllQuestionnaireTuples());
		}
		
		public static void refreshFilledQuestionnaireTuples(){
			setFilledQuestionnaireTuples(getAllFilledQuestionnaireTuples());
		}
		
		private static Collection<QuestionnaireTuple> getAllQuestionnaireTuples(){
			return questionnaireDAO.getAll();
		}
		
		private static Collection<FilledQuestionnaireTuple> getAllFilledQuestionnaireTuples(){
			return filledQuestionnaireDAO.getAll();
		}
		/**
		 * @param questionnaireTuples the questionnaireTuples to set
		 */
		public static void setQuestionnaireTuples(Collection<QuestionnaireTuple> questionnaireTuples) {
			LocalDAOContener.questionnaireTuples = questionnaireTuples;
		}

		/**
		 * @return the questionnaireTuples
		 */
		public static Collection<QuestionnaireTuple> getQuestionnaireTuples() {
			return questionnaireTuples;
		}

		/**
		 * @param filledQuestionnaireTuples the filledQuestionnaireTuples to set
		 */
		public static void setFilledQuestionnaireTuples(
				Collection<FilledQuestionnaireTuple> filledQuestionnaireTuples) {
			LocalDAOContener.filledQuestionnaireTuples = filledQuestionnaireTuples;
		}

		/**
		 * @return the filledQuestionnaireTuples
		 */
		public static Collection<FilledQuestionnaireTuple> getFilledQuestionnaireTuples() {
			return filledQuestionnaireTuples;
		}
		
		public static Collection<QuestionnaireTuple> getQuestionnaireTuplesByName(String name){
			Collection<QuestionnaireTuple> collection = new ArrayList<QuestionnaireTuple>();
			for(QuestionnaireTuple questionnaireTuple : questionnaireTuples){
				if( questionnaireTuple.getName().equals(name))
						collection.add(questionnaireTuple);
			}
			return collection;
		}
		
		public static Collection<FilledQuestionnaireTuple> getFilledQuestionnaireTupleByName(String name){
			Collection<FilledQuestionnaireTuple> collection = new ArrayList<FilledQuestionnaireTuple>();
			for(FilledQuestionnaireTuple filledQuestionnaireTuple : filledQuestionnaireTuples){
				if( filledQuestionnaireTuple.getName().equals(name))
						collection.add(filledQuestionnaireTuple);
			}
			return collection;
		}
		
		public static HashMap<Long, Object> getTuplesByName(String name){
			HashMap<Long, Object> allElements = new HashMap<Long, Object>();
			Collection<QuestionnaireTuple> questionnaireTuples = getQuestionnaireTuplesByName(name);
			Collection<FilledQuestionnaireTuple> filledQuestionnaireTuples = getFilledQuestionnaireTupleByName(name);
			for(QuestionnaireTuple questionnaireTuple : questionnaireTuples)
				allElements.put(questionnaireTuple.getId(), questionnaireTuple.getName());
			for(FilledQuestionnaireTuple filledQuestionnaireTuple : filledQuestionnaireTuples)
				allElements.put(filledQuestionnaireTuple.getId(), filledQuestionnaireTuple.getName());
			return allElements;
		}
		
		public static QuestionnaireTuple getQuestionnaireTupleById(long id){
			return questionnaireDAO.getById(id);
		}
		
		public static FilledQuestionnaireTuple getFilledQuestionnaireTupleById(long id){
			return filledQuestionnaireDAO.getById(id);
		}
		
		public static void insertFilledQuestionnaireTuple(FilledQuestionnaireTuple filledQuestionnaireTuple){
			filledQuestionnaireDAO.insert(filledQuestionnaireTuple);
		}
		
		public static void updateFilledQuestionnaireTuple(FilledQuestionnaireTuple filledQuestionnaireTuple){
			filledQuestionnaireDAO.update(filledQuestionnaireTuple);
		}
	}
	
}
