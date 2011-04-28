/**
 * 
 */
package update;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;

import com.szas.android.SZASApplication.DBContentProvider;
import com.szas.data.UserTuple;

import flexjson.JSONSerializer;

/**
 * @author pszafer@gmail.com
 *
 */
public class Users {

	public Users() {
		
	}
	public static ContentProviderOperation addUser(UserTuple userTuple, boolean status, ContentResolver contentResolver){
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		
		ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(DBContentProvider.CONTENT_URI);
		builder.withValue(DBContentProvider.DBCOL_ID, userTuple.getId());
		builder.withValue(DBContentProvider.DBCOL_syncTimestamp, "0"); //XXX WHAT INSERT HERE?
		builder.withValue(DBContentProvider.DBCOL_status, status?1:0);
		builder.withValue(DBContentProvider.DBCOL_form, new JSONSerializer().include("*").serialize(userTuple));
		return builder.build();
	}
	
	
	
}
