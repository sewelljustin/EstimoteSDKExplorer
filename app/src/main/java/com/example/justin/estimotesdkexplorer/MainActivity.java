package com.example.justin.estimotesdkexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.eddystone.Eddystone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Map<String, Eddystone> beaconMap;
    private List<Eddystone> beaconList;

    private ListView listView;
    private ArrayAdapter<String> listAdapter;

    /** Scan ID of Eddystone scanner */
    String scanId;

    /** Flag to indicate scanning activity */
    boolean scanning;

    /** Flag to indicate service ready */
    boolean serviceReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconMap = new HashMap<>();
        beaconList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
            @Override
            public void onEddystonesFound(List<Eddystone> list) {
                for (Eddystone eddystone : list) {
                    String macAddress = eddystone.macAddress.toStandardString();
                    if (!beaconMap.containsKey(macAddress)) {
                        beaconMap.put(macAddress, eddystone);
                        beaconList.add(eddystone);

                        listAdapter.add(macAddress);
                    }
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
