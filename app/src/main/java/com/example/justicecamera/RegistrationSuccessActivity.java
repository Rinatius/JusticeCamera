package com.example.justicecamera;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegistrationSuccessActivity extends AppCompatActivity
{
  private Button loginButton;

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.registration_success );

    initUI();
  }

  private void initUI()
  {
    getSupportActionBar().setTitle(getString(R.string.app_name));
    getSupportActionBar().setSubtitle(getString(R.string.registration_page_name));
    loginButton = (Button) findViewById( R.id.loginButton );
    loginButton.setOnClickListener( new View.OnClickListener()
    {
      @Override
      public void onClick( View view )
      {
        onLoginButtonClicked();
      }
    } );
  }

  public void onLoginButtonClicked()
  {
    startActivity( new Intent( this, LoginActivity.class ) );
    finish();
  }
}