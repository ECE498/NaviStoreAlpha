package com.uwaterloo.navistore.CyPhy;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cyphymedia.sdk.controller.CyPhy;
import com.cyphymedia.sdk.controller.CyPhyConfig;
import com.cyphymedia.sdk.utility.Constants;

import java.io.IOException;

/**BeaconScanner Singleton class
 * Manages all beacon scanning-related components, and abstracts away CyPhy SDK-related components.
 */
public class BeaconScanner {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private static BeaconScanner mScanner = null;

    private Activity mActivity;

    private BeaconScanner() {}

    /**
     * Initialize required CyPhyConfig for CyPhy SDK, parameters as followed
     * <p>
     * 1. Scanning : Background Between Scan Interval (in ms, default 0)
     * - Used when app is not killed, but app running in background
     * - The duration between each scanning, for non-scanning state
     * <p>
     * 2. Scanning : Background Scan Interval (in ms, default 1000)
     * - Used when app is not killed, but app running in background
     * - The duration for each scanning, for scanning state
     * <p>
     * 3. Scanning : Foreground Between Scan Interval (in ms, default 0)
     * - Used when app is not killed, and app running in foreground
     * - The duration between each scanning, for non-scanning state
     * <p>
     * 4. Scanning : Foreground Scan Interval (in ms, default 1000)
     * - Used when app is not killed, and app running in foreground
     * - The duration for each scanning, for scanning state
     * <p>
     * 5. Scanning : Offscreen Scan Interval (in ms, default 5000)
     * - Used when app is not killed or not killed, foreground or background (Service)
     * - The duration for each scanning result handling
     * <p>
     * 6. Scanning : BLE repair Count (integer, default 12
     * - Used for part of Android devices which have BLE Stack unclear-able problem
     * - Number of times for 0 detected Beacon, it will try to fix the problem
     * <p>
     * 7. Scanning : Beacon Cache Timeout (in ms, defaul 2 * 60 * 1000)
     * - Time interval for caching a beacon to consider it as exists
     * <p>
     * 8.Drag Sensing : Drag Delay (in ms, default 1000)
     * - A time lock for each drag response
     * - To prevent some motion sensitive Android devices keep returning results for CyPhy Drag callback
     * <p>
     * 9. Network : Failure Limit (integer, default -1 [unlimited])
     * - Number of times for acceptable failure for beacon data retrieval from cloud
     * <p>
     * 10. Network : Failure Retry Timeout (in ms, default 3 * 60 * 60 * 1000)
     * - Time interval for system to allow the failed beacon to retrieve their data again
     * <p>
     * 11. Network : Network Cycle (in ms, default : 5000)
     * - Time interval for each time doing beacon data retrieval for new coming beacons
     * z
     *
     * @return Config completed CyPhyConfig Object
     */
    private CyPhyConfig initCyPhyConfig() {
        CyPhyConfig mConfig = new CyPhyConfig();
        mConfig.mBeaconScanningConfig.setBackgroundBetweenScanInterval(0);
        mConfig.mBeaconScanningConfig.setBackgroundScanInterval(10 * 1000);
        mConfig.mBeaconScanningConfig.setForegroundBetweenScanInterval(0);
        mConfig.mBeaconScanningConfig.setForegroundScanInterval(3 * 1000);
        mConfig.mBeaconScanningConfig.setBeaconTimeoutInterval(10 * 1000);
        mConfig.mBeaconScanningConfig.setOffscreenScanInterval(4 * 1000);
        mConfig.mBeaconScanningConfig.setServiceRestingInterval(60 * 1000);
        mConfig.mBeaconScanningConfig.setServiceScanInterval(10 * 1000);
        mConfig.mBeaconScanningConfig.setBleRepairCount(12);
        mConfig.mBeaconScanningConfig.setScanningMode(new CyPhyConfig.ScanningMode(CyPhyConfig.BeaconScanningConfig.MODE_ALL));
        mConfig.mDragSensingConfig.setDragDelay(1000);
        mConfig.mNetworkConfig.setFailureLimit(-1);
        mConfig.mNetworkConfig.setFailureRetryTimeout(3 * 60 * 60 * 1000);
        mConfig.mNetworkConfig.setNetworkCycle(5000);
        mConfig.mNetworkConfig.enableCustomDomain(true);
        mConfig.mNetworkConfig.setApiDomain("https://apia05.cyphy.com/" /*Your API Domain*/);
        mConfig.mNetworkConfig.setCloudDomain("https://clouda05.cyphy.com/"  /*Your Cloud Domain*/);
        mConfig.mNetworkConfig.setShareDomain("https://sharea05.cyphy.com/" /*Your Share Domain*/);
        mConfig.mExtraConfig.setAutoNotification(false);
        return mConfig;
    }

    public static BeaconScanner getInstance() {
        if (null == mScanner) {
            mScanner = new BeaconScanner();
        }
        return mScanner;
    }

    /**Initialize the Bluetooth beacon scanner
     * Initialize the CyPhy SDK components and begin the SDK authentication process.
     *
     * @param activity - Activity: Android Activity to get Context and to request permissions
     *                 for ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION
     */
    public void init (Activity activity) {
        this.mActivity = activity;

        try {
            CyPhyBeaconListener beaconListener = new CyPhyBeaconListener();
            CyPhyConfig configuration = initCyPhyConfig();

            // CyPhy SDK Initialization
            CyPhy.initSDK(mActivity.getApplicationContext(), "com", beaconListener, configuration);
            CyPhy.getInstance().setCyPhyPushMode(Constants.PushMode.ALL);
            // (a) Basic Function (i) CyPhy SDK Authentication
            CyPhy.getInstance().auth(mActivity.getApplicationContext());
        } catch (PackageManager.NameNotFoundException exception) {
            android.util.Log.e("BluetoothDemoActivity", "Initializing CyPhy Beacon data: ", exception);
        }
    }

    /**Start the beacon scan
     * Begin the scanning of the Bluetooth beacons. This will periodically trigger the callback
     * functions from CyPhyBeaconReceiver. This should only be called once authentication of the
     * CyPhy Media SDK is complete.
     */
    public void startBeaconScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Here, thisActivity is the current activity
            if ((ContextCompat.checkSelfPermission(mActivity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(mActivity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        // (a) Basic Function (vi) CyPhy Bluetooth Configuration Initialization
        try {
            CyPhy.getInstance().initBluetooth(mActivity.getApplicationContext());
        } catch (IOException ignored) {
        }
        // (a) Basic Function (ii) CyPhy Beacon Scanning Resume
        try {
            CyPhy.getInstance().resumeBeaconScan();
        } catch (IOException ignored) {
        }
        // (a) Basic Function (iv) CyPhy Push Resume
        /**
         * To be noted,
         * Resume CyPhy Foreground = enable foreground (background will follow original setting)
         * Resume CyPhy Background = enable foreground + background
         */
        try {
            CyPhy.getInstance().resumeCyPhyForegroundPush();
        } catch (IOException | PackageManager.NameNotFoundException ignored) {
        }
        try {
            CyPhy.getInstance().resumeCyPhyBackgroundPush();
        } catch (IOException | PackageManager.NameNotFoundException ignored) {
        }
        try {
            CyPhy.getInstance().resumeCyPhyService();
        } catch (Exception ignored) {
        }
    }
}
