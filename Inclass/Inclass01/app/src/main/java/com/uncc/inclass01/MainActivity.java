package com.uncc.inclass01;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.uncc.inclass01.ui.dashboard.Dashboard;
import com.uncc.inclass01.utilities.Auth;

public class MainActivity extends AppCompatActivity {

    private void redirectOnAuth(boolean authStatus){
        Intent i;
        if(authStatus){
            i = new Intent(MainActivity.this, Dashboard.class);
        }else{
            i = new Intent(MainActivity.this, Login.class);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Launcher);
        setContentView(R.layout.activity_main);
        Auth auth = new Auth();
        redirectOnAuth(auth.getAuthStatus());
    }

    private void startActivity(int code, Class<?> cls) {
        Intent i = new Intent(MainActivity.this, cls);
        startActivityForResult(i, code);
    }
}
