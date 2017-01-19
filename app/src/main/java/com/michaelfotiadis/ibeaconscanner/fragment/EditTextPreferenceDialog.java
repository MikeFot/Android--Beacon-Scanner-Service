package com.michaelfotiadis.ibeaconscanner.fragment;

import android.os.Bundle;
import android.support.v7.preference.EditTextPreferenceDialogFragmentCompat;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

public class EditTextPreferenceDialog extends EditTextPreferenceDialogFragmentCompat {

    public static EditTextPreferenceDialog newInstance(String key) {
        final EditTextPreferenceDialog
                fragment = new EditTextPreferenceDialog();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        ((EditText) view.findViewById(android.R.id.edit)).setInputType(InputType.TYPE_CLASS_NUMBER);
    }

}