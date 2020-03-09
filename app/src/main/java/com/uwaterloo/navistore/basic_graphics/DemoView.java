package com.uwaterloo.navistore.basic_graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.cyphymedia.sdk.model.ScannedBeacon;
import com.uwaterloo.navistore.BeaconCoordinates;
import com.uwaterloo.navistore.UserPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DemoView extends View {

    private List<BeaconDrawing> mBeaconDrawings;
    private UserDrawing mUserDrawing;

    public DemoView(Context context, UserDrawing userDrawing){
        super(context);
        mUserDrawing = userDrawing;
        mBeaconDrawings = new ArrayList<>();
    }

    public boolean containsBeacon(String bid) {
        for (BeaconDrawing beaconDrawing : mBeaconDrawings) {
            if (beaconDrawing.getBid().equals(bid)) {
                return true;
            }
        }
        return false;
    }

    public void registerBeaconDrawing(BeaconDrawing beaconDrawing) {
        String bid = beaconDrawing.getBid();
        if (BeaconCoordinates.getInstance().isBeaconValid(bid)) {
            mBeaconDrawings.add(beaconDrawing);
            float coordinateX = BeaconCoordinates.getInstance().getCoordinateX(bid);
            float coordinateY = BeaconCoordinates.getInstance().getCoordinateY(bid);
            beaconDrawing.setCoordinates(coordinateX, coordinateY);
        }
    }

    public void registerBeaconDrawing(String bid) {
        BeaconDrawing beaconDrawing = new BeaconDrawing(bid);
        registerBeaconDrawing(beaconDrawing);
    }

    public void updateBeacon(ScannedBeacon beacon) {
        for (BeaconDrawing beaconDrawing : mBeaconDrawings) {
            if (beaconDrawing.getBid().equals(beacon.bid)) {
                beaconDrawing.setRangeRadius(Math.round(Float.parseFloat(beacon.distance) * UserPosition.PIXEL_PER_DISTANCE));
            }
        }
        this.postInvalidate();
    }

    public void updateFocus(ScannedBeacon beacon1, ScannedBeacon beacon2, ScannedBeacon beacon3) {
        for (BeaconDrawing beaconDrawing : mBeaconDrawings) {
            if (((null != beacon1) && beaconDrawing.getBid().equals(beacon1.bid)) ||
                    ((null != beacon2) && beaconDrawing.getBid().equals(beacon2.bid)) ||
                    ((null != beacon3) && beaconDrawing.getBid().equals(beacon3.bid))) {
                beaconDrawing.setRangeColor(Color.BLUE);
            } else {
                beaconDrawing.setRangeColor(Color.RED);
            }
        }
        this.postInvalidate();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // custom drawing code here
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        // make the entire canvas white
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);

        mUserDrawing.draw(canvas);
        for (BeaconDrawing beaconDrawing : mBeaconDrawings) {
            beaconDrawing.draw(canvas);
        }
    }
}
