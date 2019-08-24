package com.uncc.inclass01.utilities;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Auth {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String LOG_TAG = "Auth Class";

    public Auth(){
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public Boolean getAuthStatus() {
        Log.d(LOG_TAG, "current user: "+currentUser);
        return !(currentUser==null);
    }

    public String loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password);
        return "";
    }

    public String getCurrentUserID(){
        return  currentUser.getUid();
    }
}
