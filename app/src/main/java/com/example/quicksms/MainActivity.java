package com.example.quicksms;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // code to post/handler request for permission
    public final static int REQUEST_CODE = -1010101;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    TextInputEditText emailField,passwordField,repeatPasswordField,phoneInputFiled, verificationCodeFiled;
    TextView text_email_phone_option;
    Button continueButton;
    SignInButton googleSigninBtn;


    TextInputLayout password_input_field_layout,
            repeat_password_input_field_layout,
                    email_input_layout,
                    phone_input_layout,
            verification_input_layout
                ;

    boolean userExits =false;
    boolean userSigningUp =false;
    boolean userSigningIn =false;
    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 9001;
    private static final int SL_OPTION_PHONE = 1111;
    private static final int SL_OPTION_EMAIL = 3333;

    private int selectedOption =SL_OPTION_EMAIL;

    private boolean onCodeSent=false;
    private String mVerificationId;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id_s))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        mAuth = FirebaseAuth.getInstance();
        emailField =findViewById(R.id.email_input_field);
        passwordField =findViewById(R.id.password_input_field);
        repeatPasswordField  =findViewById(R.id.repeat_password_input_field);
        continueButton = findViewById(R.id.continue_button);
        googleSigninBtn =findViewById(R.id.sign_in_button);
        password_input_field_layout = findViewById(R.id.password_input_field_layout);
        repeat_password_input_field_layout = findViewById(R.id.repeat_password_input_field_layout);
        email_input_layout =findViewById(R.id.email_input_layout);
        phone_input_layout = findViewById(R.id.phone_input_layout);
        text_email_phone_option =findViewById(R.id.text_email_phone_option);
        phoneInputFiled  = findViewById(R.id.phone_input_field);
        verification_input_layout = findViewById(R.id.verification_input_layout);
        verificationCodeFiled = findViewById(R.id.verification_input_field);
        text_email_phone_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedOption==SL_OPTION_PHONE){
                    text_email_phone_option.setText("Use phone instead");
                    email_input_layout.setVisibility(View.VISIBLE);
                    phone_input_layout.setVisibility(View.GONE);
                    selectedOption =SL_OPTION_EMAIL;
                } else{
                    selectedOption =SL_OPTION_PHONE;
                    text_email_phone_option.setText("Use email instead");
                    email_input_layout.setVisibility(View.GONE);
                    phone_input_layout.setVisibility(View.VISIBLE);
                }
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedOption == SL_OPTION_PHONE && onCodeSent){
                    String verificationCode = String.valueOf(verificationCodeFiled.getText());
                    verifyPhoneNumberWithCode(mVerificationId,verificationCode);
                }
                else if(selectedOption==SL_OPTION_PHONE){
                    String phone_number = String.valueOf(phoneInputFiled.getText());
                    startPhoneNumberVerification(phone_number);
                }

                else if(!userSigningUp && !userSigningIn) {
                    checkUser();
                    Toast.makeText(getApplicationContext(),"===>checking user",Toast.LENGTH_LONG).show();
                } else if(userSigningUp){
                    String email = String.valueOf(emailField.getText());
                    String password = String.valueOf(passwordField.getText());
                    String repeat_password = String.valueOf(repeatPasswordField.getText());
                    Toast.makeText(getApplicationContext(),"===>Signing up",Toast.LENGTH_LONG).show();
                    if(password.equals(repeat_password)){
                        createUser(email,password);
                    }
                    else{
                        repeat_password_input_field_layout.setErrorEnabled(true);
                        repeat_password_input_field_layout.setError("Password not matched");
                        System.out.println("password did not matched");
                    }

                }else if(userSigningIn){
                    String email = String.valueOf(emailField.getText());
                    String password = String.valueOf(passwordField.getText());
                    String repeat_password = String.valueOf(repeatPasswordField.getText());
                    Toast.makeText(getApplicationContext(),"===>Signing up",Toast.LENGTH_LONG).show();
                    signinUser(email,password);

                    Toast.makeText(getApplicationContext(),"===>Signing IN",Toast.LENGTH_LONG).show();

                }

            }
        });



        googleSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



        new Thread(new Runnable() {
            @Override
            public void run() {
           FirebaseUser user =mAuth.getCurrentUser();
                if(user!=null)
                if(user.isEmailVerified()){
               updateUI(user);
           }else {
               verifyEmailAddress();
           }

            }
        }).start();

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                password_input_field_layout.setEnabled(false);
                verification_input_layout.setVisibility(View.VISIBLE);
                onCodeSent=true;
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        checkAllPermission();
      //  checkAllPermission();
        // [END phone_auth_callbacks]
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser);
        }
        //checkAllPermission();
    }

    @Override
    public void onResume() {
     //
        super.onResume();


    }

    private void reload() {
    }

    private void createUser(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent userHome = new Intent(MainActivity.this,UserHome.class);
        startActivity(userHome);
    }

    private void verifyEmailAddress(){
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Re-enable button
                      //  findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void signinUser(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    private void checkUser(){
        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
        }
        else {
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()){
                                boolean check =!task.getResult().getSignInMethods().isEmpty();
                                if (!check){
                                    userExits=false;
                                    userSigningIn =false;
                                    userSigningUp =true;
                                    password_input_field_layout.setVisibility(View.VISIBLE);
                                    repeat_password_input_field_layout.setVisibility(View.VISIBLE);
                                    continueButton.setText("Sign up");
                                }
                                else {
                                    password_input_field_layout.setVisibility(View.VISIBLE);
                                    continueButton.setText("Sign In");
                                    userExits=true;
                                    userSigningUp =false;
                                    userSigningIn =true;
                                    Toast.makeText(getApplicationContext(),"email already exst",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }





//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
//                firebaseAuthWithGoogle(account.getIdToken());
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
//            }
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
    //    if (requestCode == RC_SIGN_IN) {
            // When request code is equal to 100 initialize task
            try {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful initialize string
                String s = "Google sign in successful";
                // Display Toast
                displayToast(s);
                // Initialize sign in account

                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        // Check credential
                        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Check condition
                                if (task.isSuccessful()) {
                                    // When task is successful redirect to profile activity display Toast
                                    startActivity(new Intent(MainActivity.this, UserHome.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    displayToast("Firebase authentication successful");
                                } else {
                                    // When task is unsuccessful display Toast
                                    displayToast("Authentication Failed :" + task.getException().getMessage());
                                }
                            }
                        });
                    }
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
       // }
    }
    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user);
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
        // [END verify_with_code]
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        //checkAllPermission();
    }


    public void checkAllPermission(){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.WRITE_SETTINGS,
                android.Manifest.permission.WRITE_SECURE_SETTINGS
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        if (!Settings.canDrawOverlays(this)) {   //Android M Or Over
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
        if(!Settings.System.canWrite(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }

        if(!Settings.System.canWrite(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }


        checkAccessibilityPermission();



//        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//        intent.setData(Uri.parse("package:com.example.quicksms"));
//        startActivity(intent);

//        try{
//            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//            startActivity(intent);
////            Intent intent1 = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
////            intent1.setData(Uri.parse("package:com.example.quicksms"));
////            startActivity(intent1);
////            Settings.Secure.putString(getContentResolver(),
////                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "com.example.quicksms/ChatHead");
////            Settings.Secure.putString(getContentResolver(),
////                    Settings.Secure.ACCESSIBILITY_ENABLED, "1");
//        }catch (Exception e){
//            System.out.println(e);
//        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // method to check is the user has permitted the accessibility permission
    // if not then prompt user to the system's Settings activity
    public boolean checkAccessibilityPermission () {
        int accessEnabled = 0;
        try {
            accessEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessEnabled == 0) {
            // if not construct intent to request permission
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // request permission via start activity for result
            startActivity(intent);
            return false;
        } else {
            return true;
        }
    }

}


