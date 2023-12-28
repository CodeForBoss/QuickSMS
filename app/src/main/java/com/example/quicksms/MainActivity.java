package com.example.quicksms;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;


import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // code to post/handler request for permission
    public final static int REQUEST_CODE = -1010101;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int SL_OPTION_PHONE = 1111;
    private static final int SL_OPTION_EMAIL = 3333;
    TextInputEditText emailField, passwordField, repeatPasswordField, phoneInputFiled, verificationCodeFiled;
    TextView text_email_phone_option,forgot_password_button;
    Button continueButton;
    SignInButton googleSigninBtn;
    TextInputLayout password_input_field_layout,
            repeat_password_input_field_layout,
            email_input_layout,
            phone_input_layout,
            verification_input_layout;
    boolean userExits = false;
    boolean userSigningUp = false;
    boolean userSigningIn = false;
    GoogleSignInOptions gso;
    ProjectUtils projectUtils;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int selectedOption = SL_OPTION_EMAIL;
    private boolean onCodeSent = false;
    private String mVerificationId;

    private ProgressBar progressBar;

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            Log.d(TAG, "onVerificationCompleted:" + credential);
            signInWithPhoneAuthCredential(credential);
            progressBar.setVisibility(View.GONE);
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            progressBar.setVisibility(View.GONE);
            Log.w(TAG, "onVerificationFailed", e);
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(getApplicationContext(),"Invalid",Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else {
                //Toast.makeText(this,"Invalid Verification Code",Toast.LENGTH_SHORT).show();
            }
            // Show a message and update the UI
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d(TAG, "onCodeSent:" + verificationId);
            password_input_field_layout.setEnabled(false);
            verification_input_layout.setVisibility(View.VISIBLE);
            onCodeSent = true;
            mVerificationId = verificationId;
            progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id_s))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        emailField = findViewById(R.id.email_input_field);
        passwordField = findViewById(R.id.password_input_field);
        repeatPasswordField = findViewById(R.id.repeat_password_input_field);
        continueButton = findViewById(R.id.continue_button);
        googleSigninBtn = findViewById(R.id.sign_in_button);
        password_input_field_layout = findViewById(R.id.password_input_field_layout);
        repeat_password_input_field_layout = findViewById(R.id.repeat_password_input_field_layout);
        email_input_layout = findViewById(R.id.email_input_layout);
        phone_input_layout = findViewById(R.id.phone_input_layout);
        text_email_phone_option = findViewById(R.id.text_email_phone_option);
        phoneInputFiled = findViewById(R.id.phone_input_field);
        verification_input_layout = findViewById(R.id.verification_input_layout);
        verificationCodeFiled = findViewById(R.id.verification_input_field);
        progressBar = findViewById(R.id.progressbar);
        forgot_password_button = findViewById(R.id.forgot_password);
        text_email_phone_option.setOnClickListener(v -> {
            if (selectedOption == SL_OPTION_PHONE) {
                text_email_phone_option.setText(R.string.use_phone_instead);
                email_input_layout.setVisibility(View.VISIBLE);
                phone_input_layout.setVisibility(View.GONE);
                selectedOption = SL_OPTION_EMAIL;
            } else {
                selectedOption = SL_OPTION_PHONE;
                text_email_phone_option.setText(R.string.use_email_instead);
                email_input_layout.setVisibility(View.GONE);
                phone_input_layout.setVisibility(View.VISIBLE);
            }
        });
        continueButton.setOnClickListener(v -> {
            if (selectedOption == SL_OPTION_PHONE && onCodeSent) {
                progressBar.setVisibility(View.VISIBLE);
                String verificationCode = String.valueOf(verificationCodeFiled.getText());
                verifyPhoneNumberWithCode(mVerificationId, verificationCode);
            } else if (selectedOption == SL_OPTION_PHONE) {
                progressBar.setVisibility(View.VISIBLE);
                String convertText = String.valueOf(phoneInputFiled.getText());
                if (!convertText.startsWith("+")) {
                   convertText = "+"+phoneInputFiled.getText();
                }
                startPhoneNumberVerification(convertText);
            } else if (!userSigningUp && !userSigningIn) {
                checkUser();
                Toast.makeText(getApplicationContext(), "===>checking user", Toast.LENGTH_LONG).show();
            } else if (userSigningUp) {
                String email = String.valueOf(emailField.getText());
                String password = String.valueOf(passwordField.getText());
                String repeat_password = String.valueOf(repeatPasswordField.getText());
                Toast.makeText(getApplicationContext(), "===>Signing up", Toast.LENGTH_LONG).show();
                if (password.equals(repeat_password)) {
                    createUserWithEmail(email, password);
                } else {
                    repeat_password_input_field_layout.setErrorEnabled(true);
                    repeat_password_input_field_layout.setError("Password not matched");
                    System.out.println("password did not matched");
                }

            } else if (userSigningIn) {
                String email = String.valueOf(emailField.getText());
                String password = String.valueOf(passwordField.getText());
                signingUserEmail(email, password);
                Toast.makeText(getApplicationContext(), "===>Signing IN", Toast.LENGTH_LONG).show();
            }
        });

        forgot_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(emailField.getText()).toString();
                if (!TextUtils.isEmpty(email)){
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Email is empty",Toast.LENGTH_SHORT).show();
                }
            }
        });


        googleSigninBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            someActivityResultLauncher.launch(signInIntent);
            Log.d(TAG, "onActivityResult: called intent");
        });

        projectUtils = new ProjectUtils(this);
        projectUtils.checkAllPermission();
        if(projectUtils.checkPostNotificationPermission(this)){
            Intent intent1 = new Intent(this,ChatService.class);
            intent1.putExtra("isVisible","false");
            intent1.putExtra("PhoneNumber","");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent1);
            } else {
                startService(intent1);
            }
        }

    }

    private void resetPassword(String email) {
          if(mAuth == null){
              return;
          }
          mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  if(task.isSuccessful()){
                      Toast.makeText(getApplicationContext(),"Password reset email has sent",Toast.LENGTH_SHORT).show();
                      password_input_field_layout.setVisibility(View.GONE);
                      forgot_password_button.setVisibility(View.GONE);
                      progressBar.setVisibility(View.GONE);
                      userSigningIn = false;
                  } else {
                      Toast.makeText(getApplicationContext(),"Password reset failed",Toast.LENGTH_SHORT).show();
                      password_input_field_layout.setVisibility(View.GONE);
                      forgot_password_button.setVisibility(View.GONE);
                      progressBar.setVisibility(View.GONE);
                      userSigningIn = false;
                  }
              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  Toast.makeText(getApplicationContext(),"Password reset failed",Toast.LENGTH_SHORT).show();
                  password_input_field_layout.setVisibility(View.GONE);
                  forgot_password_button.setVisibility(View.GONE);
                  progressBar.setVisibility(View.GONE);
                  userSigningIn = false;
              }
          });
    }
    @Override
    public void onStart() {
        super.onStart();
        //updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        //updateUI();
    }

    private void createUserWithEmail(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        updateUI();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        //updateUI();
                    }
                });
    }

    private void updateUI() {
        try {
            System.out.println("=====> " + "updateUI");
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                if (user.isEmailVerified()) {
                    Intent userHome = new Intent(MainActivity.this, UserHome.class);
                    userHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(userHome);
                    finish();
                } else {
                    verifyEmailAddress();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void verifyEmailAddress() {
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, (OnCompleteListener<Void>) task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this,
                                "Verification email sent to " + user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                        repeat_password_input_field_layout.setVisibility(View.GONE);
                        password_input_field_layout.setVisibility(View.GONE);
                        userSigningUp = false;
                        userSigningIn = false;
                        continueButton.setText("continue");
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(MainActivity.this,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void signingUserEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        updateUI();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        //updateUI();
                    }
                });
    }


    private void checkUser() {
        String email = Objects.requireNonNull(emailField.getText()).toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
        } else {
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean check = !Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();
                            if (!check) {
                                userExits = false;
                                userSigningIn = false;
                                userSigningUp = true;
                                password_input_field_layout.setVisibility(View.VISIBLE);
                                repeat_password_input_field_layout.setVisibility(View.VISIBLE);
                                forgot_password_button.setVisibility(View.GONE);
                                continueButton.setText(R.string.sign_up);
                            } else {
                                password_input_field_layout.setVisibility(View.VISIBLE);
                                forgot_password_button.setVisibility(View.VISIBLE);
                                continueButton.setText(R.string.sign_in);
                                userExits = true;
                                userSigningUp = false;
                                userSigningIn = true;
                               // Toast.makeText(getApplicationContext(), "email already exst", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: "+result.getResultCode());
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: called ok");
                        // There are no request codes
                        Intent data = result.getData();
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
                                Log.d(TAG, "onActivityResult: called success");
                                // Initialize sign in account

                                // Initialize sign in account
                                GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                                // Check condition
                                if (googleSignInAccount != null) {
                                    // When sign in account is not equal to null initialize auth credential
                                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                                    // Check credential
                                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                                        // Check condition
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.GONE);
                                            // When task is successful redirect to profile activity display Toast
                                            startActivity(new Intent(MainActivity.this, UserHome.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                            displayToast("Firebase authentication successful");
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            // When task is unsuccessful display Toast
                                            displayToast("Authentication Failed :" + Objects.requireNonNull(task.getException()).getMessage());
                                        }
                                    });
                                }
                            } else {
                                Log.d(TAG, "onActivityResult: called failed");
                            }
                       }
                        catch (ApiException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                       }
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // }
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
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
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                       // updateUI();
                        // Update UI
                        progressBar.setVisibility(View.GONE);
                        Intent userHome = new Intent(MainActivity.this, UserHome.class);
                        userHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(userHome);
                        finish();
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Intent intent1 = new Intent(this,ChatService.class);
                intent1.putExtra("isVisible","false");
                intent1.putExtra("PhoneNumber","");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent1);
                } else {
                    startService(intent1);
                }
            } else {

            }
        }
    }


}


