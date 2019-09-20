package com.vbanjan.inclass03;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vbanjan.inclass03.Utils.User;


public class LogInFragment extends Fragment implements View.OnClickListener {

    NavController navController;

    private OnFragmentInteractionListener mListener;

    public LogInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    EditText userEmail, userPassword;
    String TAG = "demo";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        view.findViewById(R.id.signUpTextView).setOnClickListener(this);
        view.findViewById(R.id.loginButton).setOnClickListener(this);
        userEmail = view.findViewById(R.id.emailEditText);
        userPassword = view.findViewById(R.id.passwordEditText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpTextView:
                navController.navigate(R.id.action_logInFragment_to_signUpFragment);
                break;
            case R.id.loginButton:
                if (validateInputFields()) {
                    Log.d(TAG, "onClick: " + getLogInDetails().toString());
                    //TODO Login Auth
                    navController.navigate(R.id.action_logInFragment_to_profileFragment);
                }
                break;
        }
    }

    public User getLogInDetails() {
        User user = new User();
        user.setUserEmail(userEmail.getText().toString().trim());
        user.setUserPassword(userPassword.getText().toString().trim());
        return user;
    }

    public boolean validateInputFields() {
        if (userEmail.getText().toString().trim().equals("")) {
            userEmail.setError("Field Required");
        } else if (userPassword.getText().toString().trim().equals("")) {
            userPassword.setError("Field Required");
        } else {
            return true;
        }
        return false;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
