package com.example.justicecamera;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

public class RegisterActivity extends AppCompatActivity {
    private final static java.text.SimpleDateFormat SIMPLE_DATE_FORMAT = new java.text.SimpleDateFormat("yyyy/MM/dd");

    private EditText emailField;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText middleNameField;
    private EditText passportNoField;
    private EditText passwordField;
    private EditText phoneNumberField;
    private Button registerButton;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String passportNo;
    private String password;
    private String phoneNumber;


    private JusticeCameraUser user;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        initUI();
    }

    private void initUI() {
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setSubtitle(getString(R.string.registration_page_name));
        emailField = (EditText) findViewById(R.id.emailField);
        firstNameField = (EditText) findViewById(R.id.firstNameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        phoneNumberField = (EditText) findViewById(R.id.phoneNumberField);
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterButtonClicked();
            }
        });

//        lastNameField = (EditText) findViewById(R.id.lastNameField);
//        passportNoField = (EditText) findViewById(R.id.passportNoField);
//        middleNameField = (EditText) findViewById(R.id.middleNameField);

    }

    private void onRegisterButtonClicked() {

        String emailText = emailField.getText().toString().trim();
        String firstNameText = firstNameField.getText().toString().trim();
        String passwordText = passwordField.getText().toString().trim();
        String phoneNumberText = phoneNumberField.getText().toString().trim();

//        String lastNameText = lastNameField.getText().toString().trim();
//        String passportNoText = passportNoField.getText().toString().trim();
//        String middleNameText = middleNameField.getText().toString().trim();

        if (emailText.isEmpty()) {
            showToast("Field 'email' cannot be empty.");
            return;
        }

        if (passwordText.isEmpty()) {
            showToast("Field 'password' cannot be empty.");
            return;
        }

        if (!emailText.isEmpty()) {
            email = emailText;
        }

        if (!firstNameText.isEmpty()) {
            firstName = firstNameText;
        }

//        if(!middleNameText.isEmpty()){
//            middleName = middleNameText;
//        }
//
//        if (!lastNameText.isEmpty()) {
//            lastName = lastNameText;
//        }
//
//        if (!passportNoText.isEmpty()) {
//            passportNo = passportNoText;
//        }

        if (!passwordText.isEmpty()) {
            password = passwordText;
        }

        if (!phoneNumberText.isEmpty()) {
            phoneNumber = phoneNumberText;
        }

        user = new JusticeCameraUser();

        if (email != null) {
            user.setEmail(email);
        }

        if (middleName != null){
            user.setProperty("middleName", middleName);
        }

        if (firstName != null) {
            user.setFirstName(firstName);
        }

        if (lastName != null) {
            user.setLastName(lastName);
        }


        if (passportNo != null) {
            user.setPassportNo(passportNo);
        }

        if (password != null) {
            user.setPassword(password);
        }

        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }

        Backendless.UserService.register(user, new DefaultCallback<BackendlessUser>(RegisterActivity.this) {
            @Override
            public void handleResponse(BackendlessUser response) {
                super.handleResponse(response);
                startActivity(new Intent(RegisterActivity.this, RegistrationSuccessActivity.class));
                finish();
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}