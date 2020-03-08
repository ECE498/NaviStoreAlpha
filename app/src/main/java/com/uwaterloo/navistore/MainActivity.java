package com.uwaterloo.navistore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.uwaterloo.navistore.CyPhy.BeaconScanner;
import com.uwaterloo.navistore.CyPhy.BeaconData;
import com.uwaterloo.navistore.basic_graphics.DemoView;

public class MainActivity extends AppCompatActivity {
    DemoView mDemoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        mDemoView = new DemoView(this);
        setContentView(mDemoView);

        BeaconData.getInstance().setDemoView(mDemoView);
        BeaconScanner.getInstance().init(this);
    }
}
