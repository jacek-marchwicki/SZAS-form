/**
 * 
 */
package com.szas.android.SZASApplication.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.szas.android.SZASApplication.R;

/**
 * @author pszafer@gmail.com Create about dialog in SZAS program
 * 
 *         LEGEND: XXX - adnotation FIXME - something wrong TODO - not
 *         implemented yet
 */
public class AlertsDialog {
	public static class DialogBuilder {
		public static AlertDialog createAboutWindow(Context context)
				throws NameNotFoundException {
			// Try to load the a package matching the name of our own package
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			String versionInfo = pInfo.versionName;

			String aboutTitle = context.getString(R.string.about) + " "
					+ context.getString(R.string.app_name);
			String versionString = context.getString(R.string.version_word)
					+ ": " + versionInfo;
			String aboutText = context.getString(R.string.about_authors);
			// Set up the TextView
			final TextView message = new TextView(context);
			// We'll use a spannablestring to be able to make links clickable
			final SpannableString s = new SpannableString(aboutText);

			// Set some padding
			message.setPadding(5, 5, 5, 5);
			// Set up the final string
			message.setText(versionString + "\n\n" + s);
			// Now linkify the text
			Linkify.addLinks(message, Linkify.ALL);

			return new AlertDialog.Builder(context)
					.setTitle(aboutTitle)
					.setCancelable(true)
					.setIcon(R.drawable.icon)
					.setPositiveButton(context.getString(android.R.string.ok),
							null).setView(message).create();
		}

		public static AlertDialog createHelpWindow(Context context, String text)
				throws NameNotFoundException {
			String helpTitle = context.getString(R.string.help);			
			LinearLayout linear = new LinearLayout(context);
			linear.setOrientation(LinearLayout.VERTICAL);
			ScrollView scrollView = new ScrollView(context);
			scrollView.addView(linear);
			final TextView message = new TextView(context);
			final SpannableString s = new SpannableString(text);
			message.setPadding(5, 5, 5, 5);
			message.setText(s);
			message.setScrollbarFadingEnabled(true);
			Linkify.addLinks(message, Linkify.ALL);
			linear.addView(message);
			return new AlertDialog.Builder(context)
					.setTitle(helpTitle)
					.setCancelable(true)
					.setIcon(R.drawable.icon)
					.setPositiveButton(context.getString(android.R.string.ok),
							null).setView(scrollView).create();
		}

	}
}
