package com.example.ev.stations.evchargingstation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class ControlActivity extends AppCompatActivity {
    RadioGroup timeGroup;
    RadioButton rTime10, rTime20, rTime30, rTime60;
    Button btSend;
    TextView tvBalance;

    RequestQueue queue;
    AlertDialog dialog;

    String ipAddress, username, password, portName;
    int balance;

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        queue = Volley.newRequestQueue(this);

        timeGroup = findViewById(R.id.rg_time);
        rTime10 = findViewById(R.id.r_time_10);
        rTime20 = findViewById(R.id.r_time_20);
        rTime30 = findViewById(R.id.r_time_30);
        rTime60 = findViewById(R.id.r_time_60);
        tvBalance = findViewById(R.id.tv_balance);
        btSend = findViewById(R.id.bt_send);

        btSend.setOnClickListener(v -> {
            switch (timeGroup.getCheckedRadioButtonId()) {
                case R.id.r_time_10:
                    if (balance >= 10) {
                        makeDialog("10 ₹ will be debited for 10 minutes of charging.", 10);
                    } else {
                        makeSnackBar("Insufficient balance");
                    }
                    break;
                case R.id.r_time_20:
                    if (balance >= 20) {
                        makeDialog("20 ₹ will be debited for 20 minutes of charging.", 20);
                    } else {
                        makeSnackBar("Insufficient balance");
                    }
                    break;
                case R.id.r_time_30:
                    if (balance >= 30) {
                        makeDialog("30 ₹ will be debited for 30 minutes of charging.", 30);
                    } else {
                        makeSnackBar("Insufficient balance");
                    }
                    break;
                case R.id.r_time_60:
                    if (balance >= 60) {
                        makeDialog("60 ₹ will be debited for 60 minutes of charging.", 60);
                    } else {
                        makeSnackBar("Insufficient balance");
                    }
                    break;
                default:
                    makeSnackBar("please select time from above");
            }
        });

        disableAllWidgets();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipAddress = extras.getString("ipAddress");  // if not returns null
            username = extras.getString("username");    // if not returns null
            password = extras.getString("password");    // if not returns null
            portName = extras.getString("portName");    // if not returns null
            balance = extras.getInt("balance");         // if not returns 0

            tvBalance.setText(Integer.toString(balance));

            if (ipAddress == null || username == null || password == null || portName == null) {
                makeSnackBar("invalid data passed");
            } else {
                enableWidgetsAccordingToBalance();
            }
            if (balance < 10) {
                makeSnackBar("low balance please recharge");
            }
        } else makeSnackBar("no data passed");

    }

    void disableAllWidgets() {
        rTime10.setEnabled(false);
        rTime20.setEnabled(false);
        rTime30.setEnabled(false);
        rTime60.setEnabled(false);
        btSend.setEnabled(false);

        rTime10.setChecked(false);
        rTime20.setChecked(false);
        rTime30.setChecked(false);
        rTime60.setChecked(false);
    }

    @SuppressLint("SetTextI18n")
    void enableWidgetsAccordingToBalance() {
        if (balance >= 10) {
            rTime10.setEnabled(true);
            btSend.setEnabled(true);
        }
        if (balance >= 20) rTime20.setEnabled(true);
        if (balance >= 30) rTime30.setEnabled(true);
        if (balance >= 60) rTime60.setEnabled(true);
    }

    void requestCharging(int value) {
        String url = "http://" + ipAddress + Constants.chargeUrl +
                "username=" + username +
                "&password=" + password +
                "&port=" + portName +
                "&value=" + value;
        Log.d("url:", url);
        @SuppressLint("SetTextI18n")
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        makeSnackBar(object.getString("message"));
                        if (object.getBoolean("success")) {
                            balance = object.getInt("balance");
                            tvBalance.setText(Integer.toString(balance));
                            disableAllWidgets();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        makeSnackBar("parse error");
                    }
                },
                error -> makeSnackBar(error.toString()));
        queue.cancelAll(0);
        queue.add(stringRequest);
    }

    void makeDialog(String message, int value) {
        if (dialog != null) dialog.cancel();
        dialog = new AlertDialog.Builder(this)
                .setTitle("Start charging")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, (_dialog, which) -> {
                    makeSnackBar("Starting charger for " + value + " minutes");
                    requestCharging(value);
                })
                .setNegativeButton(android.R.string.no, (_dialog, which) -> {
                })
                .show();
    }

    void makeSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                .show();
    }
}