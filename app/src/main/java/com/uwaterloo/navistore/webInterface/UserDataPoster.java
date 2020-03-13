package com.uwaterloo.navistore.webInterface;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.uwaterloo.navistore.OrientationSensor;
import com.uwaterloo.navistore.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserDataPoster implements Runnable {
    public static String NAVISTORE_SITE_URL = "http://www.thingjs.com/s/32dbcf1baba90cc14c2479c2";
    public static String NAVISTORE_SERVER_URL = "http://3.209.66.199:3000/";

    private static UserDataPoster mPoster = null;

    private Context mContext;

    private RequestQueue mRequestQueue;
    private UserDataPostRequest mPostRequest;

    // JSON object to send to server
    private JSONObject mPostRequestData;

    // User information to place in JSON object
    private float mUserX;
    private float mUserY;
    private float mUserOrientation;

    private UserDataPoster() {
        mContext = null;
        mRequestQueue = null;
    }

    private UserDataPoster(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                android.util.Log.w("RequestErrorListener", "error response: " + error);
            }
        };

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                android.util.Log.d("RequestListener", "response: " + response.toString());
            }
        };

        mPostRequestData = new JSONObject();
        try {
            mPostRequestData.put("x", mUserX);
            mPostRequestData.put("y", mUserY);
            mPostRequestData.put("orientation", mUserOrientation);
        } catch (JSONException e) {
            android.util.Log.e("UserDataPoster", "Initializing JSON object data: ", e);
        }

        mPostRequest = new UserDataPostRequest(NAVISTORE_SERVER_URL, null, mPostRequestData, responseListener, errorListener);

        mUserX = 0.0f;
        mUserY = 0.0f;
        mUserOrientation = 0.0f;
    }

    public void updateData(float x, float y, float orientation) {
        synchronized(mPostRequestData) {
            mUserX = x;
            mUserY = y;
            mUserOrientation = orientation;
        }
    }

    public void updatePosition(float x, float y) {
        synchronized(mPostRequestData) {
            mUserX = x;
            mUserY = y;
        }
    }

    public static void init(Context context) {
        mPoster = new UserDataPoster(context);
    }

    public static UserDataPoster getInstance() {
        if (null == mPoster) {
            mPoster = new UserDataPoster();
        }
        return mPoster;
    }

    @Override
    public void run() {
        boolean isUpdated = false;
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                android.util.Log.e("UserDataPoster", "sleep: ", e);
            }

            mUserOrientation = OrientationSensor.getInstance(mContext).getOrientation();

            try {
                synchronized(mPostRequestData) {
                    if ((Math.abs(Math.round(mPostRequestData.getDouble("x") - mUserX)) > 0)
                            || (Math.abs(Math.round(mPostRequestData.getDouble("y") - mUserY)) > 0)
                            || (Math.abs(Math.round(mPostRequestData.getDouble("orientation") - mUserOrientation)) > 0)) {
                        mPostRequestData.put("x", mUserX);
                        mPostRequestData.put("y", mUserY);
                        mPostRequestData.put("orientation", mUserOrientation);
                        isUpdated = true;
                    }
                }
            } catch (JSONException e) {
                android.util.Log.e("UserDataPoster", "run: " + e);
            }

            if (isUpdated) {
                mRequestQueue.add(mPostRequest);
                isUpdated = false;
            }
        }
    }
}
