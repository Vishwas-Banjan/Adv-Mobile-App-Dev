package com.vbanjan.inclass03;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SignUpFragment extends Fragment implements View.OnClickListener {
    NavController navController;
    private OnFragmentInteractionListener mListener;
    SharedPreferences sharedPref;

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
        view.findViewById(R.id.editButton).setOnClickListener(this);
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
            case R.id.editButton:
                //TODO Create Account and Login
                if (validateInputFields()) {
                    Log.d(TAG, "onClick: Create Account " + getUserInputDetails().toString());
//                    new signUpUser(getUserInputDetails()).execute(); //Async Task
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userDetails", getUserInputDetails());
                    Toast.makeText(getContext(), "New Account Created", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_signUpFragment_to_profileFragment, bundle);
                }
                break;
        }
    }

    public User getUserInputDetails() {
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

    String createAccountURL; //TODO Set CreateUserURL

    private class signUpUser extends AsyncTask<Void, Void, String> {
        User user;
        private ProgressDialog progressDialog;

        public signUpUser(User user) {
            this.user = user;
            progressDialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Creating your account, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.userToken), s);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {
            final OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("firstName", user.getUserFirstName())
                    .add("lastName", user.getUserLastName())
                    .add("email", user.getUserEmail())
                    .add("password", user.getUserPassword())
                    .add("city", user.getUserCity())
                    .add("gender", user.getUserGender())
                    .build();
            Request request = new Request.Builder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .url(createAccountURL)
                    .post(formBody)
                    .build();
            String token = null;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response.toString());
                String json = response.body().string();
                JSONObject root = new JSONObject(json);
                if (root.getBoolean("auth") == true) {
                    token = root.getString("token");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return token;
        }
    }
}
