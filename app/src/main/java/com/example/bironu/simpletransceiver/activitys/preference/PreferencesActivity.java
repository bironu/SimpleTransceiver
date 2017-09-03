/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bironu.simpletransceiver.activitys.preference;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.example.bironu.simpletransceiver.R;

/**
 * Handles SIP authentication settings for the Walkie Talkie app.
 */
public class PreferencesActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fragment fragment = new SimplePrefFragment();
		FragmentManager fm = this.getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(android.R.id.content, fragment).commit();
	}

	public static class SimplePrefFragment extends PreferenceFragment
	implements OnSharedPreferenceChangeListener
	{
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.addPreferencesFromResource(R.xml.preferences);
			resetSummary();
			PreferenceManager pm = this.getPreferenceManager();
			SharedPreferences prefs = pm.getSharedPreferences();
			prefs.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			PreferenceManager pm = this.getPreferenceManager();
			SharedPreferences prefs = pm.getSharedPreferences();
			prefs.unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			PreferenceManager pm = this.getPreferenceManager();
			Preference pref = pm.findPreference(key);
			if(pref instanceof EditTextPreference) {
				pref.setSummary(((EditTextPreference)pref).getText());
			}
			this.getActivity().setResult(RESULT_OK);
		}

		// CheckBoxを除く項目のSummaryに現在値を設定する。
		public void resetSummary() {
			PreferenceScreen screen = this.getPreferenceScreen();
			SharedPreferences sharedPrefs = screen.getSharedPreferences();
			for (int i = 0; i < screen.getPreferenceCount(); i++) {
				Preference pref = screen.getPreference(i);
				if (pref instanceof CheckBoxPreference) continue;
				String key = pref.getKey();
				String val = sharedPrefs.getString(key, "");
				pref.setSummary(val);
			}
		}
	}	
}
