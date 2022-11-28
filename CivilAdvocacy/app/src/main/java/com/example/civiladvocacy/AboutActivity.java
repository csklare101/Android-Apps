package com.example.civiladvocacy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.apiDesc){
            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setData(Uri.parse("https://developers.google.com/civic-information/"));

            if(intent.resolveActivity(getPackageManager())!= null){
                startActivity(intent);
            }
            else{
                noAppFound("No app to handle web events!");
            }
        }
    }

    public void noAppFound(String message){
        AlertDialog.Builder noApp = new AlertDialog.Builder(this);
        noApp.setMessage(message);
        noApp.setTitle("No App Found");

        noApp.create().show();
    }
}
