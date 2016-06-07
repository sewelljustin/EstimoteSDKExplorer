package com.example.justin.estimotesdkexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Utils;
import com.estimote.sdk.eddystone.Eddystone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;

    /** Scan ID of Eddystone scanner */
    String scanId;

    /** Flag to indicate scanning activity */
    boolean scanning;

    /** Flag to indicate service ready */
    boolean serviceReady;

    /** Views for Activity */
    TextView macAddressTextView;
    TextView distanceTextView;
    TextView proximityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        macAddressTextView = (TextView) findViewById(R.id.macAddressTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        proximityTextView = (TextView) findViewById(R.id.proximityTextView);

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
            @Override
            public void onEddystonesFound(List<Eddystone> list) {
                if (!list.isEmpty()) {
                    macAddressTextView.setText(list.get(0).macAddress.toStandardString());
                    distanceTextView.setText(String.valueOf(Utils.computeAccuracy(list.get(0))));
                    proximityTextView.setText(String.valueOf(Utils.computeProximity(list.get(0))));
                }

            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                serviceReady = true;

                if (!scanning) {
                    startScanning();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (serviceReady && !scanning) {
            startScanning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (scanning) {
            stopScanning();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        beaconManager.disconnect();
    }

    private void startScanning() {
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        scanId = beaconManager.startEddystoneScanning();
        scanning = true;
    }

    private void stopScanning() {
        beaconManager.stopEddystoneScanning(scanId);
        scanning = false;
    }
}
