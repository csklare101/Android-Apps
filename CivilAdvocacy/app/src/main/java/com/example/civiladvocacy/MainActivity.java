package com.example.civiladvocacy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FusedLocationProviderClient mFusedLocationClient;
    private static String locationString = "Unspecified Location";
    private String cityString;
    private String zipString;
    private final ArrayList<Official> officialList = new ArrayList<>();

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private RecyclerView recyclerView;
    private OfficialAdapter officalAdapter;

    private TextView locationView;

    private static final int LOCATION_REQUEST = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        officalAdapter = new OfficialAdapter(officialList, this);

        recyclerView.setAdapter(officalAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*
        if(officialList.size() == 0){
            Official test = new Official("Tester John", "President of Testing Applications", "Testing Party",
                    "", "https://twitter.com/BarackObama", "", "123 sample st, chicago il", "(555)555-5555",
                    "csklare101@gmail.com", "https://www.google.com/", "");
            officialList.add(test);
            officalAdapter.notifyItemRangeChanged(0, officialList.size());
        }
         */

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationView = findViewById(R.id.locationText);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleResult);
        determineLocation();
    }

    public void addOfficials(ArrayList<Official> ofa){
        officialList.clear();
        officialList.addAll(ofa);
        officalAdapter.notifyItemRangeChanged(0,officialList.size());
    }

    public void handleResult(ActivityResult result){
        if(result == null || result.getData() == null){
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.aboutOpt){
            Intent intent = new Intent(this, AboutActivity.class);
            activityResultLauncher.launch(intent);
        }
        else if(item.getItemId() == R.id.changeLocOpt){
            AlertDialog.Builder askLoc = new AlertDialog.Builder(this);
            askLoc.setTitle("Enter Address");
            askLoc.setMessage("Enter a city/state, or a zip code");
            final EditText manualAdd = new EditText(this);
            manualAdd.setInputType(InputType.TYPE_CLASS_TEXT);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(manualAdd);
            askLoc.setView(layout);

            if(hasNetworkConnection()) {
                askLoc.setPositiveButton("OK", (dialog, which) -> {
                    CivicInformation.downloadInformation(this, manualAdd.getText().toString());
                });
                askLoc.setNegativeButton("Cancel", (dialog, which) -> {
                });
                askLoc.create().show();
            }
            else{
                AlertDialog.Builder noInternet = new AlertDialog.Builder(this);
                noInternet.setTitle("No Network Connection");
                noInternet.setMessage("Civil officials information cannot be accesed without internet!");
                noInternet.create().show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void getCivilData(){
        if(hasNetworkConnection() ){
            CivicInformation.downloadInformation(this, zipString);
        }
        else{
            AlertDialog.Builder noInternet = new AlertDialog.Builder(this);
            noInternet.setTitle("No Network Connection");
            noInternet.setMessage("Civil officials information cannot be accesed without internet!");
            noInternet.create().show();
        }
    }

    public void setLocationText(String city, String loc){
        cityString = city;
        locationString = loc;
        locationView.setText(loc);
    }

    private void determineLocation(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                locationString = getLocation(location);
                //Toast.makeText(MainActivity.this, locationString, Toast.LENGTH_LONG).show();
                locationView.setText(locationString);
                getCivilData();
            }
        }).addOnFailureListener(this, e ->
                Toast.makeText(MainActivity.this,
                        e.getMessage(), Toast.LENGTH_LONG).show());

    }

    private String getLocation(Location loc){
        StringBuilder sb = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String zip = addresses.get(0).getPostalCode();
            cityString = city;
            zipString = addresses.get(0).getPostalCode();
            String state = addresses.get(0).getAdminArea();
            sb.append(String.format(
                    Locale.getDefault(),
                    "%s, %s, %s",
                    city, state, zip));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                   Toast.makeText(MainActivity.this, "Loaction permission was denied, cannot determine address", Toast.LENGTH_LONG);
                }
            }
        }
    }
    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Official currentOfficial = officialList.get(pos);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("LOCATION", locationString);
        intent.putExtra("SHOW_OFFICIAL", currentOfficial);
        activityResultLauncher.launch(intent);
    }

    public boolean hasNetworkConnection(){
        ConnectivityManager cm = getSystemService(ConnectivityManager.class);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return(ni != null && ni.isConnectedOrConnecting());
    }
}