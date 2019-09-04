package com.uncc.inclass01;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText emailText, passwordText;
    private TextView goToCreateAccount, forgotPassword;
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
        Button loginButton = findViewById(R.id.login_button);
        emailText = findViewById(R.id.login_email);
        passwordText = findViewById(R.id.login_password);
        goToCreateAccount = findViewById(R.id.go_to_create_account);
        forgotPassword = findViewById(R.id.forgotPassword);
        loginButton.setOnClickListener(this);
        goToCreateAccount.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.login_button:{
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
                break;
            }
            case R.id.go_to_create_account:{
                // go to create account
                startActivity(new Intent(Login.this, CreateAccount.class));
                break;
            }
            case R.id.forgotPassword:{
                // enter email
                AlertDialog.Builder forgotPasswordDialog = new AlertDialog.Builder(this);
                View forgotPassView = getLayoutInflater().inflate(R.layout.fragment_ask_for_data, null, false);
                EditText input = forgotPassView.findViewById(R.id.input);
                forgotPasswordDialog.setTitle("Enter Your Email: ");
                input.setHint("Email");
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                forgotPasswordDialog.setView(forgotPassView);
                forgotPasswordDialog.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // forgotPassword
                        Task resetPassword = mAuth.sendPasswordResetEmail(email);
                        if (resetPassword.isSuccessful()){
                            Snackbar.make(view, "We've Sent you a mail to reset your password", Snackbar.LENGTH_LONG).show();
                        }else{
                            Snackbar.make(view, resetPassword.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                        dialogInterface.cancel();
                    }
                });
                forgotPasswordDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                forgotPasswordDialog.show();
                break;
            }
        }

    }
}
