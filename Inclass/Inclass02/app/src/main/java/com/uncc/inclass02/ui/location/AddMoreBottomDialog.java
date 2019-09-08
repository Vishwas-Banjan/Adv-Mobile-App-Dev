package com.uncc.inclass02.ui.location;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
import com.uncc.inclass02.ui.ride.RequestRide;

public class AddMoreBottomDialog extends BottomSheetDialogFragment implements View.OnClickListener{
    public AddMoreBottomDialog() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View addMoreDialogSheet = View.inflate(getContext(), R.layout.fragment_add_more_dialog, null);
//        dialog.findViewById(R.id.shareLocationBtn).setOnClickListener(this);
//        dialog.findViewById(R.id.askForRideBtn).setOnClickListener(this);

//        startActivity(AppConstant.REQUEST_RIDE_CODE, RequestRide.class);

        dialog.setContentView(addMoreDialogSheet);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shareLocationBtn:{
                break;
            }
            case R.id.askForRideBtn:{
                askForLocationPermission();
                break;
            }
        }
    }

    public void askForLocationPermission(){
        // ask for permission
    }
}
