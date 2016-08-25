package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

public class Login1 extends AppCompatActivity {
    ProgressDialog pd;
    Button buttonLogin, buttonSignUp, buttonSkip;
    EditText editLogin, editPassword;
    TextView textViewInfo;
    CheckBox checkBox;
    private static long back_pressed;
    public final static String PATH = "path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        final BackendlessUser user = new BackendlessUser();
        getSupportActionBar().hide();

        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);

        init();

        String userToken = UserTokenStorageFactory.instance().getStorage().get();
        if (userToken != null && !userToken.equals("")) {
            Intent outer = getIntent();

            if (outer.hasExtra(Intent.EXTRA_STREAM)){
                Uri videoUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                String path = MainActivity.getRealPathFromURI(this, videoUri);
                Intent toMain = new Intent(Login1.this, MainActivity.class);
                toMain.putExtra(PATH, path);
                startActivity(toMain);
            } else {
                startActivity(new Intent(Login1.this, MainActivity.class));
            }
        }
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    pd = new ProgressDialog(Login1.this);
                    pd.setMessage(getString(R.string.checking));
                    pd.show();

                    boolean stayLoggedIn = checkBox.isChecked();
                    Backendless.UserService.login(editLogin.getText().toString(), editPassword.getText().toString(), new AsyncCallback<BackendlessUser>() {
                        public void handleResponse(BackendlessUser user) {
                            pd.dismiss();
                            Intent i = new Intent(Login1.this, MainActivity.class);
                            startActivity(i);
                            // user has been logged in
                        }

                        public void handleFault(BackendlessFault fault) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), getString(R.string.author_error), Toast.LENGTH_LONG).show();

                            // login failed, to get the error code call fault.getCode()
                        }
                    }, stayLoggedIn);
                }
            });

            buttonSignUp.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    pd = new ProgressDialog(Login1.this);
                    pd.setMessage(getString(R.string.checking));
                    pd.show();
                    user.setEmail(editLogin.getText().toString());
                    user.setPassword(editPassword.getText().toString());

                    Backendless.UserService.register(user, new BackendlessCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {
                            pd.dismiss();
                            Log.i(getString(R.string.registration), backendlessUser.getEmail() + getString(R.string.havebeen_registered));
                            //   textViewInfo.setText("Пользователь " + backendlessUser.getEmail() + " зарегистрирован");
                            Helper.showToast(getString(R.string.check_email), Login1.this);
                        }
                        public void handleFault(BackendlessFault fault) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "Registration error", Toast.LENGTH_LONG).show();

                            // login failed, to get the error code call fault.getCode()
                        }

                    });
                }
            });

            buttonSkip.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent i = new Intent(Login1.this, CheckedVideoList.class);
                    startActivity(i);
                    finish();
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

    @Override
    public void onBackPressed() {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);

    }
}
