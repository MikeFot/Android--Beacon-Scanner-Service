package com.michaelfotiadis.ibeaconscanner.activities;

import android.os.Bundle;

import com.michaelfotiadis.ibeaconscanner.R;
import com.michaelfotiadis.ibeaconscanner.fragment.SettingsFragment;

public class ScanPreferencesActivity extends BaseActivity {

    private static final String FRAGMENT_TAG = ScanPreferencesActivity.class.getSimpleName() + "_fragment_tag";

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.action_settings));

        setDisplayHomeAsUpEnabled(true);

        addContentFragmentIfMissing(new SettingsFragment(), R.id.content_frame, FRAGMENT_TAG);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_default_fragment_container;
    }


}
