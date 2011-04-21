package com.szas.server;

import com.szas.data.FilledQuestionnaireTuple;
import com.szas.data.QuestionnaireTuple;
import com.szas.data.UserTuple;
import com.szas.sync.remote.RemoteDAO;
import com.szas.sync.remote.RemoteSyncHelper;
import com.szas.sync.remote.RemoteSyncHelperImpl;

public final class StaticSyncer {
	private final static RemoteSyncHelper syncHelper;
	private final static RemoteDAO<UserTuple> usersDAO;
	private final static RemoteDAO<QuestionnaireTuple> questionnaireDAO;
	private final static RemoteDAO<FilledQuestionnaireTuple> filledQuestionnaireDAO;
	static {
		syncHelper = new RemoteSyncHelperImpl();
		
		usersDAO = new PersistentRemoteDAO<UserTuple>(UserTuple.class);
		questionnaireDAO = new PersistentRemoteDAO<QuestionnaireTuple>(QuestionnaireTuple.class);
		filledQuestionnaireDAO = new PersistentRemoteDAO<FilledQuestionnaireTuple>(FilledQuestionnaireTuple.class);
		
		syncHelper.append("users", usersDAO);
		syncHelper.append("questionnaire", questionnaireDAO);
		syncHelper.append("filled", filledQuestionnaireDAO);
	}
	public static RemoteSyncHelper getSyncHelper() {
		return syncHelper;
	}
	public static RemoteDAO<UserTuple> getUsersDAO() {
		return usersDAO;
	}
}
