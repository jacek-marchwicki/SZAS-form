/**
 * 
 */
package com.szas.android.SZASApplication;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author pszafer@gmail.com Class to handle batch operations execute on
 *         DBProvider XXX not used
 * 
 *         LEGEND: XXX - adnotation FIXME - something to fix TODO - not
 *         implemented yet
 */
public class BatchOperations {
	private final String TAG = "BatchOperations";

	private final ContentResolver contentResolver;
	ArrayList<ContentProviderOperation> operations;

	/**
	 * Initialize class and pass contentResolver
	 * 
	 * @param contentResolver
	 */
	public BatchOperations(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}

	/**
	 * Get size of ContentProviderOperation list
	 * 
	 * @return size of list
	 */
	public int getSize() {
		return operations.size();
	}

	/**
	 * Add new operation to list
	 * 
	 * @param operation
	 *            pass ContentProviderOperation
	 */
	public void addOperation(ContentProviderOperation operation) {
		operations.add(operation);
	}

	public void execute() {
		if (operations.size() == 0)
			return;
		try {
			contentResolver.applyBatch(TAG, operations);// FIXME don't know WHAT
														// authority to pass
		} catch (RemoteException e) {
			Log.e(TAG, "Cannot save data");
		} catch (OperationApplicationException e) {
			Log.e(TAG, "Cannot save data");
		}
		operations.clear();
	}

}
