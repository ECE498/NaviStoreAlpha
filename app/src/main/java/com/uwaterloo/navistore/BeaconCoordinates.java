package com.uwaterloo.navistore;

import java.util.HashMap;

// Singleton containing beacon coordinate data
public class BeaconCoordinates {
    // Offset of each beacon with respect to [0, 0] coordinate
    public static final float INITIAL_OFFSET = 25.0f;
    // Halfway pixel length between adjacent beacons
    public static final float HALFWAY_LENGTH = 250.0f;

    private static BeaconCoordinates mBeaconCoordinates = null;

    private HashMap<String, Coordinate> mBeaconCoordinateMap;

    private BeaconCoordinates() {
        mBeaconCoordinateMap = new HashMap<>();

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32741", new Coordinate(
                INITIAL_OFFSET + (0 * (2 * HALFWAY_LENGTH)),
                INITIAL_OFFSET + (0 * (2 * HALFWAY_LENGTH))));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32742", new Coordinate(
                INITIAL_OFFSET + (1 * (2 * HALFWAY_LENGTH)),
                INITIAL_OFFSET + (0 * (2 * HALFWAY_LENGTH))));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32745", new Coordinate(
                INITIAL_OFFSET + (0 * (2 * HALFWAY_LENGTH)),
                INITIAL_OFFSET + (1 * (2 * HALFWAY_LENGTH))));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32747", new Coordinate(
                INITIAL_OFFSET + (1 * (2 * HALFWAY_LENGTH)),
                INITIAL_OFFSET + (1 * (2 * HALFWAY_LENGTH))));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32749", new Coordinate(
                INITIAL_OFFSET + (0 * (2 * HALFWAY_LENGTH)),
                INITIAL_OFFSET + (2 * (2 * HALFWAY_LENGTH))));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32750", new Coordinate(
                INITIAL_OFFSET + (1 * (2 * HALFWAY_LENGTH)),
                INITIAL_OFFSET + (2 * (2 * HALFWAY_LENGTH))));
    }

    public static BeaconCoordinates getInstance() {
        if (null == mBeaconCoordinates) {
            mBeaconCoordinates = new BeaconCoordinates();
        }
        return mBeaconCoordinates;
    }

    // Return null if invalid
    public Coordinate getCoordinate(String bid) {
        Coordinate coordinate = null;
        if (mBeaconCoordinateMap.containsKey(bid)) {
            coordinate = mBeaconCoordinateMap.get(bid);
            // FIXME: better alternatives?
            // Copy to ensure original is cannot be modified
            coordinate = new Coordinate(coordinate.mX, coordinate.mY);
        }
        return coordinate;
    }

    public boolean isBeaconValid(String bid) {
        return mBeaconCoordinateMap.containsKey(bid);
    }
}
