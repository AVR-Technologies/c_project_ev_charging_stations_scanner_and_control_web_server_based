package com.example.ev.stations.evchargingstation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MenuActivity extends AppCompatActivity {
    Button btOpenControl, btSearchStations;

    String ipAddress, username, password, portName;
    int balance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btOpenControl = findViewById(R.id.bt_open_control);
        btSearchStations = findViewById(R.id.bt_search_stations);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipAddress = extras.getString("ipAddress");  // if not returns null
            username = extras.getString("username");    // if not returns null
            password = extras.getString("password");    // if not returns null
            balance = extras.getInt("balance");         // if not returns 0
            if(ipAddress == null || username == null || password == null) {
                makeSnackBar("invalid data passed");
            }
            if(balance < 10) {
                makeSnackBar("low balance please recharge");
            }
        }

        btOpenControl.setOnClickListener(v-> scan());
        btSearchStations.setOnClickListener(v-> goToStationsActivity());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                portName = intentResult.getContents();
                goToControlActivity();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    void scan(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }
    void goToControlActivity(){
        Intent intent = new Intent(this, ControlActivity.class);
        intent.putExtra("ipAddress", ipAddress);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("portName", portName);
        intent.putExtra("balance", balance);
        startActivity(intent);
    }
    void goToStationsActivity(){
        Intent intent = new Intent(this, StationsActivity.class);
        intent.putExtra("ipAddress", ipAddress);
        startActivity(intent);
    }

    void makeSnackBar(String message){
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}