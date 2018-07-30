package com.ntu.energyautomationcontrol;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.flags.impl.SharedPreferencesFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Boolean autoHeating;
    private Boolean heatingState;
    private ImageView on_off_image;
    private NumberPicker targetTemperaturePicker;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Initialise the variables needed
        on_off_image = (ImageView) findViewById(R.id.on_off_image);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        targetTemperaturePicker = (NumberPicker) findViewById(R.id.currentTemperaturePicker);
        firebaseAuth = FirebaseAuth.getInstance();
        UID  = firebaseAuth.getUid();

        /**
         * Add a value change listener that will update the interface every time any of the values
         * on the database change due to the action of the IoT device pushing data.
         */
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Get real-time values from database
                final Integer liveCurrentTemperature = ((Long) dataSnapshot.child("users").child(UID).child("currentTemperature").getValue()).intValue();
                final Integer liveTargetTemperature  = ((Long) dataSnapshot.child("users").child(UID).child("targetTemperature").getValue()).intValue();
                autoHeating = (Boolean) dataSnapshot.child("users").child(UID).child("auto").getValue();
                heatingState = (Boolean) dataSnapshot.child("users").child(UID).child("state").getValue();

                //Update interface accordingly
                if(autoHeating) { changeImage(R.mipmap.on_off_icon_grey_foreground); }
                else
                {
                    if(heatingState) { changeImage(R.mipmap.on_off_icon_blue_foreground); }
                    else { changeImage(R.mipmap.on_off_icon_foreground); }
                }

                targetTemperaturePicker.setValue(liveTargetTemperature);

                String message = liveCurrentTemperature.toString() + " " +
                        (Math.abs(liveCurrentTemperature) == 1 ? getText(R.string.degree) : getText(R.string.degrees));

                ((TextView) findViewById(R.id.currentTemperatureText)).setText(message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        /**
         * This bit adds a value change listener that posts the new target temperature value to the
         * database every time the slider is modified
         */

        targetTemperaturePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newValue) {
                firebaseAuth = FirebaseAuth.getInstance();
                databaseReference.child("users").child(UID).child("targetTemperature").setValue(newValue);
            }
        });

    }

    // This is a reusable method for changing the on/off button image
    private void changeImage(int imageSource) { this.on_off_image.setImageResource(imageSource); }

    /**
     * This code was going to be used for realtime messaging between devices but due to some
     * limitations on the Raspberri Pi side we decided to go for a loop that checks values over a
     * certain period of time instead of accessing it real time as it is needed.
     */
    private void sendMessageoToFirebase(View view)
    {
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder("152140682888" + "@gcm.googleapis.com")
                .setMessageId(Integer.toString(1))
                .addData("my_message", "Hello World")
                .addData("my_action","SAY_HELLO")
                .build());
    }

    /**
     * This method pushes the state and mode of the heating to the database, it is executed when
     * any of the values change
     */
    private void updateFirebaseVariables() {

        databaseReference.child("users").child(UID).child("auto").setValue(this.autoHeating);
        databaseReference.child("users").child(UID).child("state").setValue(this.heatingState);
    }

    /**
     * These methods are executed when the mode buttons are clicked.
     * They change the variables and interface, then push to the database
     */
    public void manualClicked(View view)
    {
        if(!this.autoHeating) { return; }
        this.autoHeating = false;
        this.heatingState = false;
        changeImage(R.mipmap.on_off_icon_foreground);
        updateFirebaseVariables();
    }

    public void autoClicked(View view)
    {
        if(this.autoHeating) { return; }
        this.autoHeating = true;
        this.heatingState = false;
        changeImage(R.mipmap.on_off_icon_grey_foreground);
        updateFirebaseVariables();
    }

    /**
     * This method is executed when the on/off button is clicked. It applies some logic as to
     * how to behave and finally pushes to the database.
     */
    public void onOffClicked(View view)
    {
        if(!this.heatingState || this.autoHeating)
        {
            this.heatingState = true;
            this.autoHeating = false;
            changeImage(R.mipmap.on_off_icon_blue_foreground);
        }
        else
        {
            this.heatingState = false;
            changeImage(R.mipmap.on_off_icon_foreground);
        }
        updateFirebaseVariables();
    }

    // This method is used for the user log off
    public void signOutUser(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
