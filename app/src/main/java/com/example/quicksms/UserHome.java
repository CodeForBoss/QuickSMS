package com.example.quicksms;



import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    TextInputEditText emailField,passwordField,
            repeatPasswordField,phoneInputFiled,
            verificationCodeFiled, fullNameInput,streetAddressInputField,addressLine2InputField,cityInputField,stateInputField,countryInputField
            ,zipInputField,bnEmailField,bnPhoneInputField,bnNameInput,bnAddressInputField,bnAddressLine2InputField,bnCityInputField,bnStateInputField,bnCountryInputField
            ,bnZipInputField;
    Button btnSave ;
    ProjectUtils projectUtils;
    private DatabaseReference mDatabase;
    private ImageButton logoutBtn;
    FirebaseAuth mAuth;
    private LinearLayout bnLayout,personalLayout;
    private TextView personalTextView,businessTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getPackageName(), 0);

        SharedPreferences.Editor editor = pref.edit();

        emailField =findViewById(R.id.email_input_field);
        email_input_layout =findViewById(R.id.email_input_layout);
        phone_input_layout = findViewById(R.id.phone_input_layout);
        phoneInputFiled  = findViewById(R.id.phone_input_field);
        fullNameInput = findViewById(R.id.full_name_input_field);
        streetAddressInputField = findViewById(R.id.street_address_field);
        addressLine2InputField = findViewById(R.id.address2_field);
        cityInputField = findViewById(R.id.city_filed);
        stateInputField = findViewById(R.id.state_field);
        countryInputField = findViewById(R.id.country_field);
        zipInputField = findViewById(R.id.zip_field);
        btnSave = findViewById(R.id.btn_save);
        logoutBtn = (ImageButton) findViewById(R.id.logout_btn);
        bnLayout = findViewById(R.id.bn_linear_layout);
        personalLayout = findViewById(R.id.personal_linear_layout);
        personalTextView = findViewById(R.id.ptext);
        businessTextView = findViewById(R.id.btext);
        bnEmailField = findViewById(R.id.bn_email_input_field);
        bnPhoneInputField = findViewById(R.id.bn_phone_input_field);
        bnNameInput = findViewById(R.id.bn_name_input_field);
        bnAddressInputField = findViewById(R.id.bn_address1_field);
        bnAddressLine2InputField = findViewById(R.id.bn_address2_field);
        bnCityInputField = findViewById(R.id.bn_city_filed);
        bnStateInputField = findViewById(R.id.bn_state_field);
        bnCountryInputField = findViewById(R.id.bn_country_field);
        bnZipInputField = findViewById(R.id.bn_zip_field);
        personalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalLayout.setVisibility(View.VISIBLE);
                bnLayout.setVisibility(View.GONE);
                personalTextView.setTypeface(null, Typeface.BOLD);
                businessTextView.setTypeface(null, Typeface.NORMAL);
            }
        });
        businessTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bnLayout.setVisibility(View.VISIBLE);
                personalLayout.setVisibility(View.GONE);
                businessTextView.setTypeface(null, Typeface.BOLD);
                personalTextView.setTypeface(null, Typeface.NORMAL);
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent loginActivity =new Intent(UserHome.this,MainActivity.class);
                startActivity(loginActivity);
                finish();
            }
        });

        projectUtils = new ProjectUtils(this);
        projectUtils.checkAllPermission();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mDatabaseUser = FirebaseDatabase.getInstance().getReference("users").child(user.getUid().toString());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    // Get Post object and use the values to update the UI
                    String email = String.valueOf(dataSnapshot.child("email").getValue(String.class));
                    String phone = String.valueOf(dataSnapshot.child("phone").getValue(String.class));
                    emailField.setText(email);
                    phoneInputFiled.setText(phone);
                    fullNameInput.setText(dataSnapshot.child("name").getValue(String.class));
                    streetAddressInputField.setText(dataSnapshot.child("street_address").getValue(String.class));
                    addressLine2InputField.setText(dataSnapshot.child("address_line2").getValue(String.class));
                    cityInputField.setText(dataSnapshot.child("city").getValue(String.class));
                    stateInputField.setText(dataSnapshot.child("state").getValue(String.class));
                    countryInputField.setText(dataSnapshot.child("country").getValue(String.class));
                    zipInputField.setText(dataSnapshot.child("zip").getValue(String.class));

                    // business data
                    bnEmailField.setText(dataSnapshot.child("bn_email").getValue(String.class));
                    bnPhoneInputField.setText(dataSnapshot.child("bn_phone").getValue(String.class));
                    bnNameInput.setText(dataSnapshot.child("bn_name").getValue(String.class));
                    bnAddressInputField.setText(dataSnapshot.child("bn_address").getValue(String.class));
                    bnAddressLine2InputField.setText(dataSnapshot.child("bn_address_line2").getValue(String.class));
                    bnCityInputField.setText(dataSnapshot.child("bn_city").getValue(String.class));
                    bnStateInputField.setText(dataSnapshot.child("bn_state").getValue(String.class));
                    bnCountryInputField.setText(dataSnapshot.child("bn_country").getValue(String.class));
                    bnZipInputField.setText(dataSnapshot.child("bn_zip").getValue(String.class));

                    editor.putString(AppConstant.pNameKey,fullNameInput.getText().toString());
                    editor.putString(AppConstant.pEmailKey,emailField.getText().toString());
                    editor.putString(AppConstant.pPhoneKey,phoneInputFiled.getText().toString());
                    editor.putString(AppConstant.pStreetAddress,streetAddressInputField.getText().toString());
                    editor.putString(AppConstant.pAddressLine,addressLine2InputField.getText().toString());
                    editor.putString(AppConstant.pCity,cityInputField.getText().toString());
                    editor.putString(AppConstant.pState,stateInputField.getText().toString());
                    editor.putString(AppConstant.pCountry,countryInputField.getText().toString());
                    editor.putString(AppConstant.pZipCode,zipInputField.getText().toString());

                    editor.putString(AppConstant.bNameKey,bnNameInput.getText().toString());
                    editor.putString(AppConstant.bEmailKey,bnEmailField.getText().toString());
                    editor.putString(AppConstant.bPhoneKey,bnPhoneInputField.getText().toString());
                    editor.putString(AppConstant.bStreetAddress,bnAddressInputField.getText().toString());
                    editor.putString(AppConstant.bAddressLine,bnAddressLine2InputField.getText().toString());
                    editor.putString(AppConstant.bCity,bnCityInputField.getText().toString());
                    editor.putString(AppConstant.bState,bnStateInputField.getText().toString());
                    editor.putString(AppConstant.bCountry,bnCountryInputField.getText().toString());
                    editor.putString(AppConstant.bZipCode,bnZipInputField.getText().toString());

                    editor.apply();
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

                mDatabaseUser.child("email").setValue(String.valueOf(emailField.getText()));
                mDatabaseUser.child("phone").setValue(String.valueOf(phoneInputFiled.getText()));
                mDatabaseUser.child("name").setValue(fullNameInput.getText().toString());
                mDatabaseUser.child("street_address").setValue(streetAddressInputField.getText().toString());
                mDatabaseUser.child("address_line2").setValue(addressLine2InputField.getText().toString());
                mDatabaseUser.child("city").setValue(cityInputField.getText().toString());
                mDatabaseUser.child("state").setValue(stateInputField.getText().toString());
                mDatabaseUser.child("country").setValue(countryInputField.getText().toString());
                mDatabaseUser.child("zip").setValue(zipInputField.getText().toString());

                // business data show
                mDatabaseUser.child("bn_email").setValue(String.valueOf(bnEmailField.getText()));
                mDatabaseUser.child("bn_phone").setValue(String.valueOf(bnPhoneInputField.getText()));
                mDatabaseUser.child("bn_name").setValue(bnNameInput.getText().toString());
                mDatabaseUser.child("bn_address").setValue(bnAddressInputField.getText().toString());
                mDatabaseUser.child("bn_address_line2").setValue(bnAddressLine2InputField.getText().toString());
                mDatabaseUser.child("bn_city").setValue(bnCityInputField.getText().toString());
                mDatabaseUser.child("bn_state").setValue(bnStateInputField.getText().toString());
                mDatabaseUser.child("bn_country").setValue(bnCountryInputField.getText().toString());
                mDatabaseUser.child("bn_zip").setValue(bnZipInputField.getText().toString());


                editor.putString(AppConstant.pNameKey,fullNameInput.getText().toString());
                editor.putString(AppConstant.pEmailKey,emailField.getText().toString());
                editor.putString(AppConstant.pPhoneKey,phoneInputFiled.getText().toString());
                editor.putString(AppConstant.pStreetAddress,streetAddressInputField.getText().toString());
                editor.putString(AppConstant.pAddressLine,addressLine2InputField.getText().toString());
                editor.putString(AppConstant.pCity,cityInputField.getText().toString());
                editor.putString(AppConstant.pState,stateInputField.getText().toString());
                editor.putString(AppConstant.pCountry,countryInputField.getText().toString());
                editor.putString(AppConstant.pZipCode,zipInputField.getText().toString());

                editor.putString(AppConstant.bNameKey,bnNameInput.getText().toString());
                editor.putString(AppConstant.bEmailKey,bnEmailField.getText().toString());
                editor.putString(AppConstant.bPhoneKey,bnPhoneInputField.getText().toString());
                editor.putString(AppConstant.bStreetAddress,bnAddressInputField.getText().toString());
                editor.putString(AppConstant.bAddressLine,bnAddressLine2InputField.getText().toString());
                editor.putString(AppConstant.bCity,bnCityInputField.getText().toString());
                editor.putString(AppConstant.bState,bnStateInputField.getText().toString());
                editor.putString(AppConstant.bCountry,bnCountryInputField.getText().toString());
                editor.putString(AppConstant.bZipCode,bnZipInputField.getText().toString());

                editor.apply();

            }
        });


    }
}