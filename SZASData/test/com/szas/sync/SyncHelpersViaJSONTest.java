package com.szas.sync;

import java.util.ArrayList;

import com.szas.sync.local.SyncLocalService;
import com.szas.sync.local.SyncLocalServiceResult;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class SyncHelpersViaJSONTest extends SyncHelpersTest {
	@Override
	protected SyncLocalService getSyncLocalService() {
		return new SyncLocalService() {
			
			@Override
			public void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
					SyncLocalServiceResult callback) {
				
				// CLient side
				String toServer;
				toServer = new JSONSerializer().include("*").serialize(toSyncElementsHolders);
				
				try {
					// Server side
					ArrayList<ToSyncElementsHolder> server_sToSyncElementsHolders =
						new JSONDeserializer<ArrayList<ToSyncElementsHolder>>().deserialize(toServer);
					ArrayList<SyncedElementsHolder> server_sResult =
						remoteSyncHelper.sync(server_sToSyncElementsHolders);
					String toClient = 
						new JSONSerializer().include("*").serialize(server_sResult);	
					
					// Client side
					ArrayList<SyncedElementsHolder> result =
						new JSONDeserializer<ArrayList<SyncedElementsHolder>>().deserialize(toClient);
					callback.onSuccess(result);
				} catch (WrongObjectThrowable e) {
					callback.onFailure(e);
				}
			}
		};
	}
}
