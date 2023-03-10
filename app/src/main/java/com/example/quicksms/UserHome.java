package com.example.quicksms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class UserHome extends AppCompatActivity {
    TextInputLayout password_input_field_layout,
            repeat_password_input_field_layout,
            email_input_layout,
            phone_input_layout,
            verification_input_layout;
    TextInputEditText emailField,passwordField,repeatPasswordField,phoneInputFiled, verificationCodeFiled;
    Button btnSave ;
    ProjectUtils projectUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getPackageName(), 0);
        String userPhoneKay="phone";
        String userEmailKay="email";
        SharedPreferences.Editor editor = pref.edit();

        String phone=pref.getString(userPhoneKay,"");
        String email=pref.getString(userEmailKay,"");
        emailField =findViewById(R.id.email_input_field);
        email_input_layout =findViewById(R.id.email_input_layout);
        phone_input_layout = findViewById(R.id.phone_input_layout);
        phoneInputFiled  = findViewById(R.id.phone_input_field);
        btnSave = findViewById(R.id.btn_save);
        emailField.setText(email);
        phoneInputFiled.setText(phone);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailField.setText(emailField.getText());
                editor.putString(userPhoneKay, String.valueOf(emailField.getText()));
                editor.putString(userEmailKay, String.valueOf(phoneInputFiled.getText()));
                phoneInputFiled.setText(phoneInputFiled.getText());
                editor.apply();
            }
        });

        projectUtils = new ProjectUtils(this);
        projectUtils.checkAllPermission();

    }
}