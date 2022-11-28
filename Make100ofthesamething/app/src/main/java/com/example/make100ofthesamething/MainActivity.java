package com.example.make100ofthesamething;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView make = (TextView) findViewById(R.id.makeOneHundred);
        String makeText = "";
        for(int i = 0; i < 100; i++){
            makeText += "Make 100 of the \"same\" thing.\n";
        }
        make.setText(makeText);
        make.setMovementMethod(new ScrollingMovementMethod());
    }
}