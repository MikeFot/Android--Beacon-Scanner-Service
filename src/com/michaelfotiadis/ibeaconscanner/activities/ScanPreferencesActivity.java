package com.michaelfotiadis.ibeaconscanner.activities;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.InputType;

import com.michaelfotiadis.ibeaconscanner.R;

public class ScanPreferencesActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 addPreferencesFromResource(R.xml.settings);
		 
		 EditTextPreference prefScanTime = (EditTextPreference) findPreference(getString(R.string.pref_scantime));
		 prefScanTime.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		 
		 EditTextPreference prefPauseTime = (EditTextPreference) findPreference(getString(R.string.pref_pausetime));
		 prefPauseTime.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
	}
}
