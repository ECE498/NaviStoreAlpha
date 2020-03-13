package com.uwaterloo.navistore;

import com.cyphymedia.sdk.model.ScannedBeacon;
import com.uwaterloo.navistore.CyPhy.BeaconData;
import com.uwaterloo.navistore.basicGraphics.DemoView;
import com.uwaterloo.navistore.basicGraphics.UserDrawing;
import com.uwaterloo.navistore.test.DataCollector;
import com.uwaterloo.navistore.test.FileLogger;
import com.uwaterloo.navistore.webInterface.UserDataPoster;

import java.util.Comparator;
import java.util.PriorityQueue;

public class UserPosition implements Runnable {
    // Calibrated RSSI value from a distance of 1 m (600 points of data collected)
    public static final float CALIBRATED_RSSI_DB = -59.9f;
    // RSSI factor 'n' in d = 10^((rssi_calibrated - rssi) / (10 * n))
    public static final float RSSI_FACTOR = 2.3f;
    // Scaling factor to correct for errors post-Kalman filter
    public static final float DISTANCE_SCALING_FACTOR = 1.0f;

    // Drawing-related members
    private DemoView mDemoView;
    private UserDrawing mUserDrawing;

    // Beacon data processing members
    private PriorityQueue<ProcessedBeacon> mBeaconData;

    // Position triangulation-related members
    private ProcessedBeacon[] mClosestBeacons;
    private Coordinate[] mBeaconIntersect;

    // Data collection members
//    private DataCollector mRssiData;
//    private DataCollector mDistanceData;

    // Current user position
    private Coordinate mPosition;

