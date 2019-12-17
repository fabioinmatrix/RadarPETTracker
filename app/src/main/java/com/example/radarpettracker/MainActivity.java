package com.example.radarpettracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radarpettracker.database.RegistroRepository;
import com.example.radarpettracker.model.Registro;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1000;
    private TextView textViewLocation;
    private TextView textViewImei;
    private TextView textViewTimeline;
    private TextView textViewPhoneNumber;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location firstLocation;
    private String IMEI;
    private String timelineString;
    private String latitude;
    private String longitude;
    private long timeline;
    private SimpleDateFormat simpleDateFormat;
    private Timer timer;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLocation = (TextView) findViewById(R.id.txt_location);
        textViewImei = (TextView) findViewById(R.id.txt_imei);
        textViewTimeline = (TextView) findViewById(R.id.txt_timeline);
        textViewPhoneNumber = (TextView) findViewById(R.id.txt_phone_number);
        imageView = (ImageView) findViewById(R.id.iv_wifi);
        textView = (TextView) findViewById(R.id.txt_please_wait);

        textView.setVisibility(View.VISIBLE);

        // Get IMEI number...
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        IMEI = telephonyManager.getDeviceId();
        if (IMEI != null) {
            textViewImei.setText("License number: " + IMEI);
        } else {
            Toast.makeText(this, "License number null...", Toast.LENGTH_SHORT).show();
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            buildLocationRequest();
            builLocationCallBack();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }

        // Thread for save register in database
        saveRegister();

        // Is connected?
        isConnected();
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(0);
    }

    private void builLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (firstLocation == null) {
                        firstLocation = location;
                        textViewLocation.setText("Geographical coordinates: " + String.valueOf(location.getLatitude()) + " / " + String.valueOf(location.getLongitude()));
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                    } else {
                        textViewLocation.setText("Geographical coordinates: " + String.valueOf(location.getLatitude()) + " / " + String.valueOf(location.getLongitude()));
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                    }
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Agreed...", Toast.LENGTH_SHORT);
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Can't have position...", Toast.LENGTH_SHORT);
                    }
                }
            }
        }
    }

    public void saveRegister() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Get date and time...
                        timeline = System.currentTimeMillis();
                        simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy - hh:mm:ss a");
                        timelineString = simpleDateFormat.format(timeline);
                        textViewTimeline.setText("Register: " + timelineString);

                        // Save register in database
                        RegistroRepository registroRepository = new RegistroRepository(MainActivity.this);
                        registroRepository.insert(new Registro(IMEI, timelineString, latitude, longitude));
                    }
                });
            }
            // Time (interval) for save register in database (aguarda 10 segundos para iniciar a gravação após a inicialização da Thread e repete a gravação após 30 segundos)
        }, 10000, 30000);
    }

    public void isConnected() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        // Is connected?...
                        if (networkInfo != null && networkInfo.isConnected()) {
                            textView.setTextColor(Color.parseColor("#00518c"));
                            textView.setText("Connected...");
                            int res1 = getResources().getIdentifier("wifi_symbol_connect", "drawable",
                                    "com.example.radarpettracker");
                            imageView.setImageResource(res1);
                        } else {
                            textView.setTextColor(Color.parseColor("#c23236"));
                            textView.setText("Disconnected...");
                            int res2 = getResources().getIdentifier("wifi_symbol_disconnect", "drawable",
                                    "com.example.radarpettracker");
                            imageView.setImageResource(res2);
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(textViewPhoneNumber.getText().toString(), null, "Attention! Radar PET is disconnected..." + "\n" + "Last known coordinate: " + latitude + " and " + longitude, null, null);
                        }
                    }
                });
            }
            // Time (interval) for check connection (aguarda 60 segundos para iniciar a verificicação de conexão após a inicialização da Thread, repete a verificação a cada 3 minutos)
        }, 20000, 20000);
    }
}
