package com.szas.android.SZASApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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
		private static RefreshSyncReceiver refreshSyncReceiver = new RefreshSyncReceiver();
		private static IntentFilter intentFilter;
		private static Context context;
		
		public static void loadContext(Context context){
			LocalDAOContener.context = context;
			questionnaireDAO = new SQLLocalDAO<QuestionnaireTuple>(context, "com.szas.data.QuestionnaireTuple");
			filledQuestionnaireDAO = new SQLLocalDAO<FilledQuestionnaireTuple>(context, "com.szas.data.FilledQuestionnaireTuple");
			refreshQuestionnaireTuples();
			new Thread(new Runnable() {
				@Override
				public void run() {
					refreshFilledQuestionnaireTuples();		
				}
			}).start();
			intentFilter = new IntentFilter();
			intentFilter.addAction(Constans.broadcastMessage);
			LocalDAOContener.context.registerReceiver(refreshSyncReceiver, intentFilter);
		}
		
		private static Collection<QuestionnaireTuple> copyOfQuestionnaireTuples;
		public static void refreshQuestionnaireTuples(){
			if(questionnaireTuples != null){
				copyOfQuestionnaireTuples = new ArrayList<QuestionnaireTuple>();
				copyOfQuestionnaireTuples.addAll(questionnaireTuples);
				setQuestionnaireTuples(getAllQuestionnaireTuples());
			if(!questionnaireTuples.equals(copyOfQuestionnaireTuples))
				sendMessage(Constans.changesQuestionnaireMessage, "");
			}
			else
				setQuestionnaireTuples(getAllQuestionnaireTuples());
		}
		
		public static void sendMessage(String action, String information){
			Intent i = new Intent(action);
			i.putExtra("info", information);
			LocalDAOContener.context.sendBroadcast(i);
		}
		
		private static Collection<FilledQuestionnaireTuple> copyOfFilledQuestionnaireTuples;
		public static void refreshFilledQuestionnaireTuples(){
			if(filledQuestionnaireTuples != null){
				copyOfFilledQuestionnaireTuples = new ArrayList<FilledQuestionnaireTuple>();
				copyOfFilledQuestionnaireTuples.addAll(filledQuestionnaireTuples);
				setFilledQuestionnaireTuples(getAllFilledQuestionnaireTuples());
				if(!filledQuestionnaireTuples.equals(copyOfFilledQuestionnaireTuples))
					sendMessage(Constans.changesFilledMessage, "filled");
			}
			else
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
			if(filledQuestionnaireTuples == null || filledQuestionnaireTuples.isEmpty())
				refreshFilledQuestionnaireTuples();
			for(FilledQuestionnaireTuple filledQuestionnaireTuple : filledQuestionnaireTuples){
				if(filledQuestionnaireTuple.getName().equals(name))
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
		
		public static void deleteFilledQuestionnaireTuple(FilledQuestionnaireTuple filledQuestionnaireTuple){
			filledQuestionnaireDAO.delete(filledQuestionnaireTuple);
		}
		
		public static void insertUpdateFilledQuestionnaireTuple(FilledQuestionnaireTuple filledQuestionnaireTuple){
			if(getFilledQuestionnaireTupleById(filledQuestionnaireTuple.getId()) != null)
				updateFilledQuestionnaireTuple(filledQuestionnaireTuple);
			else
				insertFilledQuestionnaireTuple(filledQuestionnaireTuple);
		}
		
		private static class RefreshSyncReceiver extends BroadcastReceiver {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.content.BroadcastReceiver#onReceive(android.content.Context,
			 * android.content.Intent)
			 */
			@Override
			public void onReceive(Context context, Intent intent) {
				String info = intent.getStringExtra("info");
				if (info != null) {
					refreshQuestionnaireTuples();
					refreshFilledQuestionnaireTuples();
				}
			}

		}
	}
	
}
