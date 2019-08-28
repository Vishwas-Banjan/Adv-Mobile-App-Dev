package com.uncc.inclass01.ui.dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.uncc.inclass01.R;

import java.util.Objects;

public class EditProfileDialog extends DialogFragment { // fix taken from stackoverflow

    private Dialog dialog;
    private EditText editText;
    private Button submit;
    String TAG = "Dialog TAG";
    private String profileInfo = "error in db, please contact the developer";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_edit_profile_dialog);
        if (savedInstanceState != null) {
            profileInfo = savedInstanceState.getString("profile_info");
        }
//        set input type and hint
        editText = dialog.findViewById(R.id.editText);
        submit = dialog.findViewById(R.id.submit);
        editText.setHint(profileInfo);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return dialog;
    }

}
