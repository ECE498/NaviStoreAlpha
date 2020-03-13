package com.uwaterloo.navistore.CyPhy;

import com.cyphymedia.sdk.model.ScannedBeacon;
import com.uwaterloo.navistore.BeaconCoordinates;
import com.uwaterloo.navistore.basicGraphics.DemoView;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Singleton BeaconData class. Receives beacon data from the CyPhy callback functions
 */
public class BeaconData {
    private final int BEACON_DATA_QUEUE_CAPACITY = 10;

    private static BeaconData mData = null;

    private DemoView mDemoView = null;
    private BlockingQueue<ScannedBeacon> mBeaconData;

    private BeaconData() {
        mBeaconData = new ArrayBlockingQueue<>(BEACON_DATA_QUEUE_CAPACITY);
    }

    public static BeaconData getInstance() {
        if (mData == null) {
            mData = new BeaconData();
        }
        return mData;
    }

    public ScannedBeacon getData() throws InterruptedException {
        return mBeaconData.take();
    }

    public void update(ScannedBeacon data) {
//        android.util.Log.d("BeaconData", "update");

        if (null != mDemoView) {
            if (!mDemoView.containsBeacon(data.bid)) {
                mDemoView.registerBeaconDrawing(data.bid);
            }
        }

        try {
            if (BeaconCoordinates.getInstance().isBeaconValid(data.bid)) {
                mBeaconData.add(data);
            }
        } catch (IllegalStateException e) {
            android.util.Log.e("BeaconData", "adding to queue", e);
        }
//        mDemoView.updateBeacon(data);
    }

    public void setDemoView(DemoView newDemoView) {
        mDemoView = newDemoView;
    }
}
