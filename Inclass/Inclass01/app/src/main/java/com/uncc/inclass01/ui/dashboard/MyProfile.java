package com.uncc.inclass01.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
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
import com.uncc.inclass01.utilities.UserProfile;

import java.io.ByteArrayOutputStream;
import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfile extends Fragment implements android.view.View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private String TAG = "ProfileTag";
    private ImageView profilePicImg;
    private Button updateFirstName;
    private Button updateLastName;
    private Button updateGender;
    private Button updateCity;
    private ProgressBar progressBar;
    private UserProfile userProfile;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private final int CAMERA_PERMISSION_CODE = 1, CAMERA_CODE = 20;
    private Bitmap profileImage, oldProfileImage;



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
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
        }
        database  = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FrameLayout frameLayout = (FrameLayout)inflater.inflate(R.layout.fragment_my_profile, container, false);
        profilePicImg = frameLayout.findViewById(R.id.profile_pic);
        updateCity = frameLayout.findViewById(R.id.updateCity);
        updateFirstName = frameLayout.findViewById(R.id.updateFirstName);
        updateLastName = frameLayout.findViewById(R.id.updateLastName);
        Button updatePassword = frameLayout.findViewById(R.id.updatePassword);
        updateGender = frameLayout.findViewById(R.id.updateGender);
        progressBar = frameLayout.findViewById(R.id.progressBar);
        profilePicImg.setOnClickListener(this);
        updateCity.setOnClickListener(this);
        updateFirstName.setOnClickListener(this);
        updateLastName.setOnClickListener(this);
        updatePassword.setOnClickListener(this);
        updateGender.setOnClickListener(this);


        GetUserData userData = new GetUserData();
        userData.execute();
        myRef = database.getReference("userProfiles").child(new Auth().getCurrentUserID());
        return frameLayout;

    }

    @SuppressLint("StaticFieldLeak")
    private class GetUserData extends AsyncTask<Void, Void, Void>{
        private String msg;
        private boolean errorFlag = false;
        Bitmap profileImage;


        @Override
        protected Void doInBackground(Void... params) {
            Auth auth = new Auth();
            try{
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(auth.getCurrentUserEmail().replace('.', '_') + ".jpeg");
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
                        profileImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icons8_user_female_skin_type_4);
                        profilePicImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_icons8_user_female_skin_type_4));
//                        Toast.makeText(getActivity(), "Error: "+exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

