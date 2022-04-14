package com.example.ev.stations.evchargingstation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

//TODO: qr scan webpage
//https://stackoverflow.com/questions/8831050/android-how-to-read-qr-code-in-my-application
//https://www.geeksforgeeks.org/how-to-read-qr-code-using-zxing-library-in-android/
public class LoginActivity extends AppCompatActivity {
    EditText etIpAddress, etUsername, etPassword;
    Button btLogin;
    RequestQueue queue;
    String ipAddress, username, password;
    int balance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(this);

        initAllWidgets();
    }
    void initAllWidgets(){
        etIpAddress = findViewById(R.id.et_ip_address);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btLogin = findViewById(R.id.bt_login);
        btLogin.setOnClickListener(v -> login());
    }

    void login() {
        ipAddress = etIpAddress.getText().toString();
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        if (ipAddress.isEmpty() || username.isEmpty() || password.isEmpty()) {
            makeSnackBar("All fields are necessary");
        } else {
            makeSnackBar("Wait for login");

            String url = "http://" + ipAddress + Constants.loginUrl + "username=" + username + "&password=" + password;
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    url,
                    response -> {
                        Log.d("response: ",response);
                        try {
                            JSONObject object = new JSONObject(response);

                            if (object.getBoolean("success")) {
                                balance = object.getInt("balance");
                                makeToast(object.getString("message"));
                                goToMenuActivity();
                            }
                            else makeSnackBar(object.getString("message"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            makeSnackBar("parse error");
                        }
                    },
                    error -> makeSnackBar("Request failed, check server reachable."));
            queue.cancelAll(0);
            queue.add(stringRequest);
        }
    }

    void goToMenuActivity(){
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("ipAddress", ipAddress);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("balance", balance);
        startActivity(intent);
    }

    void makeSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}