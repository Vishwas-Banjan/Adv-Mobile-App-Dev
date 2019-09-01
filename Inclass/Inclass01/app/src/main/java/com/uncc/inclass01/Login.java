package com.uncc.inclass01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.uncc.inclass01.ui.dashboard.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private Button loginButton;
    private EditText emailText, passwordText;
    private TextView goToCreateAccount;
    private String email, password;
    private String TAG = "Login Tag";
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.login_button);
        emailText = (EditText) findViewById(R.id.login_email);
        passwordText = (EditText) findViewById(R.id.login_password);
        goToCreateAccount = (TextView) findViewById(R.id.go_to_create_account);

        goToCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, view.getId()+": is the id of clicked unit");
                Login.this.startActivity(new Intent(Login.this, CreateAccount.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                // check if email and passwords are filled
                // password is atleast 6 char long
                if (email.isEmpty()||password.isEmpty()||password.length()<6){
                    // notify user about that
                    Snackbar.make(view, R.string.invalid_login, Snackbar.LENGTH_LONG).show();
                }else{
                    // logging in
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "login successful: "+task.getException());
                            if(task.isSuccessful()){
//                                Log.d(TAG, "login successful: "+task.getException());
                                Login.this.startActivity(new Intent(Login.this, Dashboard.class));
                                finish();
                            }else{
                                Snackbar.make( view, R.string.invalid_login, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
//
//    @Override
//    public void onClick(final View view) {
//        Log.e(TAG, view.getId()+": is the id of clicked unit");
//        switch (view.getId()){
//            case R.id.login_button:{
//                email = emailText.getText().toString();
//                password = passwordText.getText().toString();
//                // check if email and passwords are filled
//                // password is atleast 6 char long
//                if (email.isEmpty()||password.isEmpty()||!email.matches("")||password.length()<6){
//                    // notify user about that
//                    Snackbar.make(view, R.string.invalid_login, Snackbar.LENGTH_LONG).show();
//                }else{
//                    // logging in
//                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            Log.d(TAG, "login successful: "+task.getException());
//                            if(task.isSuccessful()){
////                                Log.d(TAG, "login successful: "+task.getException());
//                                Login.this.startActivity(new Intent(Login.this, Dashboard.class));
//                            }else{
//                                Snackbar.make(view, R.string.invalid_login, Snackbar.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//                }
//                // login user -> create error use cases
//                break;
//            }
//            case R.id.go_to_create_account:{
//                // go to create account
//                startActivity(new Intent(Login.this, CreateAccount.class));
//                break;
//            }
//        }
//
//    }
}
