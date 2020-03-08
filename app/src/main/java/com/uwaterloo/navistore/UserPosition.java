package com.uwaterloo.navistore;

import com.cyphymedia.sdk.model.ScannedBeacon;
import com.uwaterloo.navistore.CyPhy.BeaconData;
import com.uwaterloo.navistore.basic_graphics.DemoView;
import com.uwaterloo.navistore.basic_graphics.UserDrawing;

public class UserPosition implements Runnable {

    private DemoView mDemoView;
    private UserDrawing mUserDrawing;

    public UserPosition(DemoView demoView, UserDrawing userDrawing) {
        mDemoView = demoView;
        mUserDrawing = userDrawing;
    }

    private void updateUserPosition(ScannedBeacon beacon) {
        float coordinateX = 500.0f;
        float coordinateY = 500.0f;

        mUserDrawing.setCoordinates(coordinateX, coordinateY);
        mDemoView.invalidate();
    }

    @Override
    public void run() {
        while (true) {
            ScannedBeacon beaconData = null;
            try {
                beaconData = BeaconData.getInstance().getData();
            } catch (InterruptedException e) {
                android.util.Log.e("UserPosition", "getting beacon data", e);
            }

            if (null != beaconData) {
                updateUserPosition(beaconData);
            }
        }
    }
}
