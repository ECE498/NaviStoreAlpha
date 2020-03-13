package com.uwaterloo.navistore;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OrientationSensor implements SensorEventListener {
    private static OrientationSensor mOrientationSensor = null;

    SensorManager mSensorManager = null;

    // Intermediary arrays storing data to get orientation
    float[] mAccelerometerVector = null;
    float[] mMagneticFieldVector = null;
    float [] mRotationMatrix = null;

    // Orientation of the phone
    // [azimuth, pitch, roll]
    float[] mOrientationVector = null;

    private OrientationSensor(Context context) {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);

        Sensor magneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI);

        mAccelerometerVector    = new float[3];
        mMagneticFieldVector    = new float[3];
        mRotationMatrix         = new float[9];

        mOrientationVector      = new float[3];
    }

    public synchronized static OrientationSensor getInstance(Context context) {
        if (null == mOrientationSensor) {
            mOrientationSensor = new OrientationSensor(context);
        }
        return mOrientationSensor;
    }

    public float getOrientation() {
        mSensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerVector, mMagneticFieldVector);
        mSensorManager.getOrientation(mRotationMatrix, mOrientationVector);
        prettyPrint(mOrientationVector);
        return convertToDegrees(mOrientationVector[0]);
    }

    private float convertToDegrees(float radianValue) {
        return (float)((radianValue * 180.0) / Math.PI);
    }

    private void prettyPrint(float[] values) {
        String stringValue = "[";
        for (float value : values) {
            stringValue += convertToDegrees(value) + ", ";
        }   
        stringValue = stringValue.substring(0, stringValue.length() - 2) + "]";
        android.util.Log.d("OrientationSensor", "orientation values: " + stringValue);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                synchronized (mAccelerometerVector) {
                    mAccelerometerVector[0] = event.values[0];
                    mAccelerometerVector[1] = event.values[1];
                    mAccelerometerVector[2] = event.values[2];
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                synchronized (mMagneticFieldVector) {
                    mMagneticFieldVector[0] = event.values[0];
                    mMagneticFieldVector[1] = event.values[1];
                    mMagneticFieldVector[2] = event.values[2];
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
