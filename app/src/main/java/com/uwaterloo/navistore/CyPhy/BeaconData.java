package com.uwaterloo.navistore.CyPhy;

import com.cyphymedia.sdk.model.ScannedBeacon;
import com.uwaterloo.navistore.basic_graphics.DemoView;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**TODO: This can be changed to how we want to handle/manage the Bluetooth beacon data
 * Singleton BeaconData class. Receives beacon data from the CyPhy callback functions
 */
public class BeaconData {
    private final int BEACON_DATA_QUEUE_CAPACITY = 10;

    private static BeaconData mData = null;

    private DemoView mDemoView = null;
    private Queue<ScannedBeacon> mBeaconData;

    private BeaconData() {
        mBeaconData = new ArrayBlockingQueue<>(BEACON_DATA_QUEUE_CAPACITY);
    }

    public static BeaconData getInstance() {
        if (mData == null) {
            mData = new BeaconData();
        }
        return mData;
    }

    public void update(ScannedBeacon data) {
        android.util.Log.d("BeaconData", "update");

        try {
            mBeaconData.add(data);
        } catch (IllegalStateException e) {
            android.util.Log.e("BeaconData", "adding to queue", e);
        }

        if (!mDemoView.containsBeacon(data.bid)) {
            mDemoView.registerBeaconDrawing(data.bid);
            android.util.Log.d("BeaconData", "registered beacon drawing");
        }
        mDemoView.invalidate();

        // TODO: do something with data so it doesn't overflow buffer
        mBeaconData.remove();
    }

    public void setDemoView(DemoView newDemoView) {
        mDemoView = newDemoView;
    }
}
