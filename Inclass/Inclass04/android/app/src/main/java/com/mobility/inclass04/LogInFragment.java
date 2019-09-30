package com.mobility.inclass04;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.mobility.inclass04.Utils.User;

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
    String userId;
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
        mListener.setDrawerLocked(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpTextView:
                navController.navigate(R.id.action_logInFragment_to_signUpFragment);
                break;
            case R.id.loginButton:
                if (validateInputFields()) {
                    //Dismiss keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(userPassword.getWindowToken(), 0);
                    new logInUser(getLogInInputDetails()).execute(); //Async Task
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
        void setDrawerLocked(boolean shouldLock);

        void onFragmentInteraction(Uri uri);
    }

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
            if (s != null) {
                sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.userToken), s);
                editor.commit();
                Log.d(TAG, "onPostExecute: UserID: " + userId + "\n Token: " + s);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("userID", userId);

                navController.navigate(R.id.action_logInFragment_to_profileFragment, bundle);
            } else {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getContext(), "Invalid Credentials!", Toast.LENGTH_SHORT).show();
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
                    .header("Content-Type", "application/json")
                    .url(getString(R.string.logInURL))
                    .post(formBody)
                    .build();
            String token = null;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response.toString());

                String json = response.body().string();
                JSONObject root = new JSONObject(json);
                if (!root.getString("token").equals("")) {
                    JSONObject userJSON = new JSONObject(root.getString("user"));
                    userId = userJSON.getString("_id");
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
