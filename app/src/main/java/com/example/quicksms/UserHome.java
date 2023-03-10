package com.example.quicksms;



import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHome extends AppCompatActivity {
    TextInputLayout password_input_field_layout,
            repeat_password_input_field_layout,
            email_input_layout,
            phone_input_layout,
            verification_input_layout;
    TextInputEditText emailField,passwordField,repeatPasswordField,phoneInputFiled, verificationCodeFiled;
    Button btnSave ;
    ProjectUtils projectUtils;
    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;
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

//        projectUtils = new ProjectUtils(this);
//        projectUtils.checkAllPermission();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        System.out.println();
        System.out.println(user.getEmail());
        System.out.println(user.getDisplayName());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mDatabaseUser = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());
        mDatabaseUser.child("email").setValue("mdjubayer247@gmail.com");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String email = String.valueOf(dataSnapshot.child("email").getValue(String.class));
                System.out.println("======firebase read "+email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("====>", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabaseUser.addValueEventListener(postListener);


    }
}