package com.uncc.inclass01;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button loginButton;
    private EditText emailText, passwordText;
    private TextView goToCreateAccount;
    private String email, password;
    private String TAG = "Login Tag";

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
    }

    @Override
    public void onClick(View view) {
        Log.e(TAG, view.getId()+": is the id of clicked unit");
        switch (view.getId()){
            case R.id.login_button:{
                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                // check if email and passwords are filled
                // password is atleast 6 char long
                if (email.isEmpty()||password.isEmpty()||!email.matches("")||password.length()<6){
                    // notify user about that
                }
                // login user -> create error use cases
                break;
            }
            case R.id.go_to_create_account:{
                // go to create account
                this.startActivity(new Intent(Login.this, CreateAccount.class));
                break;
            }
        }

    }
}
