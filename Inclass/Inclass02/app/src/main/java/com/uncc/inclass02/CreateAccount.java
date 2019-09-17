package com.uncc.inclass02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

public class CreateAccount extends AppCompatActivity {

    private EditText signupEmail, signupPassword, signupVerifyPassword;
    private String email, password, verifyPassword;
    private TextView goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Button createAccountOne = findViewById(R.id.create_account1btn);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupVerifyPassword = findViewById(R.id.signup_verify_password);
        goToLogin = findViewById(R.id.go_to_login);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateAccount.this, Login.class));
            }
        });

        createAccountOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                email = signupEmail.getText().toString();
                password = signupPassword.getText().toString();
                verifyPassword = signupVerifyPassword.getText().toString();
                if (password.equals(verifyPassword) && password.length() >= 6 && !email.isEmpty()) {
                    // go to create account 2 activity
                    Intent goToCreateAccount2 = new Intent(CreateAccount.this, CreateAccount2.class);
                    goToCreateAccount2.putExtra("email", email);
                    goToCreateAccount2.putExtra("password", password);
                    startActivity(goToCreateAccount2);
                    finish();
                } else {
                    Snackbar.make(view, "Please fill in the details and make sure that password is atleast 6 characters long", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}
