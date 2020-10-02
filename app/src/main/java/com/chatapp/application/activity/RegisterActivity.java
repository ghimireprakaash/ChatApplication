package com.chatapp.application.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.chatapp.application.R;
import com.chatapp.application.fragments.DialogFragment;
import com.hbb20.CountryCodePicker;

public class RegisterActivity extends AppCompatActivity {
    String regex = "^[0-9]{10,14}$";

    CountryCodePicker ccp;
    EditText editTextCarrierNumber;
    Button continueBtn;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);

        ccp = findViewById(R.id.countryCodePicker);
        editTextCarrierNumber = findViewById(R.id.phNumberEditText);
        continueBtn = findViewById(R.id.continueButton);


        //for button enable and disable
        editTextCarrierNumber.addTextChangedListener(textChangeListener);



        //Attach CarrierNumber editText to CCP
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);

        ccp.getFormattedFullNumber();

        //Defining constructor of Bundle class
        bundle = new Bundle();

    }


    //TextChange Listener listens to either button should be disabled or enabled
    private TextWatcher textChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String editTextPhoneNumberField = editTextCarrierNumber.getText().toString().trim();

            continueBtn.setEnabled(!editTextPhoneNumberField.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };



    //OnCountry selected
    public void OnCountryCodePicker(View view) {
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                ccp.getSelectedCountryName();
                ccp.getSelectedCountryCodeWithPlus();

            }
        });
    }



    //On Pressed Continue Button after number is filled and country selected
    public void OnPressedContinue(View view) {
        String code = ccp.getSelectedCountryCode();

        String phNumber = editTextCarrierNumber.getText().toString().trim();

        if (phNumber.length() < 10 || phNumber.length() > 14 || phNumber.matches(regex)){

            editTextCarrierNumber.setError("Valid phone number is required.");

        } else {
            String fullPhoneNumberWithCountryCode = "+" + code + phNumber;

            bundle.putString("phNumberWithCountryCode", fullPhoneNumberWithCountryCode);

            bundle.putString("phNumber", phNumber);

            final DialogFragment dialogFragment = new DialogFragment();
            dialogFragment.setArguments(bundle);
            dialogFragment.show(getSupportFragmentManager(), "Dialog Fragment");

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}