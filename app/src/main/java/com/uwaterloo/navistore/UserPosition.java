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

    public static final float PIXEL_PER_DISTANCE = 500.0f / 3.0f;

    private DemoView mDemoView;
    private UserDrawing mUserDrawing;

//    private HashMap<String, ScannedBeacon> mData;
    private PriorityQueue<ScannedBeacon> mBeaconData;
    private ScannedBeacon[] mClosestBeacons;
    private float[] mBeaconIntersectX;
    private float[] mBeaconIntersectY;

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
        mClosestBeacons = new ScannedBeacon[3];
        mBeaconIntersectX = new float[3];
        mBeaconIntersectY = new float[3];
    }

    private void updateUserPosition() {
        getClosestBeacons();
        mDemoView.updateFocus(mClosestBeacons[0], mClosestBeacons[1], mClosestBeacons[2]);
        calculatePosition();
        repopulateQueue();
        mUserDrawing.setCoordinates(mCoordinateX, mCoordinateY);
        mDemoView.postInvalidate();
    }

    private void calculatePosition() {
        triangulate();
//        quantizeCoordinates();
    }

    private void triangulate() {
        int index;
        for (index = 0; index < mNumClosestBeacons; index++) {
            mBeaconIntersectX[index] = BeaconCoordinates.getInstance().getCoordinateX(mClosestBeacons[index].bid);
            mBeaconIntersectY[index] = BeaconCoordinates.getInstance().getCoordinateY(mClosestBeacons[index].bid);
        }
        for (; index < mBeaconIntersectX.length; index++) {
            mBeaconIntersectX[index] = -1.0f;
            mBeaconIntersectY[index] = -1.0f;
        }

        // Triangulate between first two beacons
        if (mNumClosestBeacons >= 2) {
            float[] firstIntersectCoordinates = getIntersection(mBeaconIntersectX[0], mBeaconIntersectY[0], Float.parseFloat(mClosestBeacons[0].distance) * PIXEL_PER_DISTANCE,
                    mBeaconIntersectX[1], mBeaconIntersectY[1], Float.parseFloat(mClosestBeacons[1].distance) * PIXEL_PER_DISTANCE,
                    mBeaconIntersectX[2], mBeaconIntersectY[2]);

            // Triangulate two beacons with the third
            if (mNumClosestBeacons >= 3) {
                float[] secondIntersectCoordinates = getIntersection(mBeaconIntersectX[1], mBeaconIntersectY[1], Float.parseFloat(mClosestBeacons[1].distance) * PIXEL_PER_DISTANCE,
                        mBeaconIntersectX[2], mBeaconIntersectY[2], Float.parseFloat(mClosestBeacons[2].distance) * PIXEL_PER_DISTANCE,
                        mBeaconIntersectX[0], mBeaconIntersectY[0]);

                float[] thirdIntersectCoordinates = getIntersection(mBeaconIntersectX[0], mBeaconIntersectY[0], Float.parseFloat(mClosestBeacons[0].distance) * PIXEL_PER_DISTANCE,
                        mBeaconIntersectX[2], mBeaconIntersectY[2], Float.parseFloat(mClosestBeacons[2].distance) * PIXEL_PER_DISTANCE,
                        mBeaconIntersectX[1], mBeaconIntersectY[1]);

                mBeaconIntersectX[1] = secondIntersectCoordinates[0];
                mBeaconIntersectY[1] = secondIntersectCoordinates[1];
                mBeaconIntersectX[2] = thirdIntersectCoordinates[0];
                mBeaconIntersectY[2] = thirdIntersectCoordinates[1];
            }

            mBeaconIntersectX[0] = firstIntersectCoordinates[0];
            mBeaconIntersectY[0] = firstIntersectCoordinates[1];
        }
        centralize();
    }

    // Implementing based on
    // http://paulbourke.net/geometry/circlesphere/ - Intersection of two circles
    // @return float[] - [x3, y3];
    private float[] getIntersection(float x0, float y0, float r0, float x1, float y1, float r1, float finalBeaconCoordX, float finalBeaconCoordY) {
        // p3 = [x3, y3]
        float[] p3 = new float[2];
        double a, d, h;
        double x2, y2;
        double x3_1, x3_2, y3_1, y3_2;

        d = getDistance(x0, y0, x1, y1);
        a = (r0*r0  - r1*r1 + d*d) / (2.0*d);
        h = r0*r0 - a*a;

        x2 = x0 + ((a * (x1 - x0)) / d);
        y2 = x0 + ((a * (y1 - y0)) / d);

        // Circles are separate or third beacon does not exist
        if ((d > (r0 + r1)) || finalBeaconCoordX < 0.0f) {
            // Set p3 to be relative center based on respective radii
            // Can't use previous x2/y2 because it is based on a nonsensical 'a' value
            double edgeX0 = x0 + ((r0 * (x1 - x0)) / d);
            double edgeY0 = y0 + ((r0 * (y1 - y0)) / d);

            double edgeX1 = x1 + ((r1 * (x0 - x1)) / d);
            double edgeY1 = y1 + ((r1 * (y0 - y1)) / d);

            x2 = (edgeX0 + edgeX1) / 2.0;
            y2 = (edgeY0 + edgeY1) / 2.0;
            p3[0] = (float)x2;
            p3[1] = (float)y2;
        // One circle is enclosed within the other
        } else if (d < Math.abs(r0 - r1)) {
            // Set p3 to be the center of smaller circle (i.e. a beacon position)
            if (r0 > r1) {
                p3[0] = x1;
                p3[1] = y1;
            } else {
                p3[0] = x0;
                p3[1] = y0;
            }
        // Typical case of two intersecting values
        } else {
            x3_1 = x2 + ((h * (y1 - y0)) / d);
            y3_1 = y2 - ((h * (x1 - x0)) / d);

            x3_2 = x2 - ((h * (y1 - y0)) / d);
            y3_2 = y2 + ((h * (x1 - x0)) / d);

            // Determine which p3 is closest to the third beacon
            if (getDistance(x3_1, y3_1, finalBeaconCoordX, finalBeaconCoordY) < getDistance(x3_2, y3_2, finalBeaconCoordX, finalBeaconCoordY)) {
                p3[0] = (float)x3_1;
                p3[1] = (float)y3_1;
            } else {
                p3[0] = (float)x3_2;
                p3[1] = (float)y3_2;
            }
        }

        return p3;
    }

    private double getDistance(double point0X, double point0Y, double point1X, double point1Y) {
        double diffX = point0X - point1X;
        double diffY = point0Y - point1Y;
        return Math.sqrt((diffX * diffX) + (diffY * diffY));
    }

    private void centralize() {
        float centerX = 0.0f;
        float centerY = 0.0f;
        for (int index = 0; index < mNumClosestBeacons; index++) {
            centerX += mBeaconIntersectX[index];
            centerY += mBeaconIntersectY[index];
        }

        centerX /= mNumClosestBeacons;
        centerY /= mNumClosestBeacons;

        mCoordinateX = centerX;
        mCoordinateY = centerY;
    }

    private void quantizeCoordinates() {
        mCoordinateX = Math.round((mCoordinateX - 25) / 250.0) * 250 + 25;
        mCoordinateY = Math.round((mCoordinateY - 25) / 250.0) * 250 + 25;
    }

    // Get closest beacons to perform positioning algorithm on; may be equal to or less then array length
    private void getClosestBeacons() {
        mNumClosestBeacons = 0;

        for (int index = 0; index < mClosestBeacons.length; index++) {
            mClosestBeacons[index] = null;
        }

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
