package com.uncc.inclass01;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;

import com.uncc.inclass01.ui.dashboard.Dashboard;
import com.uncc.inclass01.utilities.Auth;

public class MainActivity extends AppCompatActivity {

    private void redirectOnAuth(boolean authStatus){
        if(authStatus){
            this.startActivity(new Intent(MainActivity.this, Dashboard.class));
        }else{
            this.startActivity(new Intent(MainActivity.this, Login.class));
        }
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
