/**
 * 
 */
package update;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;

import com.szas.android.SZASApplication.SQLLocalDAO;
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
		
		ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(SQLLocalDAO.DBContentProvider.CONTENT_URI);
		builder.withValue(SQLLocalDAO.DBContentProvider.DBCOL_ID, userTuple.getId());
		builder.withValue(SQLLocalDAO.DBContentProvider.DBCOL_syncTimestamp, "0"); //XXX WHAT INSERT HERE?
		builder.withValue(SQLLocalDAO.DBContentProvider.DBCOL_status, status?1:0);
		builder.withValue(SQLLocalDAO.DBContentProvider.DBCOL_form, new JSONSerializer().include("*").serialize(userTuple));
		return builder.build();
	}
	
	
	
}
