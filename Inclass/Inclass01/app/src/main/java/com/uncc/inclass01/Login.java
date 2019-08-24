package com.uncc.inclass01;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.uncc.inclass01.ui.dashboard.Dashboard;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AppConstant.DASHBOARD_CODE, Dashboard.class);
            }
        });

    }

    private void startActivity(int code, Class<?> cls) {
        Intent i = new Intent(Login.this, cls);
        startActivityForResult(i, code);
    }

}
