package com.example.justin.estimotesdkexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconMap = new HashMap<>();
        beaconList = new ArrayList<>();

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
                    @Override
                    public void onEddystonesFound(List<Eddystone> list) {
                        for (Eddystone eddystone : list) {
                            String macAddress = eddystone.macAddress.toStandardString();
                            if (!beaconMap.containsKey(macAddress)) {
                                beaconMap.put(macAddress, eddystone);
                                beaconList.add(eddystone);
                            }
                        }
                    }
                });

                beaconManager.startEddystoneScanning();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }
}
