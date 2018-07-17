package com.ntu.energyautomationcontrol;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;

public class AuthenticationHandler {

    private FirebaseAuth userAuthentication;
    public FirebaseAuth getUserAuthentication() { return userAuthentication; }
    public void setUserAuthentication(FirebaseAuth userAuthentication) { this.userAuthentication = userAuthentication; }

    private void checkAuthStatus()
    {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = userAuthentication.getCurrentUser();
        //TODO updateUI(currentUser);
    }
/*
    private void createAccount(String email, String password)
    {
        userAuthentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = userAuthentication.getCurrentUser();
                            //TODO updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //TODO updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void LoginUser(String email, String password)
    {
        userAuthentication.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = userAuthentication.getCurrentUser();
                            //TODO updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            //Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //TODO updateUI(null);
                        }

                        // ...
                    }
                });
    }
    */
}
