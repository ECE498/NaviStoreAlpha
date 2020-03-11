package com.uwaterloo.navistore.basicGraphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.uwaterloo.navistore.Coordinate;

public class BeaconDrawing implements Drawing {
    private final int BEACON_COLOR = Color.YELLOW;
    private final int BEACON_RADIUS = 25;

    private final int TEXT_COLOR = Color.BLACK;
    private final float TEXT_SIZE = 20.0f;

    private String mBid;
    private Paint mBeaconPaint;
    private Paint mTextPaint;
    private Paint mRangePaint;
    private Coordinate mPosition;
    private int mRangeRadius;

    public BeaconDrawing(String bid) {
        mBid = bid;

        mBeaconPaint = new Paint();
        mBeaconPaint.setColor(BEACON_COLOR);

        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(TEXT_COLOR);
        mTextPaint.setTextSize(TEXT_SIZE);

        mRangePaint = new Paint();
        mRangePaint.setStyle(Paint.Style.STROKE);
        mRangePaint.setStrokeWidth(4.0f);
        mRangePaint.setColor(Color.RED);

        mPosition = new Coordinate();
        mRangeRadius = 25;
    }

    public String getBid(){
        return mBid;
    }

    public Coordinate getPosition() { return mPosition; }

    public void setRangeRadius(int radius) {
        mRangeRadius = radius;
    }

    public void setRangeColor(int color) {
        mRangePaint.setColor(color);
    }

    public void setPosition(float coordinateX, float coordinateY) {
        mPosition.mX = coordinateX;
        mPosition.mY = coordinateY;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mPosition.mX, mPosition.mY, BEACON_RADIUS, mBeaconPaint);
        canvas.drawText(mBid.substring(mBid.length() - 2), mPosition.mX, mPosition.mY, mTextPaint);
        canvas.drawCircle(mPosition.mX, mPosition.mY, mRangeRadius, mRangePaint);
    }
}
