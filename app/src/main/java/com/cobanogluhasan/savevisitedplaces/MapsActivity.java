package com.cobanogluhasan.savevisitedplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
   private ArrayList<String> latitude = new ArrayList<String>();
    private ArrayList<String> longitude = new ArrayList<String>();

    SharedPreferences sharedPreferences;

    public void centerOnLocation(Location location, String title) {

        if(location!=null) {
            mMap.clear();
            LatLng usersLocation = new LatLng(location.getLatitude(), location.getLongitude());


            mMap.addMarker(new MarkerOptions().position(usersLocation).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usersLocation, 15));
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sharedPreferences = this.getSharedPreferences("com.cobanogluhasan.savevisitedplaces", Context.MODE_PRIVATE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);


        getIntent();

        int number = getIntent().getIntExtra("number", 1);

       // Toast.makeText(this, String.valueOf(number), Toast.LENGTH_SHORT).show();

        if(number==0) {
            //zoom in
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centerOnLocation(location, "users Location");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            if(Build.VERSION.SDK_INT <23) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
            }

            else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centerOnLocation(lastKnownLocation, "users Location");
                }

            }


        }

        else {
            getIntent();
            int intentNumber = getIntent().getIntExtra("number",0);

            Location placeLoc = new Location(LocationManager.GPS_PROVIDER);

            placeLoc.setLatitude(Double.parseDouble(MainActivity.latitude.get(intentNumber)));
            placeLoc.setLongitude(Double.parseDouble(MainActivity.longitude.get(intentNumber)));
            centerOnLocation(placeLoc, MainActivity.place.get(intentNumber));

        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED) {

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

               locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerOnLocation(lastKnownLocation, "users Location");

            }
        }

    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String adress = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if(addressList != null && addressList.get(0) != null) {
                Log.i(TAG, "onMapLongClick: " + addressList.get(0));

              if(addressList.get(0).getThoroughfare() != null && addressList.get(0).getSubAdminArea() != null) {
                    adress = addressList.get(0).getThoroughfare() + addressList.get(0).getSubAdminArea();
                }

                else {
                    if(addressList.get(0).getThoroughfare() != null || addressList.get(0).getLocality() != null || addressList.get(0).getAdminArea() != null) {
                        adress = addressList.get(0).getThoroughfare() +" " + addressList.get(0).getLocality() +" " + addressList.get(0).getAdminArea();
                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if(adress.equals("")) {
            SimpleDateFormat simpleDateFormat =new SimpleDateFormat("HH:mm dd/MM/yyyy");
            adress += simpleDateFormat.format(new Date());
        }


        mMap.addMarker(new MarkerOptions().position(latLng).title(adress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        MainActivity.place.add(adress);
        MainActivity.locations.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();


        try {


            for (LatLng coordinats:MainActivity.locations) {
                latitude.add(Double.toString(coordinats.latitude));
                longitude.add(Double.toString(coordinats.longitude));
            }


            sharedPreferences.edit().putString("place",   ObjectSerializer.serialize(MainActivity.place)).apply();
            sharedPreferences.edit().putString("latitude",   ObjectSerializer.serialize(latitude)).apply();
            sharedPreferences.edit().putString("longitude",   ObjectSerializer.serialize(longitude)).apply();



            Log.i(TAG, "onMapLongClick: obj" + ObjectSerializer.serialize(MainActivity.place));
            Log.i(TAG, "onMapLongClick: obj" + ObjectSerializer.serialize(latLng.longitude));

        } catch (Exception e) {
            e.printStackTrace();
        }




        Toast.makeText(this, "Place added!!", Toast.LENGTH_SHORT).show();



    }
}