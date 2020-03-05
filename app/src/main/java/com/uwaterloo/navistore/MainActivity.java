package com.uwaterloo.navistore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.uwaterloo.navistore.CyPhy.BeaconScanner;
import com.uwaterloo.navistore.CyPhy.BeaconData;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        TextView valueTV = new TextView(this);
//        valueTV.setText("Bluetooth Data (default)");
//        valueTV.setId(5);
//        ((LinearLayout) linearLayout).addView(valueTV);
        BeaconData.getInstance().setTextView((TextView) findViewById(R.id.main_text_box));
        BeaconScanner.getInstance().init(this);
    }
}
