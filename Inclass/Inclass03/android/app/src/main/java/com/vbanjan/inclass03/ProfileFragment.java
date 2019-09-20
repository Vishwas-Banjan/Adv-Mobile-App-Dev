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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.vbanjan.inclass03.Utils.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    String TAG = "demo";
    EditText userFirstName, userLastName, userEmail, userCity;
    MaterialButtonToggleGroup genderToggleGroup;
    MaterialButton editBtn, saveBtn;
    SharedPreferences sharedPref;
    User user;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Serializable bundle = this.getArguments().getSerializable("userDetails");
        if (bundle != null) {
            user = (User) this.getArguments().getSerializable("userDetails");
            Log.d(TAG, "onCreate: ProfileBundle " + user.toString());
        }
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
        userFirstName = view.findViewById(R.id.firstNameEditText);
        userLastName = view.findViewById(R.id.lastNameEditText);
        userEmail = view.findViewById(R.id.emailEditText);
        userCity = view.findViewById(R.id.cityEditText);
        genderToggleGroup = view.findViewById(R.id.toggle_button_group);
        editBtn = view.findViewById(R.id.editButton);
        editBtn.setOnClickListener(this);
        saveBtn = view.findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(this);
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
        disableFields();
//        new getUserDetails().execute(); //Async Task
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editButton:
                enableFields();
                editBtn.setVisibility(View.GONE);
                saveBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.saveButton:
                disableFields();
                saveBtn.setVisibility(View.GONE);
                editBtn.setVisibility(View.VISIBLE);
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
        userEmail.setEnabled(true);
        userCity.setEnabled(true);
        genderToggleGroup.getChildAt(0).setEnabled(true);
        genderToggleGroup.getChildAt(1).setEnabled(true);
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    String getUser; //TODO Set Get User Details URL

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
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected User doInBackground(Void... voids) {

            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .header("x-access-token", sharedPref.getString(getString(R.string.userToken), ""))
                    .url(getUser)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    String json = responseBody.string();
                    JSONObject root = new JSONObject(json);
                    User user = new User();
                    user.setUserFirstName(root.getString("userFirstName"));
                    user.setUserLastName(root.getString("userLastName"));
                    user.setUserEmail(root.getString("userEmail"));
                    user.setUserCity(root.getString("userCity"));
                    user.setUserGender(root.getString("userGender"));

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
}
