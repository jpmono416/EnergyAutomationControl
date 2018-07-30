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
    private EditText passwordInput2;
    private Button switchToRegisterButton;
    private Button switchToLoginButton;
    private Button submitLoginButton;
    Button submitRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Initialise all required interface elements
        userAuthentication = FirebaseAuth.getInstance();
        userEmailEntered = findViewById(R.id.emailInput);
        userPasswordEntered = findViewById(R.id.passwordInput);
        userPasswordRepeated = findViewById(R.id.passwordInput2);
        passwordInput2 = (EditText) findViewById(R.id.passwordInput2);
        switchToRegisterButton = (Button) findViewById(R.id.switchToRegisterButton);
        switchToLoginButton = (Button) findViewById(R.id.switchToLoginButton);
        submitLoginButton = (Button) findViewById(R.id.submitLoginButton);
        submitRegisterButton = (Button) findViewById(R.id.submitRegisterButton);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = userAuthentication.getCurrentUser();

        if(currentUser != null){
            Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }
    }

    /**
     * This method is used to create a new user into the realtime database. It checks for not null
     * fields and passwords entered match. Then it creates the user on the database.
     */
    public void createAccount(View view)
    {
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

        /**
         * This is triggered when a user registration is complete. It is a listener on the
         * database and is in charge of automatically inserting default values for the new user settings
         */
        userAuthentication.createUserWithEmailAndPassword(userEmailEntered.getText().toString().trim(), userPasswordEntered.getText().toString().trim())
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
                                myRef.child("users").child(UID).child("auto").setValue(false);
                                myRef.child("users").child(UID).child("state").setValue(false);

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                // In case of data set fail, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e) { e.printStackTrace(); }

                    }
                });
    }


    /**
     * This function is used for logging in a user. It first checks that there are no null fields
     * And then checks the values against the ones stored on Firebase's user auth.
     */
    public void loginUser(View view)
    {

        if(TextUtils.isEmpty(userEmailEntered.getText())  || TextUtils.isEmpty(userPasswordEntered.getText()))
        {
            Toast.makeText(getApplicationContext(), "Please fill in every field", Toast.LENGTH_SHORT).show();
            return;
        }

        userAuthentication.signInWithEmailAndPassword(userEmailEntered.getText().toString(), userPasswordEntered.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try
                        {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = userAuthentication.getCurrentUser();

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

    /**
     * These two methods are used for switching between the "register" and the "login" interfaces
     * It hides or shows the relevant fields and buttons at each time.
     */
    public void switchToRegister(View view)
    {
        passwordInput2.setVisibility(VISIBLE);
        switchToRegisterButton.setVisibility(INVISIBLE);
        switchToLoginButton.setVisibility(VISIBLE);
        submitLoginButton.setVisibility(INVISIBLE);
        submitRegisterButton.setVisibility(VISIBLE);
    }

    public void switchToLogin(View view)
    {
        passwordInput2.setVisibility(GONE);
        switchToRegisterButton.setVisibility(VISIBLE);
        switchToLoginButton.setVisibility(INVISIBLE);
        submitLoginButton.setVisibility(VISIBLE);
        submitRegisterButton.setVisibility(INVISIBLE);
    }
}
