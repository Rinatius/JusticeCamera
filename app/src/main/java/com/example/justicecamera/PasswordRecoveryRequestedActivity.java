package com.example.justicecamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class PasswordRecoveryRequestedActivity extends AppCompatActivity
{
  private Button loginButton;

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.password_recovery_requested );


    initUI();
  }

  private void initUI()
  {
    loginButton = (Button) findViewById( R.id.loginButton );

    loginButton.setOnClickListener( new View.OnClickListener()
    {
      @Override
      public void onClick( View view )
      {
        onLoginButtonClicked();
      }
    } );
    getSupportActionBar().setTitle(getString(R.string.app_name));
    getSupportActionBar().setSubtitle(getString(R.string.password_recovery_page_name));
  }

  public void onLoginButtonClicked()
  {
    startActivity( new Intent( this, LoginActivity.class ) );
    finish();
  }
}