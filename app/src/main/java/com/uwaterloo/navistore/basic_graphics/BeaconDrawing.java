package com.uwaterloo.navistore.basic_graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BeaconDrawing implements Drawing {
    private final int BEACON_COLOR = Color.YELLOW;
    private final int BEACON_RADIUS = 25;

    private final int TEXT_COLOR = Color.BLACK;
    private final float TEXT_SIZE = 20.0f;

    private String mBid;
    private Paint mBeaconPaint;
    private Paint mTextPaint;
    private float mCoordinateX;
    private float mCoordinateY;

    public BeaconDrawing(String bid) {
        mBid = bid;

        mBeaconPaint = new Paint();
        mBeaconPaint.setColor(BEACON_COLOR);

        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(TEXT_COLOR);
        mTextPaint.setTextSize(TEXT_SIZE);

        mCoordinateX = 0.0f;
        mCoordinateY = 0.0f;
    }

    public String getBid(){
        return mBid;
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
        canvas.drawCircle(mCoordinateX, mCoordinateY, BEACON_RADIUS, mBeaconPaint);
        canvas.drawText(mBid.substring(mBid.length() - 2), mCoordinateX, mCoordinateY, mTextPaint);
    }
}
