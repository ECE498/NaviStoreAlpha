package com.uwaterloo.navistore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.uwaterloo.navistore.CyPhy.BeaconScanner;
import com.uwaterloo.navistore.CyPhy.BeaconData;
import com.uwaterloo.navistore.basicGraphics.DemoView;
import com.uwaterloo.navistore.basicGraphics.UserDrawing;
import com.uwaterloo.navistore.test.FileLogger;

public class MainActivity extends AppCompatActivity {
    private UserDrawing mUserDrawing;
    private UserPosition mUserPosition;
    private DemoView mDemoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_main);
//        mUserPosition = new UserPosition(null, null);
//        return;

        FileLogger.getInstance().setContext(this.getApplicationContext());
        FileLogger.getInstance().open("NaviStore_log_" + (int) (System.nanoTime() / 1000000000L) + ".csv");
        FileLogger.getInstance().logToFile("index,rssi");

        mUserDrawing = new UserDrawing();
        mDemoView = new DemoView(this, mUserDrawing);
        setContentView(mDemoView);
        mUserPosition = new UserPosition(mDemoView, mUserDrawing);
        BeaconData.getInstance().setDemoView(mDemoView);

        Thread userPositionThread = new Thread(mUserPosition);
        userPositionThread.start();
        BeaconScanner.getInstance().init(this);
    }
}
