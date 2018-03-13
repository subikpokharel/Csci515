package com.csci515.subik.serverconnection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String name, email;
    private Integer age;
    private char gender;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = "S";
        email = "l@und.edu";
        age = 21;
        gender = 'M';
         textView = findViewById(R.id.tv);
        //new SigninActivity( getApplicationContext(), textView ).execute( );
    }

    public void login(View view) {

        new SigninActivity( getApplicationContext(), textView ).execute( name, age.toString(), String.valueOf(gender), email );

    }
}
