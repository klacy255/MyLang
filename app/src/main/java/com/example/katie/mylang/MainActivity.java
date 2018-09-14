package com.example.katie.mylang;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * MainActivity.java
 * Purpose: activity that will be considered the "homepage" once a user is logged in.
 *
 * @author katie
 * @version 1.0 4/10/17.
 */
public class MainActivity extends AppCompatActivity {
    TextView message;
    TextView welcomeMessage;
    Button dictionaries;
    Button translate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = (TextView) findViewById(R.id.welcome);
        welcomeMessage = (TextView) findViewById(R.id.welcomeMessage);
        dictionaries = (Button) findViewById(R.id.btn_dictionaries);
        translate = (Button) findViewById(R.id.btn_translate);

        Typeface font = Typeface.createFromAsset(getAssets(), "bebas_neue/TTF's/BebasNeue Regular.ttf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "roboto/Roboto-Regular.ttf");

        message.setTypeface(font);
        welcomeMessage.setTypeface(font2);
        dictionaries.setTypeface(font);
        translate.setTypeface(font);

    }
}
