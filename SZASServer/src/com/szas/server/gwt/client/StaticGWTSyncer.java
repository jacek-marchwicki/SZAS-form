package com.szas.server.gwt.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.szas.data.FilledQuestionnaireTuple;
import com.szas.data.QuestionnaireTuple;
import com.szas.data.UserTuple;
import com.szas.sync.SyncedElementsHolder;
import com.szas.sync.ToSyncElementsHolder;
import com.szas.sync.WrongObjectThrowable;
import com.szas.sync.local.LocalDAO;
import com.szas.sync.local.LocalDAOImpl;
import com.szas.sync.local.LocalSyncHelper;
import com.szas.sync.local.LocalSyncHelperImpl;
import com.szas.sync.local.SyncLocalService;
import com.szas.sync.local.SyncLocalServiceResult;

public final class StaticGWTSyncer {
	private final static class GWTSyncLocalService implements SyncLocalService {

		@Override
		public void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
				final SyncLocalServiceResult callback) {
			syncingService.sync(toSyncElementsHolders, new AsyncCallback<ArrayList<SyncedElementsHolder>>() {
				
				@Override
				public void onSuccess(ArrayList<SyncedElementsHolder> result) {
					if (result == null)
					{
						callback.onFailure(new WrongObjectThrowable());
						return;
					}
					callback.onSuccess(result);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
			});
		}
		
	}
	private final static GWTSyncLocalService syncLocalService;
	private final static LocalSyncHelper syncHelper;
	
	private final static LocalDAO<UserTuple> usersDAO;
	private final static LocalDAO<QuestionnaireTuple> questionnaireDAO;
	private final static LocalDAO<FilledQuestionnaireTuple> filledQuestionnaireDAO;
	
	private final static AutoSyncer autoSyncer;
	private final static SyncingServiceAsync syncingService = GWT
	.create(SyncingService.class);
	
	static {
		syncLocalService = new GWTSyncLocalService();
		syncHelper = new LocalSyncHelperImpl(syncLocalService);
		
		usersDAO = new LocalDAOImpl<UserTuple>();
		questionnaireDAO = new LocalDAOImpl<QuestionnaireTuple>();
		filledQuestionnaireDAO = new LocalDAOImpl<FilledQuestionnaireTuple>();
		
		syncHelper.append("users", usersDAO);
		syncHelper.append("questionnaire", getQuestionnairedao());
		syncHelper.append("filled", getFilledquestionnairedao());
		
		autoSyncer = new AutoSyncer(syncHelper);
		getAutosyncer().addWatcher(usersDAO);
		getAutosyncer().syncNow();
	}
	
	public static LocalDAO<UserTuple> getUsersdao() {
		return usersDAO;
	}

	public static AutoSyncer getAutosyncer() {
		return autoSyncer;
	}

	public static LocalDAO<QuestionnaireTuple> getQuestionnairedao() {
		return questionnaireDAO;
	}

	public static LocalDAO<FilledQuestionnaireTuple> getFilledquestionnairedao() {
		return filledQuestionnaireDAO;
	}
}
