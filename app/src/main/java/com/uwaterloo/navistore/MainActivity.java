package com.uwaterloo.navistore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.uwaterloo.navistore.CyPhy.BeaconScanner;
import com.uwaterloo.navistore.CyPhy.BeaconData;
import com.uwaterloo.navistore.basic_graphics.DemoView;
import com.uwaterloo.navistore.basic_graphics.UserDrawing;

public class MainActivity extends AppCompatActivity {
    private UserDrawing mUserDrawing;
    private UserPosition mUserPosition;
    private DemoView mDemoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        mUserDrawing = new UserDrawing();
        mDemoView = new DemoView(this, mUserDrawing);
        setContentView(mDemoView);
        mUserPosition = new UserPosition(mDemoView, mUserDrawing);

        Thread userPositionThread = new Thread(mUserPosition);
        userPositionThread.start();

        BeaconData.getInstance().setDemoView(mDemoView);
        BeaconScanner.getInstance().init(this);
    }
}
