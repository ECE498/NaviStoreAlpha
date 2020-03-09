package com.uwaterloo.navistore;

import java.util.HashMap;

public class BeaconCoordinates {
    private static BeaconCoordinates mBeaconCoordinates = null;

    private HashMap<String, Float> mBeaconXCoordinateMap;
    private HashMap<String, Float> mBeaconYCoordinateMap;

    private BeaconCoordinates() {
        mBeaconXCoordinateMap = new HashMap<>();
        mBeaconYCoordinateMap = new HashMap<>();

        mBeaconXCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32741", 25.0f);
        mBeaconYCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32741", 25.0f);

        mBeaconXCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32742", 525.0f);
        mBeaconYCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32742", 25.0f);

        mBeaconXCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32745", 25.0f);
        mBeaconYCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32745", 525.0f);

        mBeaconXCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32747", 525.0f);
        mBeaconYCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32747", 525.0f);

        mBeaconXCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32749", 25.0f);
        mBeaconYCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32749", 1025.0f);

        mBeaconXCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32750", 525.0f);
        mBeaconYCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32750", 1025.0f);
    }

    public static BeaconCoordinates getInstance() {
        if (null == mBeaconCoordinates) {
            mBeaconCoordinates = new BeaconCoordinates();
        }
        return mBeaconCoordinates;
    }

    public boolean isBeaconValid(String bid) {
        return (mBeaconXCoordinateMap.containsKey(bid) && mBeaconYCoordinateMap.containsKey(bid));
    }

    // Return negative value if invalid
    public float getCoordinateX(String bid) {
        float coordinateX = -1.0f;
        if (mBeaconXCoordinateMap.containsKey(bid) && mBeaconYCoordinateMap.containsKey(bid)) {
            coordinateX = mBeaconXCoordinateMap.get(bid);
        }
        return coordinateX;
    }

    // Return negative value if invalid
    public float getCoordinateY(String bid) {
        float coordinateY = -1.0f;
        if (mBeaconXCoordinateMap.containsKey(bid) && mBeaconYCoordinateMap.containsKey(bid)) {
            coordinateY = mBeaconYCoordinateMap.get(bid);
        }
        return coordinateY;
    }
}
