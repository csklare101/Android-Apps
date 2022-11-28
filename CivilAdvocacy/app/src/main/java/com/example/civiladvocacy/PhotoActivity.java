package com.example.civiladvocacy;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {
    private static final String TAG = "PhotoActivity";
    private TextView name;
    private TextView office;

    private ConstraintLayout layout;

    private TextView location;
    private String loc;

    private ImageView partyIcon;

    private ImageView officialPhoto;
    private Official official;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        name = findViewById(R.id.officialNameP);
        office = findViewById(R.id.officeTextP);
        layout = findViewById(R.id.ConstraintLayoutP);
        location = findViewById(R.id.locationTextP);

        partyIcon = findViewById(R.id.partyIconP);
        officialPhoto = findViewById(R.id.officialPhotoP);

        if(getIntent().hasExtra("LOCATION")){
            loc = (String) getIntent().getSerializableExtra("LOCATION");
            location.setText(loc);
        }
        if(getIntent().hasExtra("SHOW_OFFICIAL_PHOTO")){
            official = (Official) getIntent().getSerializableExtra("SHOW_OFFICIAL_PHOTO");

            name.setText(official.getName());
            office.setText(official.getOffice());

            if(official.getParty().equals("Republican") || official.getParty().equals("Republican Party")){
                layout.setBackgroundColor(Color.RED);
                partyIcon.setImageResource(R.drawable.rep_logo);
            }
            else if(official.getParty().equals("Democrat") || official.getParty().equals("Democratic")
                    || official.getParty().equals("Democratic Party")){
                layout.setBackgroundColor(Color.BLUE);
                partyIcon.setImageResource(R.drawable.dem_logo);
            }
            else{
                layout.setBackgroundColor(Color.BLACK);
                partyIcon.setVisibility(View.GONE);
            }

            Picasso.get().load(official.getPhotoUrl())
                    .into(officialPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            long time = System.currentTimeMillis();
                            Log.d(TAG, "onSuccess: "+ time);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d(TAG, "onError " + e);
                            officialPhoto.setImageResource(R.drawable.brokenimage);
                        }
                    });
        }
    }
}
