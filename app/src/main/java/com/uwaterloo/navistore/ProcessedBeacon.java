package com.uwaterloo.navistore;

public class ProcessedBeacon {
    public String bid;
    public float rssi;
    public float battery;

    // Used for Kalman Filtering
    public float calculatedDistance;
    public float kalmanP;

    public float finalDistance;

    public ProcessedBeacon(String bid, String rssi, String battery) {
        this.bid = bid;
        this.rssi = Float.parseFloat(rssi);
        this.battery = Float.parseFloat(battery);
        calculatedDistance = 1.0f;
        kalmanP = 0.0f;
        finalDistance = 0.0f;
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
