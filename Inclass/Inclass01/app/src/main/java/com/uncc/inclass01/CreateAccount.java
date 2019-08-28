package com.uncc.inclass01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CreateAccount extends AppCompatActivity {

    private EditText signupEmail, signupPassword, signupVerifyPassword;
    private Button createAccountOne;
    private String email, password, verifyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        createAccountOne = (Button) findViewById(R.id.create_account1btn);
        signupEmail = (EditText) findViewById(R.id.signup_email);
        signupPassword = (EditText) findViewById(R.id.signup_password);
        signupVerifyPassword = (EditText) findViewById(R.id.signup_verify_password);

        createAccountOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                email = signupEmail.getText().toString();
                password = signupPassword.getText().toString();
                verifyPassword = signupVerifyPassword.getText().toString();
                if(password.equals(verifyPassword)){
                    // go to create account 2 activity
                    Intent goToCreateAccount2 = new Intent(CreateAccount.this, CreateAccount2.class);
                    goToCreateAccount2.putExtra("email", email);
                    goToCreateAccount2.putExtra("password", password);

                    startActivity(goToCreateAccount2);
                    finish();
                }
            }
        });
    }
}
