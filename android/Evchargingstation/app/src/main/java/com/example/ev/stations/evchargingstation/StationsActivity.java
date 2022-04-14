package com.example.ev.stations.evchargingstation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StationsActivity extends AppCompatActivity {
    ListView lvStations;
    List<Station> stations = new ArrayList<>();
    StationListAdapter adapter;
    FusedLocationProviderClient client;
    LocationManager locationManager;
    Location myLocation;
    int locationRequestCode = 7;

    RequestQueue queue;

    String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations);

        queue = Volley.newRequestQueue(this);
        client = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        adapter = new StationListAdapter(this, R.layout.station_list_item, stations);

        lvStations = findViewById(R.id.lv_stations);
        lvStations.setAdapter(adapter);
        lvStations.setOnItemClickListener((adapterView, view, position, l) -> navigate(adapter.getItem(position)));


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipAddress = extras.getString("ipAddress");  // if not returns null
            if (ipAddress == null) {
                makeSnackBar("invalid data passed");
            }
        }
        refresh();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isLocationEnabled()) {
                getCurrentLocationAndRefreshStationsList();
            } else {
                Log.e("stations", "Gps is off");
                Snackbar.make(findViewById(android.R.id.content), "Turn on Gps and come back", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Log.e("stations", "Location permission denied");
            Snackbar.make(findViewById(android.R.id.content), "Location permission denied", Snackbar.LENGTH_LONG).show();
        }
    }

    void refresh() {
        if (isLocationPermissionGranted() && isLocationEnabled()) {
            getCurrentLocationAndRefreshStationsList();
        } else {
            requestLocationPermission();
        }
    }

    boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    boolean isLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
    }

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    void getCurrentLocationAndRefreshStationsList() {
        client.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                myLocation = location;
                getStationsData();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void navigate(Station item) {
        Uri uri = Uri.parse(String.format("google.navigation:q=%f,%f", item.latitude, item.longitude));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    void getStationsData() {
        makeSnackBar("loading stations list");
        String url = "http://" + ipAddress + Constants.stationsUrl;
        Log.d("stations", url);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        Log.d("stations", response);
                        if (object.getBoolean("success")) {
                            JSONArray _stations = object.getJSONArray("stations");
                            if (_stations.length() > 0) {
                                Log.d("stations", _stations.toString());
                                adapter.clear();
                                for (int i = 0; i < _stations.length(); i++) {
                                    Station _station = new Station(
                                            _stations.getJSONObject(i).getString("name"),
                                            _stations.getJSONObject(i).getDouble("lat"),
                                            _stations.getJSONObject(i).getDouble("lon"),
                                            _stations.getJSONObject(i).getInt("plugs"),
                                            0F
                                    );
                                    _station.updateDistance(myLocation);
                                    adapter.add(_station);
                                }
                            } else {
                                makeSnackBar("no data found");
                            }
                        } else {
                            makeSnackBar(object.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> makeSnackBar(error.toString())
        );
        queue.cancelAll(0);
        queue.add(request);
    }

    void makeSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}