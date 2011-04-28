/**
 * 
 */
package update;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.szas.android.SZASApplication.BatchOperations;
import com.szas.data.UserTuple;
import com.szas.sync.Tuple;
import com.szas.sync.remote.RemoteTuple;

/**
 * @author pszafer@gmail.com
 *
 */
public class RemoteTupleManager {
	private static final String TAG = "RemoteTupleManager";
	
	/**
	 * 
	 * @param context The context of Service/Activity
	 * @param account currently not used
	 * @param remoteTuples tuple
	 */
	public static synchronized void syncRemoteTuples(Context context, String account, List<RemoteTuple<Tuple>> remoteTuples){
		long tupleId;
		long rawId = 0;
		final ContentResolver contentResolver = context.getContentResolver();
		final BatchOperations batchOperations = new BatchOperations(contentResolver);
		for(final RemoteTuple<Tuple> remoteTuple: remoteTuples){
			tupleId = remoteTuple.getElement().getId();
			if(remoteTuple.getElement().getClass().getName().equals(UserTuple.class)){
			//	rawId = lookupRawObject(CONTENT_URI, _idCol, contentResolver, id)
				//lookup if id exists
				if(rawId != 0)
				if(!remoteTuple.isDeleted()){
					//update operation
				}
				else
				{
					//delete operation
				}
			}
			else{
				if(!remoteTuple.isDeleted())
				{
					//add tuple
				}
			}
			if(batchOperations.getSize() > 50)		//do batch operations every fifty elements
				batchOperations.execute();
		}
		batchOperations.execute();
	}
	
	/**
	 * Query if object exists in database already
	 * @param CONTENT_URI Uri to know which database working on
	 * @param _idColName Name of id columnd in db
	 * @param contentResolver pass content resolver
	 * @param id check if downloaded id is in db 
	 * @return
	 */
	private static long lookupRawObject(Uri CONTENT_URI, String _idColName, ContentResolver contentResolver, long id){
		//TODO change for cursor size
		long _id = 0;
		final Cursor cursor = contentResolver.query(CONTENT_URI, 
				new String[] {_idColName}, 
				_idColName , 
				new String[] {String.valueOf(id)}, 
				null);
		try{
			if(cursor.moveToFirst())
				_id = cursor.getLong(cursor.getColumnIndexOrThrow(_idColName));
		}
		finally{
			if(cursor!=null)
				cursor.close();
		}
		return _id;
	}
	
	private static void updateTuple(Uri CONTENT_URI, Context context, ContentResolver contentResolver, Tuple tuple,
			long id, BatchOperations batchOperations){
		
	}
}
