package com.cobanogluhasan.savevisitedplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ListView listView;
   public static ArrayList<LatLng> locations;
   public static ArrayList<String>  place;
   public static   ArrayAdapter<String> arrayAdapter;
   public static SharedPreferences sharedPreferences;

    public static ArrayList<String> latitude = new ArrayList<String>();
    public static ArrayList<String> longitude = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.cobanogluhasan.savevisitedplaces", Context.MODE_PRIVATE);


        place=new ArrayList<String>();
        locations = new ArrayList<LatLng>();

        getSavedInfo();

        listView = (ListView) findViewById(R.id.listView);


       arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, place);

        listView.setAdapter(arrayAdapter);


        arrayAdapter.notifyDataSetChanged();





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i(TAG, "onItemClick: " + String.valueOf(position));

                if(position==0) {
                    view.setBackgroundColor(Color.GRAY);}


                Intent mapsIntent = new Intent(getApplicationContext(), MapsActivity.class);
                mapsIntent.putExtra("number", position);
                startActivity(mapsIntent);
            }

        });



    }

    private void getSavedInfo() {
        place.clear();
        latitude.clear();
        longitude.clear();
        locations.clear();

        Double lati,longi;

        try {

            place = ((ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("place", ObjectSerializer.serialize(new ArrayList<String>()))));
            latitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitude", ObjectSerializer.serialize(new ArrayList<String>())));
            longitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitude", ObjectSerializer.serialize(new ArrayList<String>())));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (place.size()>0 && longitude.size()>0 && latitude.size()>0) {

            if(place.size() == latitude.size() && longitude.size()==place.size()) {

                for (int i=0;i<place.size();i++) {
                    lati = Double.parseDouble(latitude.get(i));
                    longi = Double.parseDouble(longitude.get(i));

                    locations.add(new LatLng(lati,longi));
                }
            }
        }
        else {
            place.add("Add a new place!");
            locations.add(new LatLng(0,0));
        }




    }


}