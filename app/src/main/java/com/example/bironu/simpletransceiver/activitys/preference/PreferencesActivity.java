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
 * 設定画面。
 */
public class PreferencesActivity extends Activity
{

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
            if (pref instanceof EditTextPreference) {
                pref.setSummary(((EditTextPreference) pref).getText());
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