    public UserPosition(DemoView demoView, UserDrawing userDrawing) {
        mDemoView = demoView;
        mUserDrawing = userDrawing;

        // Compare distances such that the head of the queue is the closest beacon
        mBeaconData = new PriorityQueue<>(20, new Comparator<ProcessedBeacon>() {
            @Override
            public int compare(ProcessedBeacon t0, ProcessedBeacon t1) {
                if (t0.finalDistance > t1.finalDistance) {
                    return 1;
                } else if (t0.finalDistance == t1.finalDistance) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        mClosestBeacons = new ProcessedBeacon[3];
        mBeaconIntersect = new Coordinate[3];
        for (int index = 0; index < mBeaconIntersect.length; index++) {
            mBeaconIntersect[index] = new Coordinate(0.0f, 0.0f);
        }

//        mRssiData = new DataCollector();
//        mDistanceData = new DataCollector();

        mPosition = new Coordinate(0.0f, 0.0f);
    }

    private void processBeaconData(ScannedBeacon beacon) {
        ProcessedBeacon processedBeacon = extractAndUpdateFromBeaconQueue(beacon);
        float inputDistance = calculateDistanceFromRssi(processedBeacon.rssi);
        kalmanFilter(processedBeacon, inputDistance);
        processedBeacon.finalDistance = processedBeacon.calculatedDistance * DISTANCE_SCALING_FACTOR;
//        processedBeacon.finalDistance = inputDistance;

//        mRssiData.collectData(processedBeacon.rssi);
//        mDistanceData.collectData(processedBeacon.finalDistance);
//        if (mRssiData.getCounter() < 1000) {
//            FileLogger.getInstance().logToFile(mRssiData.getCounter() - 1 + "," + processedBeacon.rssi);
//        }

        mBeaconData.add(processedBeacon);
        if (null != mDemoView) {
            mDemoView.updateBeacon(processedBeacon);
        }
    }

    private ProcessedBeacon extractAndUpdateFromBeaconQueue(ScannedBeacon beacon) {
        ProcessedBeacon processedBeacon = null;
        for (ProcessedBeacon existingBeacon : mBeaconData) {
            if (existingBeacon.bid.equals(beacon.bid)) {
                processedBeacon = existingBeacon;
                break;
            }
        }
        if (null != processedBeacon) {
            processedBeacon.rssi = Float.parseFloat(beacon.rssi);
            processedBeacon.battery = Float.parseFloat(beacon.battery);
            if (!mBeaconData.remove(processedBeacon)) {
                android.util.Log.d("UserPosition", "Existing ProcessedBeacon was not removed");
            }
        } else {
            processedBeacon = new ProcessedBeacon(beacon.bid, beacon.rssi, beacon.battery);
        }
        return processedBeacon;
    }

    private float calculateDistanceFromRssi(float rssi) {
        return (float)Math.pow(10, (CALIBRATED_RSSI_DB - rssi) / (10 * RSSI_FACTOR));
    }

    // Kalman Filter based on paper from AMSEE 2016 conference
    // "An Indoor Positioning Algorithm Using Bluetooth Low Energy RSSI"
    // Song Chair, Renbo An and Zhengzhong Du
    private void kalmanFilter(ProcessedBeacon beacon, float inputDistance) {
        final float A = 0.875f;
        final float H = 0.025f;
        final float r = 6.08f;
        final float q = 8.08f;

        beacon.calculatedDistance = (A * beacon.calculatedDistance);
        beacon.kalmanP = (A * A * beacon.kalmanP) + q;
        float gain = (beacon.kalmanP * H) / ((beacon.kalmanP * H * H) + r);
        beacon.kalmanP = (1 - (gain * H)) * beacon.kalmanP;
        beacon.calculatedDistance = beacon.calculatedDistance + (gain * (inputDistance - (H * beacon.calculatedDistance)));
    }

    private void updateUserPosition() {
        int numClosestBeacons = getClosestBeacons(mClosestBeacons);
        triangulate(mClosestBeacons, numClosestBeacons, mPosition);
//        quantizeCoordinates(mPosition);
        repopulateQueue(mClosestBeacons, numClosestBeacons);

        UserDataPoster.getInstance().updatePosition(mPosition.mX, mPosition.mY);

        if (null != mDemoView && null != mUserDrawing) {
            mDemoView.updateFocus(mClosestBeacons[0], mClosestBeacons[1], mClosestBeacons[2]);
            mUserDrawing.setCoordinates(mPosition.mX, mPosition.mY);
            mDemoView.postInvalidate();
        }
    }

    // @sideeffect Update 'position' with newly-triangulated user position
    private void triangulate(ProcessedBeacon[] closestBeacons, int numClosestBeacons, Coordinate position) {
        int index;
        for (index = 0; index < numClosestBeacons; index++) {
            mBeaconIntersect[index] = BeaconCoordinates.getInstance().getCoordinate(closestBeacons[index].bid);
        }
        for (; index < mBeaconIntersect.length; index++) {
            mBeaconIntersect[index].mX = -1.0f;
            mBeaconIntersect[index].mY = -1.0f;
        }

        // Triangulate between first two beacons
        if (numClosestBeacons >= 2) {
            Coordinate firstIntersect = getIntersection(mBeaconIntersect[0].mX, mBeaconIntersect[0].mY, closestBeacons[0].finalDistance * BeaconCoordinates.PIXEL_PER_DISTANCE,
                    mBeaconIntersect[1].mX, mBeaconIntersect[1].mY, closestBeacons[1].finalDistance * BeaconCoordinates.PIXEL_PER_DISTANCE,
                    mBeaconIntersect[2].mX, mBeaconIntersect[2].mY);

            // Triangulate two beacons with the third
            if (numClosestBeacons >= 3) {
                Coordinate secondIntersect = getIntersection(mBeaconIntersect[1].mX, mBeaconIntersect[1].mY, closestBeacons[1].finalDistance * BeaconCoordinates.PIXEL_PER_DISTANCE,
                        mBeaconIntersect[2].mX, mBeaconIntersect[2].mY, closestBeacons[2].finalDistance * BeaconCoordinates.PIXEL_PER_DISTANCE,
                        mBeaconIntersect[0].mX, mBeaconIntersect[0].mY);

                Coordinate thirdIntersect = getIntersection(mBeaconIntersect[0].mX, mBeaconIntersect[0].mY, closestBeacons[0].finalDistance * BeaconCoordinates.PIXEL_PER_DISTANCE,
                        mBeaconIntersect[2].mX, mBeaconIntersect[2].mY, closestBeacons[2].finalDistance * BeaconCoordinates.PIXEL_PER_DISTANCE,
                        mBeaconIntersect[1].mX, mBeaconIntersect[1].mY);

                mBeaconIntersect[1] = secondIntersect;
                mBeaconIntersect[2] = thirdIntersect;
            }

            mBeaconIntersect[0] = firstIntersect;
        }

        centralize(mBeaconIntersect, numClosestBeacons, position);
    }

    // Implementing based on
    // http://paulbourke.net/geometry/circlesphere/ - Intersection of two circles
    private Coordinate getIntersection(float x0, float y0, float r0, float x1, float y1, float r1, float finalBeaconCoordX, float finalBeaconCoordY) {
        Coordinate p3 = new Coordinate();
        double a, d, h;
        double x2, y2;
        // Possible coordinates for p3 (2 possibilities for intersecting circles)
        double x3_1, x3_2, y3_1, y3_2;

        d = getDistance(x0, y0, x1, y1);
        a = ((r0*r0)  - (r1*r1) + (d*d)) / (2.0*d);
        h = Math.sqrt((r0*r0) - (a*a));

        x2 = x0 + ((x1 - x0) * (a / d));
        y2 = y0 + ((y1 - y0) * (a / d));

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
            p3.mX = (float)x2;
            p3.mY = (float)y2;
        // One circle is enclosed within the other
        } else if (d < Math.abs(r0 - r1)) {
            // Set p3 to be the center of smaller circle (i.e. a beacon position)
            if (r0 > r1) {
                p3.mX = x1;
                p3.mY = y1;
            } else {
                p3.mX = x0;
                p3.mY = y0;
            }
        // Typical case of two intersecting values
        } else {
            x3_1 = x2 + ((h * (y1 - y0)) / d);
            y3_1 = y2 - ((h * (x1 - x0)) / d);

            x3_2 = x2 - ((h * (y1 - y0)) / d);
            y3_2 = y2 + ((h * (x1 - x0)) / d);

            // Determine which p3 is closest to the third beacon
            if (getDistance(x3_1, y3_1, finalBeaconCoordX, finalBeaconCoordY) < getDistance(x3_2, y3_2, finalBeaconCoordX, finalBeaconCoordY)) {
                p3.mX = (float)x3_1;
                p3.mY = (float)y3_1;
            } else {
                p3.mX = (float)x3_2;
                p3.mY = (float)y3_2;
            }
        }

        return p3;
    }

    private double getDistance(double point0X, double point0Y, double point1X, double point1Y) {
        double diffX = point0X - point1X;
        double diffY = point0Y - point1Y;
        return Math.sqrt((diffX * diffX) + (diffY * diffY));
    }

    // @sideeffect Store center coordinate in 'center'
    private void centralize(Coordinate[] points, int numPoints, Coordinate center) {
        center.mX = 0.0f;
        center.mY = 0.0f;
        for (int index = 0; index < numPoints; index++) {
            center.mX += points[index].mX;
            center.mY += points[index].mY;
        }

        center.mX /= numPoints;
        center.mY /= numPoints;
    }

    // @sideeffect Update 'point' with quantized coordinates
    private void quantizeCoordinates(Coordinate point) {
        point.mX = Math.round((point.mX - BeaconCoordinates.INITIAL_OFFSET) / BeaconCoordinates.HALFWAY_LENGTH) * (int)BeaconCoordinates.HALFWAY_LENGTH + (int)BeaconCoordinates.INITIAL_OFFSET;
        point.mY = Math.round((point.mY - BeaconCoordinates.INITIAL_OFFSET) / BeaconCoordinates.HALFWAY_LENGTH) * (int)(BeaconCoordinates.HALFWAY_LENGTH) + (int)BeaconCoordinates.INITIAL_OFFSET;
    }

    // Get closest beacons to perform positioning algorithm on; may be equal to or less then array length
    private int getClosestBeacons(ProcessedBeacon[] closestBeacons) {
        int numClosestBeacons = 0;

        for (int index = 0; index < closestBeacons.length; index++) {
            closestBeacons[index] = null;
        }

        for (int index = 0; index < closestBeacons.length; index++) {
            closestBeacons[index] = mBeaconData.poll();

            if (null == closestBeacons[index]) {
                break;
            }

            numClosestBeacons++;
//            android.util.Log.d("UserPosition", "closest beacon [" + index + "]: " + closestBeacons[index].bid.substring(closestBeacons[index].bid.length() - 2) + " | " + closestBeacons[index].finalDistance);
        }

        return numClosestBeacons;
    }

    // Repopulate priority queue with the closest beacons
    private void repopulateQueue(ProcessedBeacon[] closestBeacons, int numClosestBeacons) {
        for (int index = 0; index < numClosestBeacons; index++) {
            mBeaconData.add(closestBeacons[index]);
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

            processBeaconData(beaconData);
            updateUserPosition();
        }
    }

    private void testGetIntersect() {
        int x0, y0, r0, x1, y1, r1, fx, fy;

        x0 = 0; y0 = 0; r0 = 6; x1 = 10; y1 = 10; r1 = 9; fx = 5; fy = 0;
        Coordinate test = getIntersection(x0, y0, r0, x1, y1, r1, fx, fy);
        android.util.Log.d("testGetIntersect", String.format("(x-(%d))^2+(y-(%d))^2=(%d)^2; (x-(%d))^2+(y-(%d))^2=(%d)^2 | (%d,%d) : (%f, %f)", x0, y0, r0, x1, y1, r1, fx, fy, test.mX, test.mY));

        x0 = 0; y0 = 0; r0 = 6; x1 = 10; y1 = 10; r1 = 9; fx = 0; fy = 5;
        test = getIntersection(x0, y0, r0, x1, y1, r1, fx, fy);
        android.util.Log.d("testGetIntersect", String.format("(x-(%d))^2+(y-(%d))^2=(%d)^2; (x-(%d))^2+(y-(%d))^2=(%d)^2 | (%d,%d) : (%f, %f)", x0, y0, r0, x1, y1, r1, fx, fy, test.mX, test.mY));

        x0 = 5; y0 = 15; r0 = 10; x1 = 15; y1 = 0; r1 = 15; fx = 20; fy = 5;
        test = getIntersection(x0, y0, r0, x1, y1, r1, fx, fy);
        android.util.Log.d("testGetIntersect", String.format("(x-(%d))^2+(y-(%d))^2=(%d)^2; (x-(%d))^2+(y-(%d))^2=(%d)^2 | (%d,%d) : (%f, %f)", x0, y0, r0, x1, y1, r1, fx, fy, test.mX, test.mY));

        x0 = 5; y0 = 15; r0 = 10; x1 = 15; y1 = 0; r1 = 15; fx = 5; fy = 5;
        test = getIntersection(x0, y0, r0, x1, y1, r1, fx, fy);
        android.util.Log.d("testGetIntersect", String.format("(x-(%d))^2+(y-(%d))^2=(%d)^2; (x-(%d))^2+(y-(%d))^2=(%d)^2 | (%d,%d) : (%f, %f)", x0, y0, r0, x1, y1, r1, fx, fy, test.mX, test.mY));

        x0 = 5; y0 = 0; r0 = 15; x1 = 25; y1 = 15; r1 = 15; fx = 5; fy = 10;
        test = getIntersection(x0, y0, r0, x1, y1, r1, fx, fy);
        android.util.Log.d("testGetIntersect", String.format("(x-(%d))^2+(y-(%d))^2=(%d)^2; (x-(%d))^2+(y-(%d))^2=(%d)^2 | (%d,%d) : (%f, %f)", x0, y0, r0, x1, y1, r1, fx, fy, test.mX, test.mY));

        x0 = 5; y0 = 0; r0 = 15; x1 = 25; y1 = 15; r1 = 15; fx = 20; fy = 0;
        test = getIntersection(x0, y0, r0, x1, y1, r1, fx, fy);
        android.util.Log.d("testGetIntersect", String.format("(x-(%d))^2+(y-(%d))^2=(%d)^2; (x-(%d))^2+(y-(%d))^2=(%d)^2 | (%d,%d) : (%f, %f)", x0, y0, r0, x1, y1, r1, fx, fy, test.mX, test.mY));
    }
}
