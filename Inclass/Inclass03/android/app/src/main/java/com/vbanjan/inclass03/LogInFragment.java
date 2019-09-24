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
import android.widget.EditText;

import com.vbanjan.inclass03.Utils.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LogInFragment extends Fragment implements View.OnClickListener {

    NavController navController;
    SharedPreferences sharedPref;
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
                    Log.d(TAG, "onClick: Login " + getLogInInputDetails().toString());
                    //TODO Login Auth
//                    new logInUser(getLogInInputDetails()).execute(); //Async Task
                    navController.navigate(R.id.action_logInFragment_to_profileFragment);
                }
                break;
        }
    }

    public User getLogInInputDetails() {
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

    String logInURL = R.string.baseURL+"/auth/login"; //TODO Set LogIn URL

    private class logInUser extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        User user;

        public logInUser(User user) {
            this.user = user;
            progressDialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Logging you in, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.userToken), s);
            editor.commit();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {
            final OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("email", user.getUserEmail())
                    .add("password", user.getUserPassword())
                    .build();
            Request request = new Request.Builder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .url(logInURL)
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
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return token;
        }
    }
}
