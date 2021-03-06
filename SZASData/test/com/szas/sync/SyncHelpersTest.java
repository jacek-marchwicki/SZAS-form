package com.szas.sync;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.szas.sync.local.LocalDAO;
import com.szas.sync.local.LocalDAOImpl;
import com.szas.sync.local.LocalSyncHelper;
import com.szas.sync.local.LocalSyncHelperImpl;
import com.szas.sync.local.SyncLocalService;
import com.szas.sync.local.SyncLocalServiceResult;
import com.szas.sync.local.SyncObserver;
import com.szas.sync.remote.RemoteDAO;
import com.szas.sync.remote.RemoteDAOImpl;
import com.szas.sync.remote.RemoteSyncHelper;
import com.szas.sync.remote.RemoteSyncHelperImpl;

public class SyncHelpersTest {
	protected static class MockSubTuple extends Tuple {
		private static final long serialVersionUID = 1L;
		private int data = 0;
		public void assertSame(MockSubTuple otherTuple) {
			assertEquals("Data schould be the same", this.getData(), otherTuple.getData());
			assertEquals("Index schould be the same", this.getId(), otherTuple.getId());
		}
		public void setData(int data) {
			this.data = data;
		}
		public int getData() {
			return data;
		}
	}
	private static final int EXAMPLE_DATA = 10;
	private static final int NEW_EXAMPLE_DATA = 12;
	private LocalSyncHelper localSyncHelper;
	protected RemoteSyncHelper remoteSyncHelper;
	private RemoteDAO<MockSubTuple> remoteMockTuples;
	private LocalDAO<MockSubTuple> localMockTuples;
	private MockSyncObserver mockSyncObserver;
	protected SyncLocalService getSyncLocalService() {
		return new SyncLocalService() {
			
			@Override
			public void sync(ArrayList<ToSyncElementsHolder> toSyncElementsHolders,
					SyncLocalServiceResult callback) {
				try {
					ArrayList<SyncedElementsHolder> result =
						remoteSyncHelper.sync(toSyncElementsHolders);
					callback.onSuccess(result);
				} catch (WrongObjectThrowable e) {
					callback.onFailure(e);
				}				
			}
		};
	}
	protected static class MockSyncObserver implements SyncObserver {
		public boolean sucess = false;
		public boolean fail = false;
		public boolean start = false;
		@Override
		public void onSucces() {
			sucess = true;
		}

		@Override
		public void onFail(Throwable throwable) {
			fail = true;
		}
		
		@Override
		public void onStart() {
			start = true;
		}
		
		public void reset() {
			sucess = false;
			fail = false;
			start = false;
		}
	}
	
	@Before
	public void setUp() {
		mockSyncObserver = new MockSyncObserver();
		
		remoteSyncHelper = new RemoteSyncHelperImpl();
		localSyncHelper = new LocalSyncHelperImpl(getSyncLocalService());
		localSyncHelper.addSyncObserver(mockSyncObserver);
		
		localMockTuples = new LocalDAOImpl<MockSubTuple>();
		remoteMockTuples = new RemoteDAOImpl<MockSubTuple>();
		
		localSyncHelper.append(MockSubTuple.class.getName(), localMockTuples);
		remoteSyncHelper.append(MockSubTuple.class.getName(), remoteMockTuples);
	}
	@Test
	public void testPushing() {
		MockSubTuple localTuple = makeExampleTuple();
		localMockTuples.insert(localTuple);
		assertEquals("Size after insertion schould be 1",1,localMockTuples.getAll().size());
		
		mockSyncObserver.reset();
		localSyncHelper.sync();
		assertTrue("start Sync Observer schould be notiffied", mockSyncObserver.start);
		assertTrue("succes Sync Observer schould be notiffied", mockSyncObserver.sucess);
		
		assertEquals("Size after sync schould be same" ,1,localMockTuples.getAll().size());
		
		assertEquals("Size after sync schould be equal",
				localMockTuples.getAll().size(),
				remoteMockTuples.getAll().size());
		
		Collection<MockSubTuple> remoteTuples = remoteMockTuples.getAll();
		MockSubTuple remoteTuple = remoteTuples.iterator().next();
		localTuple.assertSame(remoteTuple);	
		
		remoteTuple.setData(NEW_EXAMPLE_DATA);
		remoteMockTuples.update(remoteTuple);
		
		localSyncHelper.sync();
		
		Collection<MockSubTuple> localTuples = localMockTuples.getAll();
		assertEquals("(sync after update schould not lead to create new rows)",1,localTuples.size());
		localTuple = localTuples.iterator().next();
		remoteTuple.assertSame(localTuple);
		
	}
	@Test
	public void testGetting() {
		MockSubTuple remoteTuple = new MockSubTuple();
		remoteTuple.setData(EXAMPLE_DATA);
		
		remoteMockTuples.insert(remoteTuple);
		assertEquals(1, remoteMockTuples.getAll().size());
		
		localSyncHelper.sync();
		
		assertEquals("size after sync schoud be same", 1, remoteMockTuples.getAll().size());
		
		assertEquals("Size after sync schould be equal",
				remoteMockTuples.getAll().size(),
				localMockTuples.getAll().size());
		
		Collection<MockSubTuple> localTuples = remoteMockTuples.getAll();
		MockSubTuple localTuple = localTuples.iterator().next();
		
		localTuple.setData(NEW_EXAMPLE_DATA);
		remoteMockTuples.update(localTuple);
		
		localSyncHelper.sync();
		
		Collection<MockSubTuple> remoteTuples = localMockTuples.getAll();
		assertEquals("(sync after update schould not lead to create new rows)",1,remoteTuples.size());
		localTuple.assertSame(remoteTuple);
	}
	
	private MockSubTuple makeExampleTuple() {
		MockSubTuple newTuple = new MockSubTuple();
		newTuple.setData(EXAMPLE_DATA);
		return newTuple;
	}
}
