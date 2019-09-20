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
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.vbanjan.inclass03.Utils.User;


public class SignUpFragment extends Fragment implements View.OnClickListener {
    NavController navController;
    private OnFragmentInteractionListener mListener;


    public SignUpFragment() {
        // Required empty public constructor
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    Button createAccount;
    EditText userFirstName, userLastName, userEmail, userPassword, userCity;
    MaterialButtonToggleGroup genderToggleGroup;
    String TAG = "demo";


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        view.findViewById(R.id.createAccountButton).setOnClickListener(this);
        view.findViewById(R.id.logInTextView).setOnClickListener(this);
        userFirstName = view.findViewById(R.id.firstNameEditText);
        userLastName = view.findViewById(R.id.lastNameEditText);
        userEmail = view.findViewById(R.id.emailEditText);
        userPassword = view.findViewById(R.id.passwordEditText);
        userCity = view.findViewById(R.id.cityEditText);
        genderToggleGroup = view.findViewById(R.id.toggle_button_group);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logInTextView:
                navController.navigate(R.id.action_signUpFragment_to_logInFragment);
                break;
            case R.id.createAccountButton:
                //TODO Create Account and Login
                if (validateInputFields()) {
                    Log.d(TAG, "onClick: " + getUserDetails().toString());
                    Toast.makeText(getContext(), "New Account Created", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_signUpFragment_to_profileFragment);
                }
                break;
        }

    }

    public User getUserDetails() {
        String gender;
        if (genderToggleGroup.getCheckedButtonId() == 0) {
            gender = "Male";
        } else {
            gender = "Female";
        }
        User user = new User(userFirstName.getText().toString().trim(),
                userLastName.getText().toString().trim(),
                userEmail.getText().toString().trim(),
                userPassword.getText().toString().trim(),
                userCity.getText().toString().trim(),
                gender);
        return user;
    }

    public boolean validateInputFields() {
        if (userFirstName.getText().toString().trim().equals("")) {
            userFirstName.setError("Field Required");
        } else if (userLastName.getText().toString().trim().equals("")) {
            userLastName.setError("Field Required");
        } else if (userEmail.getText().toString().trim().equals("")) {
            userEmail.setError("Field Required");
        } else if (userPassword.getText().toString().trim().equals("")) {
            userPassword.setError("Field Required");
        } else if (userCity.getText().toString().trim().equals("")) {
            userCity.setError("Field Required");
        } else if (genderToggleGroup.getCheckedButtonId() == -1) {
            Toast.makeText(getContext(), "Select gender", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
