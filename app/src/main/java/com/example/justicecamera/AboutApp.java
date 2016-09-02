package com.example.justicecamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutApp extends AppCompatActivity {

    TextView textViewPart1, textViewPart2, textViewPart3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        init();
        settext();
    }

    private void init(){
        textViewPart1 = (TextView) findViewById(R.id.textViewAboutPart1);
        textViewPart2 = (TextView) findViewById(R.id.textViewAboutPart2);
        textViewPart3 = (TextView) findViewById(R.id.textViewAboutPart3);
        getSupportActionBar().setSubtitle(getString(R.string.about));
    }

    private void settext(){
        textViewPart1.setText(TextCont.PART1);
        textViewPart2.setText(TextCont.PART2);
        textViewPart3.setText(TextCont.PART3);
    }
}
