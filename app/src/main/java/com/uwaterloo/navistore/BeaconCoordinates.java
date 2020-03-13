package com.uwaterloo.navistore;

import java.util.HashMap;

// Singleton containing beacon coordinate data
public class BeaconCoordinates {
    // Number of pixels per distance (m)
    public static final float PIXEL_PER_DISTANCE = 100.0f;
    // Offset of each beacon with respect to [0, 0] coordinate
    public static final float INITIAL_OFFSET_X = PIXEL_PER_DISTANCE / 8.0f;
    public static final float INITIAL_OFFSET_Y = PIXEL_PER_DISTANCE / 8.0f;
    // Offset of room position
    public static final float ROOM_OFFSET_X = (float)(0.6 * PIXEL_PER_DISTANCE);
    public static final float ROOM_OFFSET_Y = (float)(0.0 * PIXEL_PER_DISTANCE);
    // Dimension of floor (i.e. width and length)
    public static final float ROOM_DIMENSION_X = (float)((8.0 - (0.6 * 2.0)) * PIXEL_PER_DISTANCE);
    public static final float ROOM_DIMENSION_Y = (float)((11.0) * PIXEL_PER_DISTANCE);

    private static BeaconCoordinates mBeaconCoordinates = null;

    private HashMap<String, Coordinate> mBeaconCoordinateMap;

    private BeaconCoordinates() {
        mBeaconCoordinateMap = new HashMap<>();

        // 56, 47, 41
        // 57, 48, 42
        // 58, 50, 43
        // 60, 51, 45

        // 41, 42, 43, 45
        // 47, 48, 50, 51
        // 56, 57, 58, 60

        // Column 3
        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32741", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 8),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 0)));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32742", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 8),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 3)));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32743", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 8),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 8)));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32745", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 8),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 11)));

        // Column 2
        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32747", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 4),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 0)));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32748", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 4),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 3)));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32750", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 4),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 8)));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32751", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 4),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 11)));

        // Column 1
        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32756", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 0),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 0)));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32757", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 0),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 3)));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32758", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 0),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 8)));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32760", new Coordinate(
                INITIAL_OFFSET_X + (PIXEL_PER_DISTANCE * 0),
                INITIAL_OFFSET_Y + (PIXEL_PER_DISTANCE * 11)));
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
