package com.chatapp.application;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.hbb20.CountryCodePicker;

public class RegisterActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    EditText editTextCarrierNumber;
    Button continueBtn;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ccp = findViewById(R.id.countryCodePicker);
        editTextCarrierNumber = findViewById(R.id.phNumberEditText);
        continueBtn = findViewById(R.id.continueButton);

        //Attach CarrierNumber editText to CCP
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);

        ccp.getFormattedFullNumber();

        //Defining constructor of Bundle class
        bundle = new Bundle();

    }


    public void OnCountryCodePicker(View view) {
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                ccp.getSelectedCountryName();
                ccp.getSelectedCountryCodeWithPlus();

            }
        });
    }

    public void OnPressedContinue(View view) {

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phNumber = editTextCarrierNumber.getText().toString().trim();

                if (phNumber.isEmpty()){
                    Log.i("click", "Button Disabled");
                    continueBtn.setEnabled(false);

                } else{
                      continueBtn.setEnabled(true);
                }

                if (phNumber.length() < 10 || phNumber.length() > 13 || Patterns.PHONE.matcher(phNumber).matches()){

                    editTextCarrierNumber.setError("Valid phone number is required.");

                }


                bundle.putString("phNumber", phNumber);

                DialogFragment dialogFragment = new DialogFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "Dialog Fragment");
            }
        });
    }
}