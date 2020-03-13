package com.uwaterloo.navistore;

import java.util.HashMap;

// Singleton containing beacon coordinate data
public class BeaconCoordinates {
    // Number of pixels per distance (m); for DemoView
    public static final float PIXEL_PER_DISTANCE = 100.0f;

    // Scaling factor for actual distance (m) to map position
    public static final float MAP_DISTANCE_SCALING_FACTOR = 1.0f;

    // Offset of each beacon with respect to [0, 0] coordinate
    public static final float INITIAL_OFFSET_X = PIXEL_PER_DISTANCE / 8.0f;
    public static final float INITIAL_OFFSET_Y = PIXEL_PER_DISTANCE / 8.0f;

    // Offset of room position
    public static final float ROOM_OFFSET_X = 0.0f;
    public static final float ROOM_OFFSET_Y = 1.15f;

    // Dimension of floor (i.e. width and length)
    public static final float ROOM_DIMENSION_X = (float)((11.0));
    public static final float ROOM_DIMENSION_Y = (float)((10.3 - (1.15 * 2.0)));

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

        // Row 1
        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32741", new Coordinate(0.44f, 0.60f));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32742", new Coordinate(3.46f, 0.62f));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32743", new Coordinate(8.47f, 0.6f));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32745", new Coordinate(11.47f, 0.62f));

        // Row 2
        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32747", new Coordinate(0.47f, 4.45f));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32748", new Coordinate(3.47f, 4.46f));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32750", new Coordinate(8.46f, 4.46f));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32751", new Coordinate(11.47f, 4.47f));

        // Row 3
        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32756", new Coordinate(0.46f, 8.32f));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32757", new Coordinate(3.47f, 8.34f));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32758", new Coordinate(8.48f, 8.29f));

        mBeaconCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32760", new Coordinate(11.47f, 8.31f));
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
