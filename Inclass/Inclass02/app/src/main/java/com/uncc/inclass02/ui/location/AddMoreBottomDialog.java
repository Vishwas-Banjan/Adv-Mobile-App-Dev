package com.uncc.inclass02.ui.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.uncc.inclass02.R;
import com.uncc.inclass02.ui.ride.RequestRide;

import java.util.Objects;

public class AddMoreBottomDialog extends BottomSheetDialogFragment implements View.OnClickListener{
    final private int locationPermissionCode = 2;

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
        addMoreDialogSheet.findViewById(R.id.shareLocationBtn).setOnClickListener(this);
        addMoreDialogSheet.findViewById(R.id.askForRideBtn).setOnClickListener(this);

//        startActivity(new Intent(getActivity(), RequestRide.class));

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
                startActivity(new Intent(getActivity(), RideRouteActivity.class));
                break;
            }
        }
    }

    private void askForLocationPermission(){
        if (Build.VERSION.SDK_INT>=23){
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, locationPermissionCode);
            }else{
                // get location
                getLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==locationPermissionCode){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                // get location
                getLocation();
            }else{
                Snackbar.make(getView(), "Please grant the location permission so that we can serve you better.", Snackbar.LENGTH_LONG).show();
                askForLocationPermission();
            }
        }
    }

    private void getLocation(){
        // get the current location coordinates
    }
}
