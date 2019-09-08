package com.uncc.inclass01.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.uncc.inclass01.R;

public class AddMoreBottomDialog extends BottomSheetDialogFragment {
    public AddMoreBottomDialog() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View addMoreDialogSheet = View.inflate(getContext(), R.layout.fragment_add_more_dialog, null);
        dialog.setContentView(addMoreDialogSheet);
    }
}
