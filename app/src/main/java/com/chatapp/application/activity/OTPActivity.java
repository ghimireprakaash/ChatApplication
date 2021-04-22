package com.chatapp.application.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.chatapp.application.R;
import com.chatapp.application.customdialog.CustomLoadingDialog;
import com.chatapp.application.profile.ProfileSetupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    private static final String TAG = "OTPActivity";

    String fullPhoneNumber;
    String numberVerificationId;
    PhoneAuthProvider.ForceResendingToken resendToken;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;


    CustomLoadingDialog loadingDialog;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_otp);

        //declaring firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();


        fullPhoneNumber = getIntent().getStringExtra("fullPhoneNumber");


        loadingDialog = new CustomLoadingDialog(this);
        loadingDialog.startLoadingDialog();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                fullPhoneNumber,                 // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                this,                    // Activity (for callback binding)
                mCallbacks);                     // OnVerificationStateChangedCallbacks
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);

            String verificationCode = phoneAuthCredential.getSmsCode();
            if (verificationCode != null) {
                verifyCode(verificationCode);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.w(TAG, "onVerificationFailed", e);

            Toast.makeText(OTPActivity.this, "Verification failed - "+ e.getMessage(), Toast.LENGTH_LONG).show();
            loadingDialog.dismissDialog();
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
        }
    };


    private void verifyCode(String verificationCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(numberVerificationId, verificationCode);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Intent intent = new Intent(OTPActivity.this, ProfileSetupActivity.class);
                            //loadingDialog dismissed before switching activity
                            loadingDialog.dismissDialog();

                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(OTPActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            loadingDialog.dismissDialog();
                        }
                    }
                });
    }
}