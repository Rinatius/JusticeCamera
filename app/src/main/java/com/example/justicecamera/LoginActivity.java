package com.example.justicecamera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;

public class LoginActivity extends Activity {
    private TextView registerLink, restoreLink;
    private EditText identityField, passwordField;
    private Button loginButton, buttonSkip;
    private CheckBox rememberLoginBox;
    public final static String PATH = "path2";
    private ProgressDialog pd;
    private boolean isVersionChecked;
    private boolean isLoginButtonClicked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initUI();

        Backendless.setUrl(Defaults.SERVER_URL);
        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);

        if (isOnline()) {
            new VersionCheckTask().execute();
        } else {
            Toast.makeText(this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        registerLink = (TextView) findViewById(R.id.registerLink);
        restoreLink = (TextView) findViewById(R.id.restoreLink);
        identityField = (EditText) findViewById(R.id.identityField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        loginButton = (Button) findViewById(R.id.loginButton);
        rememberLoginBox = (CheckBox) findViewById(R.id.rememberLoginBox);
     //   buttonSkip = (Button) findViewById(R.id.buttonSkip);

        String tempString = getResources().getString(R.string.register_text);
        SpannableString underlinedContent = new SpannableString(tempString);
        underlinedContent.setSpan(new UnderlineSpan(), 0, tempString.length(), 0);
        registerLink.setText(underlinedContent);
        tempString = getResources().getString(R.string.restore_link);
        underlinedContent = new SpannableString(tempString);
        underlinedContent.setSpan(new UnderlineSpan(), 0, tempString.length(), 0);
        restoreLink.setText(underlinedContent);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLoginButtonClicked = true;
                if (isVersionChecked) {
                    onLoginButtonClicked();
                } else {
                    new VersionCheckTask().execute();
                }
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterLinkClicked();
            }
        });

        restoreLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRestoreLinkClicked();
            }
        });

    /*    buttonSkip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, CheckedVideoList.class);
                startActivity(i);
                finish();
            }
        });*/
    }

    public void onLoginButtonClicked() {
        String identity = identityField.getText().toString();
        String password = passwordField.getText().toString();
        boolean rememberLogin = rememberLoginBox.isChecked();

        Backendless.UserService.login(identity, password, new DefaultCallback<BackendlessUser>(LoginActivity.this) {
            public void handleResponse(BackendlessUser backendlessUser) {
                super.handleResponse(backendlessUser);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }, rememberLogin);
    }

    public void onRegisterLinkClicked() {
        startActivity(new Intent(this, RegisterActivity.class));
        //finish();
    }

    public void onRestoreLinkClicked() {
        startActivity(new Intent(this, RestorePasswordActivity.class));
      //  finish();
    }

    private void checkUser(){
        Backendless.UserService.isValidLogin(new DefaultCallback<Boolean>(this, getString(R.string.loading)) {
            @Override
            public void handleResponse(Boolean isValidLogin) {
                if (isValidLogin && Backendless.UserService.CurrentUser() == null) {
                    String currentUserId = Backendless.UserService.loggedInUser();

                    if (!currentUserId.equals("")) {
                        Backendless.UserService.findById(currentUserId, new DefaultCallback<BackendlessUser>(LoginActivity.this, getString(R.string.logging_in)) {
                            @Override
                            public void handleResponse(BackendlessUser currentUser) {
                                super.handleResponse(currentUser);
                                Backendless.UserService.setCurrentUser(currentUser);

                                Intent outer = getIntent();

                                if (outer.hasExtra(Intent.EXTRA_STREAM)){
                                    Uri videoUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                                    String path = MainActivity.getRealPathFromURI(LoginActivity.this, videoUri);
                                    Intent toMain = new Intent(LoginActivity.this, MainActivity.class);
                                    toMain.putExtra(PATH, path);
                                    startActivity(toMain);
                                    finish();
                                } else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
                    }
                }

                super.handleResponse(isValidLogin);
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showUpdateDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(getString(R.string.update_application));
        builder.setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                } finally {
                    finish();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


    private class VersionCheckTask extends AsyncTask<Void, Void, String > {
        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(LoginActivity.this, "", getString(R.string.checking_version), true );
        }

        @Override
        protected String doInBackground(Void... params) {
            Acct app = Helper.getApplicationInfo();
            String version = app.getVersion();

            if ((version != null) && (!version.equals(""))) {
                return version;
            } else return "ERROR";

        }

        @Override
        protected void onPostExecute(String result){
            pd.dismiss();

            String localVersion = BuildConfig.VERSION_NAME;

            if (!(result.equals("ERROR"))){
                isVersionChecked = true;
                String remoteVersion = result;
                if (localVersion.equals(remoteVersion)){
                    if (isLoginButtonClicked){
                        onLoginButtonClicked();
                    } else {
                        checkUser();
                    }
                } else {
                    showUpdateDialog();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Server error, please try later", Toast.LENGTH_SHORT).show();
            }
        }
    }
}