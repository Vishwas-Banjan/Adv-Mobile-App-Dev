package com.uncc.inclass02;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uncc.inclass02.ui.dashboard.Dashboard;
import com.uncc.inclass02.utilities.Auth;
import com.uncc.inclass02.utilities.UserProfile;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class CreateAccount2 extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth mAuth;
    private EditText signupFirstName, signupLastName, signupCity;
    private Switch signupGender;
    private String email, password, firstName, lastName, gender, city;
    private Button createAccount;
    private ImageView profilePicBtn;
    private Bitmap profileImage;
    private StorageReference mStorageRef;
    private String LOG_Account = "CreateAccount2";
    private final int CAMERA_PERMISSION_CODE = 1, CAMERA_CODE = 20;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account2);
        Intent goToCreateAccount2 = getIntent();
        email = goToCreateAccount2.getStringExtra("email");
        password = goToCreateAccount2.getStringExtra("password");
        profilePicBtn = findViewById(R.id.upload_profile_pic);
        createAccount = findViewById(R.id.signup_create_account_2);
        profilePicBtn.setOnClickListener(this);
        createAccount.setOnClickListener(this);

        signupFirstName = findViewById(R.id.signup_first_name);
        signupLastName = findViewById(R.id.signup_last_name);
        signupGender = findViewById(R.id.signup_gender);
        signupCity = findViewById(R.id.signup_city);

        Log.d(LOG_Account, "password: "+password+", email: "+email);
        if (email == null ||password == null){
            Log.d(LOG_Account, "password is null");
            startActivity(new Intent(CreateAccount2.this, CreateAccount.class));
        }

        // storage ref
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        Log.d(LOG_Account, "something is clicked");
        Log.d(LOG_Account, "createAccountClicked?= "+view.getId()+":id,  createAccount: "+R.id.create_account_2+", imagebtnID:  "+R.id.profile_pic);
        switch (view.getId()){
            case R.id.upload_profile_pic: {
                clickPicForProfilePic();
                break;
            }
            case R.id.signup_create_account_2: {
                Log.d(LOG_Account, "btn is clicked");
                createUserAccount();
                break;
            }
        }
    }

    void clickPicForProfilePic(){
        // intent to capture image
        if (Build.VERSION.SDK_INT>=23){
            // if permission already granted
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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

    void createUserAccount(){
        Log.d(LOG_Account, "running create user");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_Account, "task is successful");
                    firstName = signupFirstName.getText().toString();
                    lastName = signupLastName.getText().toString();
                    gender = signupGender.isChecked()? "male": "female";
                    city = signupCity.getText().toString();
                    UserProfile userProfile = new UserProfile(firstName, lastName, email, gender, city);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference userDbRef = database.getReference("userProfiles").child(new Auth().getCurrentUserID());
                    userDbRef.setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                if (profileImage == null){
                                    profileImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
                                }
                                new UploadProfilePic().execute();
                                startActivity(new Intent(CreateAccount2.this, Dashboard.class));
                                finish();
                            }else{
                                createAccountExceptionHandling(task);
                            }
                        }
                    });

                } else {
                    Log.d(LOG_Account, "unsuccessful task");
                    createAccountExceptionHandling(task);
                }
            }
        });
    }


    private void createAccountExceptionHandling(Task task){
        Toast.makeText(getApplicationContext(),  task.getException()+"", Toast.LENGTH_SHORT).show();
        Log.d(LOG_Account, "failed user creation : "+task.getException());
//        startActivity(new Intent(CreateAccount2.this, CreateAccount.class));
    }


    private class UploadProfilePic extends AsyncTask<String, String, String>{
        private String msg;
        private boolean flag = false;


        @Override
        protected String doInBackground(String... strings) {
            StorageReference mountainsRef = mStorageRef.child(email.replace('.', '_')+".jpeg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            profileImage.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    msg = exception.getMessage();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    msg = "Your Profile photo uploaded successfully";
                }
            });

            flag = true;
            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (flag){
                Snackbar.make(findViewById(R.id.create_account_2), msg, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // check your permission
        Log.d(LOG_Account, "reqCode:  "+requestCode+",    grantResult:   "+grantResults[0]);
        if (requestCode==CAMERA_PERMISSION_CODE){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                // go to camera
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  check your intent
        if (requestCode==CAMERA_CODE && resultCode == RESULT_OK && data!=null && data.getExtras()!=null){
            // process data
            profileImage = (Bitmap) data.getExtras().get("data");
            // compress the profile image
            profilePicBtn.setImageBitmap(profileImage);
        }
    }
}
