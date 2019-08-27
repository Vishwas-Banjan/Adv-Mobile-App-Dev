package com.uncc.inclass01.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uncc.inclass01.R;
import com.uncc.inclass01.utilities.Auth;
import com.uncc.inclass01.utilities.User;

import java.io.ByteArrayOutputStream;
import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1, TAG = "ProfileTag";
    private StorageReference mStorageRef;
    private ImageView profilePicImg;
    private Button updatePassword, updateFirstName, updateLastName, updateGender, updateCity;
    // Write a message to the database
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;



    public MyProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MyProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfile newInstance(String param1) {
        MyProfile fragment = new MyProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FrameLayout frameLayout = (FrameLayout)inflater.inflate(R.layout.fragment_my_profile, container, false);
        profilePicImg = frameLayout.findViewById(R.id.profile_pic);
        updateCity = frameLayout.findViewById(R.id.updateCity);
        updateFirstName = frameLayout.findViewById(R.id.updateFirstName);
        updateLastName = frameLayout.findViewById(R.id.updateLastName);
        updatePassword = frameLayout.findViewById(R.id.updatePassword);
        updateGender = frameLayout.findViewById(R.id.updateGender);
        progressBar = frameLayout.findViewById(R.id.progressBar);

        GetUserData userData = new GetUserData();
        userData.execute();

        updateCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileDialog editProfileDialog = new EditProfileDialog();
                Bundle data = new Bundle();
                data.putString("profile_info", "city");
                editProfileDialog.setArguments(data);
                if (getFragmentManager() != null) {
                    editProfileDialog.show(getFragmentManager(), "DialogTAG");
                }
            }
        });

        // Inflate the layout for this fragment
        return frameLayout;

    }

    private class GetUserData extends AsyncTask<Void, Void, Void>{
        private String msg;
        private boolean errorFlag = false;
        Bitmap profileImage;


        @Override
        protected Void doInBackground(Void... params) {
            Auth auth = new Auth();
            try{
                mStorageRef = FirebaseStorage.getInstance().getReference(auth.getCurrentUserEmail().replace('.','_')+".jpeg");
                final File localFile = File.createTempFile("profilePic", "jpeg");

                mStorageRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Successfully downloaded data to local file
                                profileImage = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                profilePicImg.setImageBitmap(profileImage);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle failed download
                        Toast.makeText(getActivity(), "Error: "+exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                Log.d(TAG, "Taking information for: "+auth.getCurrentUserID());

//                DatabaseReference myRef =  database.getReference().child("userProfiles").child(auth.getCurrentUserID());
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("userProfiles").child(new Auth().getCurrentUserID());

                // Read from the database
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
//                        User userProfileData = dataSnapshot.getValue(User.class);
//
//                        Log.w(TAG, dataSnapshot.getValue().toString());
//                        if (userProfileData.getEmail() != null) {
//                            Log.d(TAG, "Value is: " + userProfileData);
//                            handleMyUI(userProfileData);
//                        }else{
//                            errorFlag = true;
//                            Log.w(TAG, "Failed to read value.");
//                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        errorFlag = true;
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });


            }catch (Exception e){
//                Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                errorFlag = true;
                Log.d(TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if (errorFlag){
                // error happen
                handleError();
            }else{
                if (profileImage!=null){
                    handleMyImage(profileImage);
                }

            }
        }
    }

    private void handleMyUI(User userProfileData){
        // hide progressbar
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        userProfileData.printData();
        updateFirstName.setText(userProfileData.getFirstName());
        updateLastName.setText(userProfileData.getLastName());
        updateGender.setText(userProfileData.getGender());
        updateCity.setText(userProfileData.getCity());
    }

    private void handleMyImage(Bitmap profileImage){
        profilePicImg.setImageBitmap(profileImage);
    }

    private void handleError(){

    }
}
