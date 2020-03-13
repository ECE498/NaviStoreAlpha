package com.uwaterloo.navistore.webInterface;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class UserDataPostRequest extends Request<JSONObject> {

    private Map<String, String> mParams;
    private JSONObject mBody;
    private Response.Listener<JSONObject> mResponseListener;
    private JSONObject mResponseData;
;
    public UserDataPostRequest(String url, Map<String, String> params, JSONObject body, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mParams = params;
        this.mBody = body;
        this.mResponseListener = responseListener;
        this.mResponseData = new JSONObject();
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] body = null;
        try {
            synchronized(mBody) {
                body = this.mBody.toString().getBytes("utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            android.util.Log.e("UserDataPostRequest", "getBody(): ", e);
        }
        return body;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        if (response != null) {
            try {
                mResponseData.put("statusCode", response.statusCode);
//                mResponseData.getJSONObject(new String(response.data));
            } catch (JSONException e) {
                android.util.Log.e("UserDataPostRequest", "parseNetworkResponse JSON data: ", e);
            }
            // can get more details such as response.headers
        }
        return Response.success(mResponseData, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        android.util.Log.d("UserDataPostRequest", "deliverResponse: " + response);
    }
}
