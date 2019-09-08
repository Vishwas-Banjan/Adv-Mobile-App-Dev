package com.uncc.inclass02.ui.location;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.uncc.inclass02.AppConstant;
import com.uncc.inclass02.R;
import com.uncc.inclass02.ui.ride.RequestRide;

public class AddMoreBottomDialog extends BottomSheetDialogFragment implements View.OnClickListener{
    public AddMoreBottomDialog() {
        super();
    }

    public static AddMoreBottomDialog newInstance() {
        return new AddMoreBottomDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_more_dialog, container,
                false);
        view.findViewById(R.id.askForRideBtn).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shareLocationBtn:{
                break;
            }
            case R.id.askForRideBtn:{
                goToRequestRide();
                break;
            }
        }
    }

    public void goToRequestRide(){
        startActivity(AppConstant.REQUEST_RIDE_CODE, RequestRide.class);
    }

    private void startActivity(int code, Class<?> cls) {
        Intent i = new Intent(getActivity(), cls);
        startActivityForResult(i, code);
    }
}
