package com.uwaterloo.navistore.CyPhy;

import com.cyphymedia.sdk.controller.CyPhy;
import com.cyphymedia.sdk.controller.CyPhyConfig;
import com.cyphymedia.sdk.model.CyPhyDat;
import com.cyphymedia.sdk.model.DraggedMedia;
import com.cyphymedia.sdk.model.NavBeacon;
import com.cyphymedia.sdk.model.ScannedBeacon;
import com.cyphymedia.sdk.model.ScannedEddystone;

import java.util.Collection;

/**
 * Listens in on certain CyPhy SDK/beacon-related events. Currently, we mainly care about the
 * authentication status event.
 */
public class CyPhyBeaconListener implements com.cyphymedia.sdk.controller.CyPhy.CyPhyListener {

    /**Check CyPhy Media SDK authentication status
     * Check the authentication status for the CyPhy Media SDK key/package name pair. If the
     * authentication is valid, then begin scanning for Bluetooth beacons.
     */
   @Override
    public void CyPhyAuthenticationStatus(CyPhy.AuthenticateStatus authenticateStatus, Exception e) {
        android.util.Log.d("CyPhyBeaconListener", "CyPhyAuthenticationStatus: " + authenticateStatus);
        if (CyPhy.AuthenticateStatus.ValidAuthentication == authenticateStatus) {
            BeaconScanner.getInstance().startBeaconScan();
        }
    }

    @Override
    public void CyPhyBeaconDrag(ScannedBeacon scannedBeacon) {
        android.util.Log.d("CyPhyBeaconListener", "CyPhyBeaconDrag");
    }

    @Override
    public void CyPhyEddystoneDrag(ScannedEddystone scannedEddystone) {
        android.util.Log.d("CyPhyBeaconListener", "CyPhyEddystoneDrag");
    }

    @Override
    public void CyPhyBeaconPush(Collection<CyPhyDat> collection, CyPhyConfig.ScanningMode scanningMode) {
        android.util.Log.d("CyPhyBeaconListener", "CyPhyBeaconPush");
    }

    @Override
    public void CyPhyFunctionException(Exception e) {
        android.util.Log.e("CyPhyBeaconListener", "CyPhyFunctionException: ", e);
    }

    @Override
    public void CyPhyReadyToDrag(int i, boolean b) {
        android.util.Log.d("CyPhyBeaconListener", "CyPhyReadyToDrag");
    }

    @Override
    public void CyPhyWifiDrag(DraggedMedia draggedMedia) {
        android.util.Log.d("CyPhyBeaconListener", "CyPhyWifiDrag");
    }

    @Override
    public void onReceivedBeacons(Collection<ScannedBeacon> collection) {
        android.util.Log.d("CyPhyBeaconListener", "onReceivedBeacons");
    }

    @Override
    public void onReceivedEddystone(Collection<ScannedEddystone> collection) {
        android.util.Log.d("CyPhyBeaconListener", "onReceivedEddystone");
    }

    @Override
    public void cyPhyNavigationBeacon(Collection<NavBeacon> collection) {
        android.util.Log.d("CyPhyBeaconListener", "cyPhyNavigationBeacon");
    }

    @Override
    public void onReceivedCyPhySpecialBeacon(CyPhyDat cyPhyDat) {
        android.util.Log.d("CyPhyBeaconListener", "onReceivedCyPhySpecialBeacon");
    }
}
