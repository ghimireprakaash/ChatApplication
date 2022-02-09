package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.chatapp.application.R;
import com.chatapp.application.customdialog.CustomLoadingDialog;
import com.chatapp.application.profile.ProfileSetupActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    private static final String TAG = "OTPActivity";
    private EditText verificationCode;
    private TextView please_wait, resendCode;
    private ProgressBar progressBar;
    private MaterialCardView verification_code_card_layout;
    private MaterialButton submitBtn;

    private String fullPhoneNumber;
    private String numberVerificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private FirebaseAuth firebaseAuth;


    private CustomLoadingDialog loadingDialog;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_otp);

        //declaring firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        initFields();

        fullPhoneNumber = getIntent().getStringExtra("fullPhoneNumber");


        progressBar.setVisibility(View.VISIBLE);

        //Phone number verification...
        verifyPhoneNumber(fullPhoneNumber);

        resendCode.setOnClickListener(v -> {
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(fullPhoneNumber)                // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS)      // Timeout and unit
                            .setActivity(this)                              // Activity (for callback binding)
                            .setCallbacks(mCallbacks)                       // OnVerificationStateChangedCallbacks
                            .setForceResendingToken(resendToken)            // Force resend verification code
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        });

        submitBtn.setOnClickListener(v -> {
            loadingDialog = new CustomLoadingDialog(this);
            loadingDialog.startLoadingDialog();

            String code = verificationCode.getText().toString().trim();
            if (TextUtils.isEmpty(code)){
                verificationCode.setError("Verification code required!");
                return;
            }
            verifyPhoneNumberWithCode(numberVerificationId, code);
        });
    }

    private void initFields() {
        verificationCode = findViewById(R.id.verification_code);
        resendCode = findViewById(R.id.resend_Code);
        submitBtn = findViewById(R.id.submit_btn);

        progressBar = findViewById(R.id.progress_bar);
        please_wait = findViewById(R.id.please_wait_txt);
        verification_code_card_layout = findViewById(R.id.verification_code_card_layout);
    }

    private void verifyPhoneNumber(String fullPhoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(fullPhoneNumber)                // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)      // Timeout and unit
                        .setActivity(this)                              // Activity (for callback binding)
                        .setCallbacks(mCallbacks)                       // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);

            signInWithPhoneAuthCredential(phoneAuthCredential);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.w(TAG, "onVerificationFailed", e);

            Toast.makeText(OTPActivity.this, "Verification failed - "+ e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            finish();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId, token);

            Log.d(TAG, "onCodeSent:" + verificationId);
            Log.d(TAG, "onCodeSent:" + resendToken);

            //Save verification ID and resending token so we can use them later
            numberVerificationId = verificationId;
            resendToken = token;


            //get Visibility back...
            please_wait.setVisibility(View.INVISIBLE);
            verification_code_card_layout.setVisibility(View.VISIBLE);
        }
    };


    private void verifyPhoneNumberWithCode(String numberVerificationId, String code) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(numberVerificationId, code);
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()){
                        Intent intent = new Intent(OTPActivity.this, ProfileSetupActivity.class);
                        //loadingDialog dismissed before switching activity
                        loadingDialog.dismissDialog();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(OTPActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        loadingDialog.dismissDialog();
                    }
                });
    }
}