package com.example.civiladvocacy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import okhttp3.Call;

public class OfficialActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "OfficialActivity";
    private TextView location;
    private String loc;

    private TextView name;
    private TextView office;
    private TextView party;
    private ImageView partyIcon;

    private ImageView fb;
    private ImageView twitter;
    private ImageView youtube;

    private TextView address;
    private TextView phone;
    private TextView email;
    private TextView website;
    
    private ImageView officialPhoto;

    private ConstraintLayout layout;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Official o;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        name = findViewById(R.id.officialNameO);
        office = findViewById(R.id.officeTitleO);
        party = findViewById(R.id.partyTextO);
        partyIcon = findViewById(R.id.partyIconO);

        fb = findViewById(R.id.fbIconO);
        twitter = findViewById(R.id.twIconO);
        youtube = findViewById(R.id.ytIconO);

        address = findViewById(R.id.addressTextO);
        phone = findViewById(R.id.phoneTextO);
        email = findViewById(R.id.emailTextO);
        website = findViewById(R.id.websiteTextO);

        officialPhoto = findViewById(R.id.officialImageO);
        layout = findViewById(R.id.ConstraintLayoutO);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleResult);

        location = findViewById(R.id.locationTextO);

        if(getIntent().hasExtra("LOCATION")){
            loc = (String) getIntent().getSerializableExtra("LOCATION");
            location.setText(loc);
        }
        if(getIntent().hasExtra("SHOW_OFFICIAL")){
            o = (Official) getIntent().getSerializableExtra("SHOW_OFFICIAL");
            name.setText(o.getName());
            office.setText(o.getOffice());
            party.setText("(" + o.getParty() + ")");

            if(o.getFb().equals("")){
                fb.setVisibility(View.GONE);
            }
            if(o.getTwitter().equals("")){
                twitter.setVisibility(View.GONE);
            }
            if(o.getYoutube().equals("")){
                youtube.setVisibility(View.GONE);
            }

            if(!o.getAddress().equals("")) {
                address.setText(o.getAddress());
            }
            if(!o.getPhone().equals("")){
                phone.setText(o.getPhone());
            }
            if(!o.getEmail().equals("")) {
                email.setText(o.getEmail());
            }
            if(!o.getWebsite().equals("")) {
                website.setText(o.getWebsite());
            }

            if(o.getParty().equals("Republican") || o.getParty().equals("Republican Party")){
                layout.setBackgroundColor(Color.RED);
                partyIcon.setImageResource(R.drawable.rep_logo);
            }
            else if(o.getParty().equals("Democrat") || o.getParty().equals("Democratic")
                    || o.getParty().equals("Democratic Party")){
                layout.setBackgroundColor(Color.BLUE);
                partyIcon.setImageResource(R.drawable.dem_logo);
            }
            else{
                layout.setBackgroundColor(Color.BLACK);
                partyIcon.setVisibility(View.GONE);
            }

            if(!o.getPhotoUrl().equals("")){
                Picasso.get().load(o.getPhotoUrl())
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
            else{
                officialPhoto.setImageResource(R.drawable.missing);
            }
        }
    }

    public void handleResult(ActivityResult result){
        if(result == null || result.getData() == null){
            return;
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.officialImageO){
            if(!o.getPhotoUrl().equals("")) {
                Intent intent = new Intent(this, PhotoActivity.class);
                intent.putExtra("LOCATION", loc);
                intent.putExtra("SHOW_OFFICIAL_PHOTO", o);
                activityResultLauncher.launch(intent);
            }
        }
        if(view.getId() == R.id.fbIconO && !o.getFb().equals("")){
            String id = o.getFb();
            String fbURL = "https://www.facebook.com/" + id;
            Intent intent;

            if(isPackageInstalled("com.facebook.katana")){
                String urlToUse = "fb://facewebmodal/f?href="+fbURL;
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
            }
            else{
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fbURL));
            }

            if(intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }
            else{
                noAppFound("No app to handle facebook intents!");
            }
        }

        if(view.getId() == R.id.ytIconO && !o.getYoutube().equals("")) {
            String id = o.getYoutube();
            String youtubeUrl = "https://www.youtube.com/" + id;
            Intent intent;

            if(isPackageInstalled("com.google.android.youtube")){
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.youtube");
                intent.setData(Uri.parse(youtubeUrl));

            }
            else{
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
            }
            if(intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }
            else{
                noAppFound("No app to handle youtube intents!");
            }
        }

        if(view.getId() == R.id.twIconO && !o.getTwitter().equals("")) {
            String id = o.getTwitter();
            String twitterAppUrl = "twitter://user?screen_name=" + id;
            String twitterWebUrl = "https://twitter.com/" + id;
            Intent intent;

            if (isPackageInstalled("com.twitter.android")) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
            } else {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
            }

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                noAppFound("No app to handle twitter intents!");
            }
        }

        if(view.getId() == R.id.addressTextO && !o.getAddress().equals("")){
            Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(o.getAddress()));

            Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
            if(intent.resolveActivity(getPackageManager())!= null){
                startActivity(intent);
            }
            else{
                noAppFound("No app to handle map events!");
            }
        }

        if(view.getId() == R.id.phoneTextO && !o.getPhone().equals("")){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + o.getPhone()));

            if(intent.resolveActivity(getPackageManager())!= null){
                startActivity(intent);
            }
            else{
                noAppFound("No app to handle phone events!");
            }
        }

        if(view.getId() == R.id.emailTextO && !o.getEmail().equals("")) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + o.getEmail()));

            if(intent.resolveActivity(getPackageManager())!= null){
                startActivity(intent);
            }
            else{
                noAppFound("No app to handle email events!");
            }
        }

        if(view.getId() == R.id.websiteTextO && !o.getWebsite().equals("")){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(o.getWebsite()));

            if(intent.resolveActivity(getPackageManager())!= null){
                startActivity(intent);
            }
            else{
                noAppFound("No app to handle web events!");
            }
        }

        if(view.getId() == R.id.partyIconO){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if(o.getParty().equals("Republican") || o.getParty().equals("Republican Party")){
                intent.setData(Uri.parse("https://www.gop.com"));
            }
            else if(o.getParty().equals("Democrat") || o.getParty().equals("Democratic")
                    || o.getParty().equals("Democratic Party")){
                intent.setData(Uri.parse("https://democrats.org"));
            }

            if(intent.resolveActivity(getPackageManager())!= null){
                startActivity(intent);
            }
            else{
                noAppFound("No app to handle web events!");
            }
        }
    }

    public boolean isPackageInstalled(String packageName) {
        try {
            return getPackageManager().getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void noAppFound(String message){
        AlertDialog.Builder noApp = new AlertDialog.Builder(this);
        noApp.setMessage(message);
        noApp.setTitle("No App Found");

        noApp.create().show();
    }
}
