package com.ntu.energyautomationcontrol;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth userAuthentication;
    private EditText userEmailEntered;
    private EditText userPasswordEntered;
    private EditText userPasswordRepeated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userAuthentication = FirebaseAuth.getInstance();

        userEmailEntered = findViewById(R.id.emailInput);
        userPasswordEntered = findViewById(R.id.passwordInput);
        userPasswordRepeated = findViewById(R.id.passwordInput2);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = userAuthentication.getCurrentUser();
        //TODO updateUI(currentUser);

        if(currentUser != null){
            Intent skipLog = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(skipLog);
            finish();
        }
    }

    public void createAccount(View view)
    {
        /*Toast.makeText(getApplicationContext(), "Came into the function", Toast.LENGTH_SHORT).show();*/

        if(TextUtils.isEmpty(userEmailEntered.getText())  || TextUtils.isEmpty(userPasswordEntered.getText()) || TextUtils.isEmpty(userPasswordRepeated.getText()))
        {
            Toast.makeText(getApplicationContext(), "Please fill in every field", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!userPasswordEntered.getText().toString().equals(userPasswordRepeated.getText().toString()))
        {
            Toast.makeText(getApplicationContext(), "Please make sure passwords match", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = userEmailEntered.getText().toString().trim();
        String passw = userPasswordEntered.getText().toString().trim();


        userAuthentication.createUserWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try
                        {
                            if (task.isSuccessful()) {
                                // Sign in success, create some default values and send user to main activity
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = userAuthentication.getCurrentUser();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference();
                                final String UID = userAuthentication.getUid();
                                myRef.child("users").child(UID).child("currentTemperature").setValue(25);
                                myRef.child("users").child(UID).child("targetTemperature").setValue(25);

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e) { e.printStackTrace(); }

                    }
                });
    }


    public void loginUser(View view)
    {

        if(TextUtils.isEmpty(userEmailEntered.getText())  || TextUtils.isEmpty(userPasswordEntered.getText()))
        {
            Toast.makeText(getApplicationContext(), "Please fill in every field", Toast.LENGTH_SHORT).show();
            return;
        }

        userAuthentication.signInWithEmailAndPassword(userEmailEntered.getText().toString(), userPasswordEntered.getText().toString())
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try
                        {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = userAuthentication.getCurrentUser();

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e) { e.printStackTrace(); }
                    }
                });
    }

    public void switchToRegister(View view)
    {
        EditText passwordInput2 = (EditText) findViewById(R.id.passwordInput2);
        Button switchToRegisterButton = (Button) findViewById(R.id.switchToRegisterButton);
        Button switchToLoginButton = (Button) findViewById(R.id.switchToLoginButton);

        passwordInput2.setVisibility(VISIBLE);
        switchToRegisterButton.setVisibility(INVISIBLE);
        switchToLoginButton.setVisibility(VISIBLE);
    }

    public void switchToLogin(View view)
    {
        EditText passwordInput2 = (EditText) findViewById(R.id.passwordInput2);
        Button switchToRegisterButton = (Button) findViewById(R.id.switchToRegisterButton);
        Button switchToLoginButton = (Button) findViewById(R.id.switchToLoginButton);


        passwordInput2.setVisibility(GONE);
        switchToRegisterButton.setVisibility(VISIBLE);
        switchToLoginButton.setVisibility(INVISIBLE);
    }
}
