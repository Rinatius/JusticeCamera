package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserTokenStorageFactory;

public class Login extends AppCompatActivity {
    static ProgressDialog pd;
    public final static String BACKENDLESS_APP_ID = "A2A1E1C9-A8F7-C938-FFEF-4D4EA6C0A300";
    public final static String BACKENDLESS_SECRET_KEY = "71C79AEF-B5AD-C438-FF02-F87ADD10AB00";
    Button buttonLogin, buttonSignUp, buttonSkip;
    EditText editLogin, editPassword;
    TextView textViewInfo;
    CheckBox checkBox;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final BackendlessUser user = new BackendlessUser();
        getSupportActionBar().hide();

        String appVersion = "v1";
        Backendless.initApp(this, BACKENDLESS_APP_ID, BACKENDLESS_SECRET_KEY, appVersion);

        init();

        String userToken = UserTokenStorageFactory.instance().getStorage().get();
        if( userToken != null && !userToken.equals( "" ) )
        {
            startActivity(new Intent(Login.this, MainActivity.class));
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean stayLoggedIn = checkBox.isChecked();
                Backendless.UserService.login(editLogin.getText().toString(), editPassword.getText().toString(), new AsyncCallback<BackendlessUser>() {
                    public void handleResponse(BackendlessUser user) {
                        Intent i = new Intent(Login.this, MainActivity.class);
                        startActivity(i);
                        // user has been logged in
                    }

                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getApplicationContext(), getString(R.string.author_error), Toast.LENGTH_LONG).show();
                        // login failed, to get the error code call fault.getCode()
                    }
                }, stayLoggedIn);
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                user.setEmail(editLogin.getText().toString());
                user.setPassword(editPassword.getText().toString());

                Backendless.UserService.register(user, new BackendlessCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser backendlessUser) {
                        Log.i(getString(R.string.registration), backendlessUser.getEmail() + getString(R.string.havebeen_registered));
                        textViewInfo.setText("Пользователь " + backendlessUser.getEmail() + " зарегистрирован");
                        Intent i = new Intent(Login.this, Login.class);
                        startActivity(i);
                    }
                });
            }
        });

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(Login.this, CheckedVideoList.class);
                startActivity(i);
            }
        });
    }
    private void init() {
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        buttonSkip = (Button) findViewById(R.id.buttonSkip);
        editLogin = (EditText) findViewById(R.id.editLogin);
        editPassword = (EditText) findViewById(R.id.editLogin);
        textViewInfo = (TextView) findViewById(R.id.textViewInfo);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
    }
}
