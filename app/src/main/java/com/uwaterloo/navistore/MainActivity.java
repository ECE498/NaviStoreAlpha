package com.uwaterloo.navistore;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.uwaterloo.navistore.CyPhy.BeaconData;
import com.uwaterloo.navistore.CyPhy.BeaconScanner;
import com.uwaterloo.navistore.basicGraphics.DemoView;
import com.uwaterloo.navistore.basicGraphics.UserDrawing;
import com.uwaterloo.navistore.webInterface.UserDataPoster;

public class MainActivity extends AppCompatActivity {
    private UserDrawing mUserDrawing = null;
    private UserPosition mUserPosition = null;
    private DemoView mDemoView = null;

    private WebView mWebView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mUserPosition = new UserPosition(null, null);
//        return;

//        FileLogger.getInstance().setContext(this.getApplicationContext());
//        FileLogger.getInstance().open("NaviStore_log_" + (int) (System.nanoTime() / 1000000000L) + ".csv");
//        FileLogger.getInstance().logToFile("index,rssi");

        OrientationSensor.getInstance(this.getApplicationContext());

        UserDataPoster.init(this.getApplicationContext());
        Thread userDataPosterThread = new Thread(UserDataPoster.getInstance());
        userDataPosterThread.start();

//        mUserDrawing = new UserDrawing();
//        mDemoView = new DemoView(this, mUserDrawing);
//        setContentView(mDemoView);

        mUserPosition = new UserPosition(mDemoView, mUserDrawing);
        BeaconData.getInstance().setDemoView(mDemoView);

        Thread userPositionThread = new Thread(mUserPosition);
        userPositionThread.start();

        BeaconScanner.getInstance().init(this);

        mWebView = new WebView(this.getApplicationContext());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        setContentView(mWebView);
        mWebView.loadUrl(UserDataPoster.NAVISTORE_SITE_URL);
    }
}
 