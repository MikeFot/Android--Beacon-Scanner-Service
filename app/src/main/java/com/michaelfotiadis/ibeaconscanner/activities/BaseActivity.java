package com.michaelfotiadis.ibeaconscanner.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.michaelfotiadis.ibeaconscanner.R;
import com.michaelfotiadis.ibeaconscanner.utils.log.AppLog;

public abstract class BaseActivity extends AppCompatActivity {

    protected static final int NO_LAYOUT = Integer.MIN_VALUE;

    private Toolbar mToolbar;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.d("OnCreate");
        if (getLayoutResource() != NO_LAYOUT) {
            AppLog.d("On Create with layout resource " + getLayoutResource());
            setContentView(getLayoutResource());
            setupActionBar();

        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            setTitle("");
        } else {
            AppLog.w(this.getClass().getName() + ": Null toolbar");
        }
    }

    public void setDisplayHomeAsUpEnabled(final boolean isEnabled) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(isEnabled);
        }
    }

    protected abstract int getLayoutResource();

    protected void addContentFragmentIfMissing(final Fragment fragment, final int id, final String fragmentTag) {
        if (getSupportFragmentManager().findFragmentByTag(fragmentTag) == null) {
            final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(id, fragment, fragmentTag);
            fragmentTransaction.commit();
        }
    }

}
