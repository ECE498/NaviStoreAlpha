package com.uwaterloo.navistore.basic_graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DemoView extends View {

    private List<BeaconDrawing> mBeaconDrawings;

    private HashMap<String, Float> mBeaconXCoordinateMap;
    private HashMap<String, Float> mBeaconYCoordinateMap;

    public DemoView(Context context){
        super(context);
        mBeaconDrawings = new ArrayList<>();

        mBeaconXCoordinateMap = new HashMap<>();
        mBeaconYCoordinateMap = new HashMap<>();

        mBeaconXCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32741", 25.0f);
        mBeaconYCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32741", 25.0f);

        mBeaconXCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32747", 1025.0f);
        mBeaconYCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32747", 25.0f);

        mBeaconXCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32750", 25.0f);
        mBeaconYCoordinateMap.put("43795068-794D-6564-6961-426561636F6E_01710_32750", 1025.0f);
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
        mBeaconDrawings.add(beaconDrawing);

        String bid = beaconDrawing.getBid();
        if (mBeaconXCoordinateMap.containsKey(bid) && mBeaconYCoordinateMap.containsKey(bid)) {
            float coordinateX = mBeaconXCoordinateMap.get(bid);
            float coordinateY = mBeaconYCoordinateMap.get(bid);
            beaconDrawing.setCoordinates(coordinateX, coordinateY);
        }
    }

    public void registerBeaconDrawing(String bid) {
        BeaconDrawing beaconDrawing = new BeaconDrawing(bid);
        registerBeaconDrawing(beaconDrawing);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // custom drawing code here
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        // make the entire canvas white
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);

        for (BeaconDrawing beaconDrawing : mBeaconDrawings) {
            android.util.Log.d("DemoView", "draw beacon");
            beaconDrawing.draw(canvas);
        }
    }
}
