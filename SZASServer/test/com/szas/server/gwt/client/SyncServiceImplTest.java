package com.szas.server.gwt.client;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class SyncServiceImplTest {
	private class MockSubTuple extends Tuple {
		public int data = 0;
	}
	private class MockTupleLocalService implements LocalDAO<MockSubTuple> {

		@Override
		public ArrayList<MockSubTuple> getAll() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void insert(MockSubTuple element) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void delete(MockSubTuple element) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void update(MockSubTuple element) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ArrayList<LocalTuple<MockSubTuple>> getElementsToSync() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	LocalSyncHelper syncService;
	@Before
	public void setUp() {
		// TODO
		//syncService = new SyncServiceImpl();
	}
	@Test
	public void testSyncing() {
		LocalDAO<MockSubTuple> mockTuples = new MockTupleLocalService();
		syncService.append(MockSubTuple.class, mockTuples);
	}
}
