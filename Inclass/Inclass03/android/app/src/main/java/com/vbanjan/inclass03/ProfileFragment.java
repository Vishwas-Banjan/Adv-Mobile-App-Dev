package com.vbanjan.inclass03;

import android.annotation.SuppressLint;
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

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
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
import okhttp3.ResponseBody;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    String TAG = "demo";
    EditText userFirstName, userLastName, userEmail, userCity;
    MaterialButtonToggleGroup genderToggleGroup;
    MaterialButton editBtn, saveBtn;
    SharedPreferences sharedPref;
    String userID;
    NavController navController;
    private OnFragmentInteractionListener mListener;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getString("userID") != null) {
            userID = getArguments().getString("userID");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        userFirstName = view.findViewById(R.id.firstNameEditText);
        userLastName = view.findViewById(R.id.lastNameEditText);
        userEmail = view.findViewById(R.id.emailEditText);
        userCity = view.findViewById(R.id.cityEditText);
        genderToggleGroup = view.findViewById(R.id.toggle_button_group);
        editBtn = view.findViewById(R.id.createAccountBtn);
        editBtn.setOnClickListener(this);
        saveBtn = view.findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(this);

        new getUserDetails().execute(); //Async Task
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setFields(User user) {
        if (user != null) {
            userFirstName.setText(user.getUserFirstName());
            userLastName.setText(user.getUserLastName());
            userEmail.setText(user.getUserEmail());
            userCity.setText(user.getUserCity());
            if (user.getUserGender().equals("Male")) {
                genderToggleGroup.check(R.id.maleButton);
            } else {
                genderToggleGroup.check(R.id.femaleButton);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(String.valueOf(R.string.userToken));
            editor.apply();
            navController.navigate(R.id.action_profileFragment_to_logInFragment);
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createAccountBtn:
                enableFields();
                editBtn.setVisibility(View.GONE);
                saveBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.saveButton:
                disableFields();
                saveBtn.setVisibility(View.GONE);
                editBtn.setVisibility(View.VISIBLE);
                if (validateInputFields()) {
                    //Dismiss keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(userCity.getWindowToken(), 0);
                    new updateUserDetails(getUserInputDetails()).execute();
                }
                break;
        }
    }

    public void disableFields() {
        userFirstName.setEnabled(false);
        userLastName.setEnabled(false);
        userEmail.setEnabled(false);
        userCity.setEnabled(false);
        genderToggleGroup.getChildAt(0).setEnabled(false);
        genderToggleGroup.getChildAt(1).setEnabled(false);
    }

    public void enableFields() {
        userFirstName.setEnabled(true);
        userLastName.setEnabled(true);
        userCity.setEnabled(true);
        genderToggleGroup.getChildAt(0).setEnabled(true);
        genderToggleGroup.getChildAt(1).setEnabled(true);
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class getUserDetails extends AsyncTask<Void, Void, User> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Fetching details, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        public getUserDetails() {
            progressDialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (user != null) {
                setFields(user);
                disableFields();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } else {
                Toast.makeText(getContext(), "Oops! Couldn't fetch user details", Toast.LENGTH_SHORT).show();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected User doInBackground(Void... voids) {
            User user = new User();
            sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .header("Authorization", "Bearer " + sharedPref.getString(getString(R.string.userToken), ""))
                    .url(getString(R.string.userDetailURL) + userID)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    String json = responseBody.string();
                    JSONObject root = new JSONObject(json);
                    user.setUserFirstName(root.getString("firstName"));
                    user.setUserLastName(root.getString("lastName"));
                    user.setUserEmail(root.getString("email"));
                    user.setUserCity(root.getString("city"));
                    user.setUserGender(root.getString("gender"));

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return user;
        }
    }

    private class updateUserDetails extends AsyncTask<Void, Void, User> {
        User user;
        private ProgressDialog progressDialog;

        public updateUserDetails(User user) {
            this.user = user;
            progressDialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Updating your account details, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                setFields(user);
                disableFields();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "Oops! Couldn't fetch user details", Toast.LENGTH_SHORT).show();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected User doInBackground(Void... voids) {
            sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            final OkHttpClient client = new OkHttpClient();
            Log.d(TAG, "doInBackground: " + this.user.toString());
            RequestBody formBody = new FormBody.Builder()
                    .add("firstName", this.user.getUserFirstName())
                    .add("lastName", this.user.getUserLastName())
                    .add("city", this.user.getUserCity())
                    .add("gender", this.user.getUserGender())
                    .add("id", userID)
                    .build();
            Request request = new Request.Builder()
                    .header("Authorization", "Bearer " + sharedPref.getString(getString(R.string.userToken), ""))
                    .header("Content-Type", "application/json")
                    .url(getString(R.string.userDetailURL))
                    .put(formBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response.toString());
                User user = new User();
                String json = response.body().string();
                JSONObject root = new JSONObject(json);
                user.setUserFirstName(root.getString("firstName"));
                user.setUserLastName(root.getString("lastName"));
                user.setUserEmail(root.getString("email"));
                user.setUserCity(root.getString("city"));
                user.setUserGender(root.getString("gender"));

            } catch (JSONException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return user;
        }
    }


    public User getUserInputDetails() {
        String gender;
        if (genderToggleGroup.getCheckedButtonId() == R.id.maleButton) {
            gender = "Male";
        } else {
            gender = "Female";
        }
        User user = new User(userFirstName.getText().toString().trim(),
                userLastName.getText().toString().trim(),
                userEmail.getText().toString().trim(),
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
        } else if (userCity.getText().toString().trim().equals("")) {
            userCity.setError("Field Required");
        } else if (genderToggleGroup.getCheckedButtonId() == -1) {
            Toast.makeText(getContext(), "Select gender", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }
}
