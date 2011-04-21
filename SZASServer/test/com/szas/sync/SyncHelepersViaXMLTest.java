package com.szas.sync;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import com.szas.sync.local.SyncLocalService;
import com.szas.sync.local.SyncLocalServiceResult;
import com.thoughtworks.xstream.XStream;

public class SyncHelepersViaXMLTest extends SyncHelpersTest {
	@Override
	protected SyncLocalService getSyncLocalService() {
		return new SyncLocalService() {
			
			@Override
			public void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
					SyncLocalServiceResult callback) {
				
				
				
				try {
					// CLient side
					String toServer;
					XStream xStream = new XStream();
					toServer = xStream.toXML(toSyncElementsHolders);
System.out.println(toServer);					
					// Server side
					String toClient = serverSide(toServer);	
System.out.println(toClient);
					
					
					// Client side
					@SuppressWarnings("unchecked")
					ArrayList<SyncedElementsHolder> result = 
						(ArrayList<SyncedElementsHolder>) xStream.fromXML(toClient);
					
					callback.onSuccess(result);
				} catch (WrongObjectThrowable e) {
					callback.onFailure(e);
				} catch (JAXBException e) {
					e.printStackTrace();
					callback.onFailure(e);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					callback.onFailure(e);
				} catch (ClassCastException e) {
					callback.onFailure(e);
					e.printStackTrace();
				}
			}

			private String serverSide(String toServer) throws JAXBException,
					UnsupportedEncodingException, WrongObjectThrowable, ClassCastException {
				XStream xStream = new XStream();

				@SuppressWarnings("unchecked")
				ArrayList<ToSyncElementsHolder> toSyncElementsHolders = 
					(ArrayList<ToSyncElementsHolder>) xStream.fromXML(toServer);
				
				ArrayList<SyncedElementsHolder> server_sResult =
					remoteSyncHelper.sync(toSyncElementsHolders);
				
				
				String toClient = xStream.toXML(server_sResult);
				
				return toClient;
			}
		};
	}
}
