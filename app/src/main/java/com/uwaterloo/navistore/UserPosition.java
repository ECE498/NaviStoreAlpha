package com.uwaterloo.navistore;

import android.renderscript.RenderScript;

import com.cyphymedia.sdk.model.ScannedBeacon;
import com.uwaterloo.navistore.CyPhy.BeaconData;
import com.uwaterloo.navistore.basic_graphics.DemoView;
import com.uwaterloo.navistore.basic_graphics.UserDrawing;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class UserPosition implements Runnable {

    private DemoView mDemoView;
    private UserDrawing mUserDrawing;

//    private HashMap<String, ScannedBeacon> mData;
    private PriorityQueue<ScannedBeacon> mBeaconData;
    private ScannedBeacon[] mClosestBeacons;
    private float[] mBeaconWeights;

    private int mNumClosestBeacons = 0;
    private float mCoordinateX = 0.0f;
    private float mCoordinateY = 0.0f;

    public UserPosition(DemoView demoView, UserDrawing userDrawing) {
        mDemoView = demoView;
        mUserDrawing = userDrawing;
        // Compare distances such that the head of the queue is the closest beacon
        mBeaconData = new PriorityQueue<>(20, new Comparator<ScannedBeacon>() {
            @Override
            public int compare(ScannedBeacon scannedBeacon, ScannedBeacon t1) {
//                return Math.round(Float.parseFloat(scannedBeacon.distance) - Float.parseFloat(t1.distance));
                float sb_distance = Float.parseFloat(scannedBeacon.distance);
                float t1_distance = Float.parseFloat(t1.distance);
                if (sb_distance > t1_distance) {
                    return 1;
                } else if (sb_distance == t1_distance) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        mClosestBeacons = new ScannedBeacon[4];
        mBeaconWeights = new float[4];
    }

    private void updateUserPosition() {
        getClosestBeacons();
        calculatePosition();
        repopulateQueue();
        mUserDrawing.setCoordinates(mCoordinateX, mCoordinateY);
        mDemoView.invalidate();
    }

    private void calculatePosition() {
        float maxWeights = 0.0f;
        for (int index = 0; index < mNumClosestBeacons; index++) {
            mBeaconWeights[index] = Float.parseFloat(mClosestBeacons[index].distance);
//            mBeaconWeights[index] *= mBeaconWeights[index];
            maxWeights += mBeaconWeights[index];
        }

        float centerX = 0.0f;
        float centerY = 0.0f;
        for (int index = 0; index < mNumClosestBeacons; index++) {
            mBeaconWeights[index] = (maxWeights - mBeaconWeights[index]) / maxWeights;

            centerX += BeaconCoordinates.getInstance().getCoordinateX(mClosestBeacons[index].bid);
            centerY += BeaconCoordinates.getInstance().getCoordinateY(mClosestBeacons[index].bid);
        }

        centerX /= mNumClosestBeacons;
        centerY /= mNumClosestBeacons;

        mCoordinateX = centerX;
        mCoordinateY = centerY;

        for (int index = 0; index < mNumClosestBeacons; index++) {
            mCoordinateX += (BeaconCoordinates.getInstance().getCoordinateX(mClosestBeacons[index].bid) - centerX) * mBeaconWeights[index];
            mCoordinateY += (BeaconCoordinates.getInstance().getCoordinateY(mClosestBeacons[index].bid) - centerY) * mBeaconWeights[index];
        }

        quantizeCoordinates();
    }

    private void quantizeCoordinates() {
        mCoordinateX = Math.round((mCoordinateX - 25) / 250.0) * 250 + 25;
        mCoordinateY = Math.round((mCoordinateY - 25) / 250.0) * 250 + 25;
    }

    // Get closest beacons to perform positioning algorithm on; may be equal to or less then array length
    private void getClosestBeacons() {
        mNumClosestBeacons = 0;
        for (int index = 0; index < mClosestBeacons.length; index++) {
            mClosestBeacons[index] = mBeaconData.poll();

            if (null == mClosestBeacons[index]) {
                break;
            }

            mNumClosestBeacons++;
            android.util.Log.d("UserPosition", "closest beacon [" + index + "]: " + mClosestBeacons[index].bid.substring(mClosestBeacons[index].bid.length() - 2) + " | " + mClosestBeacons[index].distance);
        }
    }

    // Repopulate priority queue with the closest beacons
    private void repopulateQueue() {
        for (int index = 0; index < mNumClosestBeacons; index++) {
            mBeaconData.add(mClosestBeacons[index]);
        }
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

            // Remove any previous instance of the same BID, if it exists
            mBeaconData.remove(beaconData);
            mBeaconData.add(beaconData);

            updateUserPosition();
        }
    }
}
