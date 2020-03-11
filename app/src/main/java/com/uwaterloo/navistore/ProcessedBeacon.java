package com.uwaterloo.navistore;

public class ProcessedBeacon {
    public String bid;
    public float rssi;
    public float battery;
    public float calculatedDistance;

    public ProcessedBeacon(String bid, String rssi, String battery) {
        this.bid = bid;
        this.rssi = Float.parseFloat(rssi);
        this.battery = Float.parseFloat(battery);
        calculatedDistance = 0.0f;
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
