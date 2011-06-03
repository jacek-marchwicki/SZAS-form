/**
 * 
 */
package com.szas.android.SZASApplication.UI;

import com.szas.android.SZASApplication.Constans;
import com.szas.android.SZASApplication.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * @author pszafer@gmail.com
 *
 */
public class Preferences extends PreferenceActivity{
	
	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		String name = getResources().getString(R.string.preference_accountchoose_key);
		String importName = getResources().getString(R.string.preference_import_key);
		String exportName = getResources().getString(R.string.preference_export_key);
		ListPreference accountChooser = (ListPreference)findPreference(name);
		Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType(Constans.ACCOUNT_TYPE);
		int i =0;
		String[] values = new String[accounts.length];
		String[] entries = new String[accounts.length];
		for(Account account : accounts){
			entries[i] = account.name;
			values[i] = String.valueOf(i);
			++i;
		}
		accountChooser.setEntries(entries);
		accountChooser.setEntryValues(values);
		Preference preference = findPreference(importName);
		preference.setTitle(Constans.mCSVImportDirectory);
		preference = findPreference(exportName);
		preference.setTitle(Constans.mCSVExportDirectory);
		
	}
}
