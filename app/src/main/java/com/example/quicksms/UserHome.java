package com.example.quicksms;



import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
    private ImageButton logoutBtn;
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
        logoutBtn = (ImageButton) findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent loginActivity =new Intent(UserHome.this,MainActivity.class);
                startActivity(loginActivity);
            }
        });
        emailField.setText(email);
        phoneInputFiled.setText(phone);

        projectUtils = new ProjectUtils(this);
        projectUtils.checkAllPermission();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        System.out.println();
        System.out.println(user.getEmail());
        System.out.println(user.getDisplayName());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mDatabaseUser = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    // Get Post object and use the values to update the UI
                    String email = String.valueOf(dataSnapshot.child("email").getValue(String.class));
                    String phone = String.valueOf(dataSnapshot.child("phone").getValue(String.class));
                    System.out.println("======firebase read "+email);
                    emailField.setText(email);
                    phoneInputFiled.setText(phone);
                }
                catch (Exception e){
                    System.out.println("======firebase read  no value stored");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("====>", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabaseUser.addValueEventListener(postListener);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailField.setText(emailField.getText());
                editor.putString(userPhoneKay, String.valueOf(emailField.getText()));
                editor.putString(userEmailKay, String.valueOf(phoneInputFiled.getText()));
                phoneInputFiled.setText(phoneInputFiled.getText());
                editor.apply();
                mDatabaseUser.child("email").setValue(String.valueOf(emailField.getText()));
                mDatabaseUser.child("phone").setValue(String.valueOf(phoneInputFiled.getText()));

            }
        });


    }
}