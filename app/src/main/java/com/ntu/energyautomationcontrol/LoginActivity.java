package com.ntu.energyautomationcontrol;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private void showPasswordInput()
    {
        final EditText passwordInput = (EditText) View.findViewById(R.id.passwordInput2);
        passwordInput.setVisibility(View.VISIBLE);
    }
}
