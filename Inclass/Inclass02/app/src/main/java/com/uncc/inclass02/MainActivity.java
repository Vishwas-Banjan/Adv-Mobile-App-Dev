package com.uncc.inclass02;

import android.content.Intent;
import android.os.Bundle;

import com.uncc.inclass02.ui.dashboard.Dashboard;
import com.uncc.inclass02.utilities.Auth;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private void redirectOnAuth(boolean authStatus){
        Intent i;
        if(authStatus){
            i = new Intent(MainActivity.this, Dashboard.class);
        }else{
            i = new Intent(MainActivity.this, Login.class);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
}
