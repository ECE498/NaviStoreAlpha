package com.uwaterloo.navistore.CyPhy;

import com.cyphymedia.sdk.model.NavBeacon;
import com.cyphymedia.sdk.model.ScannedBeacon;
import com.cyphymedia.sdk.service.CyPhyEddystoneUID;
import com.cyphymedia.sdk.service.CyPhyServiceReceiver;
import com.google.gson.Gson;

import java.util.Collection;

/**
 * Receives Bluetooth beacon data. This includes data like ID, battery, RSSI, distance
 */
public class CyPhyBeaconReceiver extends CyPhyServiceReceiver {

    public CyPhyBeaconReceiver() {
        super();
    }

    @Override
    public void onReceivedBeacons(final Collection<ScannedBeacon> collection) {
        android.util.Log.d("CyPhyBeaconReceiver", "Service :: onReceivedBeacons.size = " + collection.size());
        for(ScannedBeacon sb : collection){
            android.util.Log.d("CyPhyBeaconReceiver", "Service :: sb = " + new Gson().toJson(sb));
            BeaconData.getInstance().update(sb);
        }
    }

    @Override
    public void cyPhyNavigationBeacon(Collection<NavBeacon> collection) {
        android.util.Log.d("CyPhyBeaconReceiver", "Service :: cyPhyNavigationBeacon.size = " + collection.size());
        for(NavBeacon nb : collection){
            android.util.Log.d("CyPhyBeaconReceiver", "Service :: nb = " + new Gson().toJson(nb));
        }
    }

    @Override
    protected void onReceivedEddystone(Collection<CyPhyEddystoneUID> collection) {
        android.util.Log.d("CyPhyBeaconReceiver", "Service :: onReceivedEddystone.size = " + collection.size());
        for(CyPhyEddystoneUID sb : collection){
            android.util.Log.d("CyPhyBeaconReceiver", "Service :: es = " + new Gson().toJson(sb));
        }
    }

    @Override
    public void onAlive() {
    }
}
