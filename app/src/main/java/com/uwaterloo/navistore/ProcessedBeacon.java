package com.uwaterloo.navistore;

public class ProcessedBeacon {
    // Threshold to increase distance from beacon if no measurement has been taken after multiple iterations
    public static final int ITERATION_THRESHOLD = 80;

    public String bid;
    public float rssi;
    public float battery;

    // Used for Kalman Filtering
    public float calculatedDistance;
    public float kalmanP;

    // Used for closest beacons
    public int iterationCounter = 0;

    public float finalDistance;

    public ProcessedBeacon(String bid, String rssi, String battery) {
        this.bid = bid;
        this.rssi = Float.parseFloat(rssi);
        this.battery = Float.parseFloat(battery);
        // Start with initial, reasonably high value
        this.calculatedDistance = 5.0f;
        this.kalmanP = 0.0f;
        this.iterationCounter = 0;
        this.finalDistance = 0.0f;
    }

    public float getDistance() {
        return finalDistance;
//        return (finalDistance + (0.5f * (iterationCounter % ITERATION_THRESHOLD)));
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if (obj instanceof ProcessedBeacon) {
            isEqual = this.bid.equals(((ProcessedBeacon)obj).bid);
        }
        return isEqual;
    }
}
