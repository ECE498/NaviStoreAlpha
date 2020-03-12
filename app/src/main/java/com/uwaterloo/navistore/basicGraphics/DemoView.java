package com.uwaterloo.navistore.basicGraphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.cyphymedia.sdk.model.ScannedBeacon;
import com.uwaterloo.navistore.BeaconCoordinates;
import com.uwaterloo.navistore.Coordinate;
import com.uwaterloo.navistore.ProcessedBeacon;
import com.uwaterloo.navistore.UserPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DemoView extends View {

    private List<BeaconDrawing> mBeaconDrawings;
    private UserDrawing mUserDrawing;

    public DemoView(Context context, UserDrawing userDrawing){
        super(context);
        mUserDrawing = userDrawing;
        mBeaconDrawings = Collections.synchronizedList(new ArrayList<BeaconDrawing>());
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
            Coordinate beaconPosition = BeaconCoordinates.getInstance().getCoordinate(bid);
            beaconDrawing.setPosition(beaconPosition.mX, beaconPosition.mY);
            synchronized(mBeaconDrawings) {
                mBeaconDrawings.add(beaconDrawing);
            }
        }
    }

    public void registerBeaconDrawing(String bid) {
        BeaconDrawing beaconDrawing = new BeaconDrawing(bid);
        registerBeaconDrawing(beaconDrawing);
    }

    public void updateBeacon(ProcessedBeacon beacon) {
        for (BeaconDrawing beaconDrawing : mBeaconDrawings) {
            if (beaconDrawing.getBid().equals(beacon.bid)) {
                beaconDrawing.setRangeRadius(Math.round(beacon.finalDistance * BeaconCoordinates.PIXEL_PER_DISTANCE));
            }
        }
        this.postInvalidate();
    }

    public void updateFocus(ProcessedBeacon beacon1, ProcessedBeacon beacon2, ProcessedBeacon beacon3) {
        synchronized(mBeaconDrawings) {
            for (BeaconDrawing beaconDrawing : mBeaconDrawings) {
                if (((null != beacon1) && beaconDrawing.getBid().equals(beacon1.bid)) ||
                        ((null != beacon2) && beaconDrawing.getBid().equals(beacon2.bid)) ||
                        ((null != beacon3) && beaconDrawing.getBid().equals(beacon3.bid))) {
                    beaconDrawing.setRangeColor(Color.BLUE);
                } else {
                    beaconDrawing.setRangeColor(Color.RED);
                }
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
