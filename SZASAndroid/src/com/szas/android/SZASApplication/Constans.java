/**
 * 
 */
package com.szas.android.SZASApplication;

import java.io.File;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

/**
 * @author pszafer@gmail.com XXX not used
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 */
public class Constans {

	public static final String broadcastMessage = "com.szas.android.szasapplication.broadcast";
	public static final String changesQuestionnaireMessage = "com.szas.android.szasapplication.changes";
	public static final String changesFilledMessage = "com.szas.android.szasapplication.filledchanges";
	/**
	 * Account type to get credentials
	 */
	public static final String ACCOUNT_TYPE = "com.google";

	public static final String MAIN_PREFERENCE_NAME = "mainPreferences";

	public static final String authority = "com.szas.android.szasapplication.provider";

	public static String mCSVImportDirectory;

	public static String mCSVExportDirectory;

	/**
	 * Authtoken type
	 */
	public static final String AUTHTOKEN_TYPE = "com.google";

	/**
	 * Result for Activity exit
	 */
	public static final int RESULT_EXIT = 2;

	public static class RefreshSyncAdapter {
		public static void refreshSyncAdapter(Context context) {
			Account[] accounts = AccountManager.get(context).getAccountsByType(
					Constans.ACCOUNT_TYPE);
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			int accountNumber = Integer.parseInt(preferences.getString(
					context.getString(R.string.preference_accountchoose_key),
					"0"));
			int isSyncable = ContentResolver.getIsSyncable(
					accounts[accountNumber], authority);
			if (isSyncable > 0) {
				Bundle extras = new Bundle();
				extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
				ContentResolver.requestSync(accounts[accountNumber], authority, extras);
			}
		}
	}

	/**
	 * 
	 * @param context
	 * @param isImport
	 *            if true is for import, false is for export
	 */
	public static void createDirectory(Context context, boolean isImport) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String directory = null;
		if (isImport)
			directory = preferences.getString(
					context.getString(R.string.preference_import_key),
					Environment.getExternalStorageDirectory() + "/szas/import");
		else
			directory = preferences.getString(
					context.getString(R.string.preference_export_key),
					Environment.getExternalStorageDirectory() + "/szas/export");
		File dir;
		if (directory != null) {
			directory = directory.replace(" ", "");
			dir = new File(directory);
			if (dir.mkdirs()||dir.exists()) {
				if (isImport) {
					mCSVImportDirectory = directory;
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString(
							context.getString(R.string.preference_import_key),
							directory);
					editor.commit();
				} else {
					mCSVExportDirectory = directory;
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString(
							context.getString(R.string.preference_export_key),
							directory);
					editor.commit();
				}
				return;
			}
		}
	}

}
