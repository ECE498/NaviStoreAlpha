package com.uwaterloo.navistore.CyPhy;

import android.widget.TextView;

import com.cyphymedia.sdk.model.ScannedBeacon;

/**TODO: This can be changed to how we want to handle/manage the Bluetooth beacon data
 * Singleton BeaconData class. Receives beacon data from the CyPhy callback functions
 */
public class BeaconData {
    private static BeaconData mData = null;

    private TextView mTextView;
    private ScannedBeacon[] mBeaconData;

    private BeaconData() {
        mBeaconData = new ScannedBeacon[3];
        for (int i = 0; i < mBeaconData.length; i++) {
            mBeaconData[i] = new ScannedBeacon("0_0_0", "0", "0");
        }
    }

    public static BeaconData getInstance() {
        if (mData == null) {
            mData = new BeaconData();
        }
        return mData;
    }

    public void update(ScannedBeacon data) {
        android.util.Log.d("BeaconData", "update");

        for (int i = 0; i < mBeaconData.length; i++) {
            if (mBeaconData[i].bid.equals(data.bid) || mBeaconData[i].bid.equals("0_0_0")) {
                mBeaconData[i] = data;
                break;
            }
        }

        StringBuffer text = new StringBuffer();
        for (ScannedBeacon beacon : mBeaconData) {
            text.append(beacon.toString() + " ");
        }
        mTextView.setText(text);
    }


    public void setTextView(TextView newTextView) {
        mTextView = newTextView;
    }
}
