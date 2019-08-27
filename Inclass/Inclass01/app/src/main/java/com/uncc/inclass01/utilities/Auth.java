package com.uncc.inclass01.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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


    public String getCurrentUserID(){
        return  currentUser.getUid();
    }

    public String getCurrentUserEmail(){
        return  currentUser.getEmail();
    }



    public boolean signOutUser(){
        try{
            mAuth.signOut();
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
