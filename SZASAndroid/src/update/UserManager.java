/**
 * 
 */
package update;

import java.util.List;

import com.szas.data.UserTuple;

import android.content.ContentResolver;
import android.content.Context;

/**
 * @author pszafer@gmai.com
 *	
 *	Class for managing syncing process Users
 */
public class UserManager {
	private static final String TAG = "UserManager";
	
	public static synchronized void syncUsers(Context context, String account, List<UserTuple> usersTuple){
		long userId;
		
		final ContentResolver contentResolver = context.getContentResolver();
		
		for(final UserTuple userTuple : usersTuple){
			userId = userTuple.getId();
			if(userTuple)
		}
	}
	
}
