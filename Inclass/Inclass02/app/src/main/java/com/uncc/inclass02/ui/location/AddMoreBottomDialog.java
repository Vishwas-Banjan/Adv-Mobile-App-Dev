package com.uncc.inclass02.ui.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.uncc.inclass02.R;
import com.uncc.inclass02.ui.chatroom.PlaceAsyncTask;
import com.uncc.inclass02.ui.ride.RequestRide;
import com.uncc.inclass02.ui.ride.SendLocation;

import java.util.Objects;

public class AddMoreBottomDialog extends BottomSheetDialogFragment implements View.OnClickListener{
    static PlaceAsyncTask asyncTask;

    public AddMoreBottomDialog() {
        super();
    }

    public static AddMoreBottomDialog newInstance(PlaceAsyncTask task) {
        asyncTask = task;
        return new AddMoreBottomDialog();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_more_dialog, container,
                false);
        view.findViewById(R.id.askForRideBtn).setOnClickListener(this);
        view.findViewById(R.id.shareLocationBtn).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shareLocationBtn:{
                shareLocation();
                break;
            }
            case R.id.askForRideBtn:{
                askForRideBool = true;
                askForLocationPermission();

            }
        }
    }

    private void shareLocation() {
        startActivity(AppConstant.REQUEST_LOC_CODE, SendLocation.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case (AppConstant.REQUEST_RIDE_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    String returnValue = data.getStringExtra(AppConstant.RIDE_REQ_RESULT);
                    String tripId = data.getStringExtra(AppConstant.TRIP_ID_RESULT);
                    asyncTask.setTrip(returnValue, tripId);
                    dismiss();
                }
                break;
            }
            case (AppConstant.REQUEST_LOC_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    String returnValue = data.getStringExtra(AppConstant.LOC_REQ_RESULT);
                    asyncTask.setCurrLocation(returnValue);
                    dismiss();
                }
                break;
            }
        }

    }

    public void goToRequestRide(){
        startActivity(AppConstant.REQUEST_RIDE_CODE, RequestRide.class);
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
        if (askForRideBool){
            startActivity(new Intent(getActivity(), RequestRide.class));
        }else{
            // share the location
        }
    }
}
