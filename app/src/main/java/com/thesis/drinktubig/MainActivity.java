package com.thesis.drinktubig;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thesis.drinktubig.ui.login.LoginPage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("Users");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void getUserLocationPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.e("Location", "Before Method" + "<-- Lat");
            getLocation();
            Log.e("Location", "After Method" + "<-- Lat");
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null){
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this,
                                Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        Log.e("Location", addresses.get(0).getLatitude() + "<-- Lat");
                        Log.e("Location", addresses.get(0).getLongitude() + "<-- Long");
                        Log.e("Location", addresses.get(0).getLocality() + "<-- Locality");
                        Log.e("Location", addresses.get(0).getAddressLine(0) + "<-- Address");
                        Log.e("Location", addresses.get(0).getCountryName() + "<-- Country");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.e("Location", "Location is Null");
                }
            }
        });
    }

    private void checkUserStatus() {

        if (user != null) {
            //Stay if user is logged-in
            String Email = user.getEmail();
            Toast.makeText(this, "Welcome " + Email, Toast.LENGTH_SHORT).show();
        } else {
            //Redirect to login page if no user detected
            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("keep", "false");
            editor.apply();
            startActivity(new Intent(MainActivity.this, LoginPage.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
//        getUserLocationPermission();
    }
}