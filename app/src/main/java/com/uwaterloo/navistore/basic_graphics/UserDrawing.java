package com.uwaterloo.navistore.basic_graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class UserDrawing implements Drawing {
    private final int USER_COLOR = Color.BLUE;
    private final int USER_RADIUS = 50;

    private Paint mBeaconPaint;
    private float mCoordinateX;
    private float mCoordinateY;

    public UserDrawing() {
        mBeaconPaint = new Paint();
        mBeaconPaint.setColor(USER_COLOR);

        mCoordinateX = 0.0f;
        mCoordinateY = 0.0f;
    }

    public float getCoordinateX() {
        return mCoordinateX;
    }

    public float getCoordinateY() {
        return mCoordinateY;
    }

    public void setCoordinates(float coordinateX, float coordinateY) {
        mCoordinateX = coordinateX;
        mCoordinateY = coordinateY;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mCoordinateX, mCoordinateY, USER_RADIUS, mBeaconPaint);
    }
}
