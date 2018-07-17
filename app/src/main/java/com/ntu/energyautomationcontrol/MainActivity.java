package com.ntu.energyautomationcontrol;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.github.stephenvinouze.materialnumberpickercore.MaterialNumberPicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Listener to send value to DB every time the value on the numberPicker is changed
        final NumberPicker targetTemperaturePicker = (NumberPicker) findViewById(R.id.currentTemperaturePicker);

        targetTemperaturePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newValue) {
                firebaseAuth = FirebaseAuth.getInstance();
                final String UID = firebaseAuth.getUid();
                databaseReference.child("users").child(UID).child("targetTemperature").setValue(newValue);
            }
        });
    }

    private void sendMessageoToFirebase(View view)
    {
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder("152140682888" + "@gcm.googleapis.com")
                .setMessageId(Integer.toString(1))
                .addData("my_message", "Hello World")
                .addData("my_action","SAY_HELLO")
                .build());
    }

}