//                Log.d(TAG, "Taking information for: "+auth.getCurrentUserID());
                // Read from the database
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        UserProfile userProfileProfileData = dataSnapshot.getValue(UserProfile.class);

                        Log.w(TAG, dataSnapshot.getValue().toString());
                        if (userProfileProfileData.getEmail() != null) {
//                            Log.d(TAG, "Value is: " + userProfileProfileData);
                            handleMyUI(userProfileProfileData);
                        }else{
                            errorFlag = true;
                            Log.w(TAG, "Failed to read value.");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        errorFlag = true;
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });


            }catch (Exception e){
                Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                errorFlag = true;
//                Log.d(TAG, e.getMessage());
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

    private void handleError(){
        Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
    }

    private void handleMyUI(UserProfile userProfileProfileData){
        // hide progressbar
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        this.userProfile = userProfileProfileData;
        updateFirstName.setText("First Name: "+ userProfileProfileData.getFirstName());
        updateLastName.setText("Last Name: "+ userProfileProfileData.getLastName());
        updateGender.setText("Gender: "+ userProfileProfileData.getGender());
        updateCity.setText("City: "+ userProfileProfileData.getCity());
    }

    private void handleMyImage(Bitmap profileImage){
        profilePicImg.setImageBitmap(profileImage);
        this.profileImage = profileImage;
    }

    @Override
    public void onClick(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        String title = "First Name";
        switch (view.getId()){
            case R.id.updateFirstName:{
                title = "First Name";
                Log.d(TAG, "firstName Btn success is clicked");
                input.setHint(userProfile.getFirstName());
                builder.setView(input);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "firstName Btn success is clicked");
                        updateData("firstName",input.getText().toString());
                    }
                });
                break;
            }
            case R.id.updateLastName:{
                title = "Last Name";
                input.setHint(userProfile.getLastName());
                builder.setView(input);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateData("lastName",input.getText().toString());
                    }
                });
                break;
            }
            case R.id.updateGender:{
                title = "Gender";
                final Switch genderSwitch = new Switch(getContext());
                final TextView femaleText = new TextView(getContext());
                femaleText.setText(R.string.female);
                final TextView maleText = new TextView(getContext());
                maleText.setText(R.string.male);
                final LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setPadding(40, 0, 40, 0);
                linearLayout.addView(femaleText);
                linearLayout.addView(genderSwitch);
                linearLayout.addView(maleText);
                genderSwitch.setTextOn("Male");
                genderSwitch.setTextOff("Female");
                builder.setView(linearLayout);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (genderSwitch.isChecked()){
                            updateData("gender", "male");
                        }else{
                            updateData("gender", "female");
                        }
                    }
                });
                break;
            }
            case R.id.updateCity:{
                title = "City";
                input.setHint(userProfile.getCity());
                builder.setView(input);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateData("city",input.getText().toString());
                    }
                });
                break;
            }
            case R.id.updatePassword:{
                title = "Set New Password";
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input.setHint("New Password");
                builder.setView(input);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Auth auth = new Auth();
                        FirebaseUser user = auth.getCurrentUser();
                        if (input.getText().toString().length()>6){
                            if (user != null) {
                                user.updatePassword(input.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Snackbar.make(view, "Updated your password successfully", Snackbar.LENGTH_SHORT).show();
                                        }else{
                                            Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }else{
                            Snackbar.make(view, "Please input password atleast 6 digit long", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            }
            case R.id.profile_pic:{
                // upload new profile pic
                clickPicForProfilePic();
                break;
            }
        }

        builder.setTitle(title);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void clickPicForProfilePic(){
        // intent to capture image
        if (Build.VERSION.SDK_INT>=23){
            // if permission already granted
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
                        "android.permission.CAMERA",
                        "android.permission.READ_EXTERNAL_STORAGE"};
                requestPermissions(permissions, CAMERA_PERMISSION_CODE);
            }
            else {
                // go to camera
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // check your permission
//        Log.d(TAG, "reqCode:  "+requestCode+",    grantResult:   "+grantResults[0]);
        if (requestCode==CAMERA_PERMISSION_CODE){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                // go to camera
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  check your intent
        if (requestCode==CAMERA_CODE && resultCode == RESULT_OK && data!=null && data.getExtras()!=null){
            // process data
            this.oldProfileImage = this.profileImage;
            profileImage = (Bitmap) data.getExtras().get("data");
            new UpdateProfilePic().execute();
        }
    }

    private void updateData(String key, String value){
        Log.d(TAG, "update UI is called");
        myRef.child(key).setValue(value);
        // update UI
        switch (key){
            case "firstName":{
                this.userProfile.setFirstName(value);
                updateFirstName.setText("First Name: "+value);
                break;
            }
            case "lastName":{
                this.userProfile.setLastName(value);
                updateLastName.setText("Last Name: "+value);
                break;
            }
            case "gender":{
                this.userProfile.setGender(value);
                updateGender.setText("Gender: "+value);
                break;
            }
            case "city":{
                this.userProfile.setCity(value);
                updateCity.setText("City: "+value);
                break;
            }
        }
    }

    public class UpdateProfilePic extends AsyncTask<String, Context, String> {

        StorageReference mStorageRef;
        String msg;
        boolean flag = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mStorageRef = FirebaseStorage.getInstance().getReference();
        }

        @Override
        protected String doInBackground(String... str) {
            StorageReference mountainsRef = mStorageRef.child(userProfile.getEmail().replace('.', '_')+".jpeg");
            // Get the data from an ImageView as bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            profileImage.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    flag = true;
                    msg = exception.getMessage();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    flag = false;
                    msg = "Your Profile photo uploaded successfully";
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(flag){
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }else{
                handleErrorOnUploadImage(flag, msg);
            }
        }
    }

    private void handleErrorOnUploadImage(boolean flag, String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        profilePicImg.setImageBitmap(oldProfileImage);
    }


}
