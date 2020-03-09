package com.uwaterloo.navistore.test;

public class DataCollector {
    private double[] mData;
    private int mCounter;

    private double mCurrentMean;
    private double mCurrentStandardDeviation;

    public DataCollector() {
        mData = new double[1000];
        mCounter = 0;
    }

    public DataCollector(int numDataPoints) {
        mData = new double[numDataPoints];
        mCounter = 0;
    }

    public void collectData(float newValue) {
        if (mCounter < mData.length) {
            mData[mCounter++] = newValue;

            mCurrentMean = getSum(mData, mCounter) / mCounter;
            mCurrentStandardDeviation = Math.sqrt((getSumOfSquares(mData, mCounter) - (mCounter * mCurrentMean * mCurrentMean)) / (mCounter - 1));
            android.util.Log.d("DataCollector", "[" + mCounter + "] recent: " + newValue + "; mean: " + mCurrentMean + "; std dev: " + mCurrentStandardDeviation);
        }
    }

    private float getSum(double[] data, int size) {
        float sum = 0.0f;
        for (int index = 0; index < size; index++) {
            sum += data[index];
        }
        return sum;
    }

    private float getSumOfSquares(double[] data, int size) {
        float sumOfSquares = 0.0f;
        for (int index = 0; index < size; index++) {
            sumOfSquares += (data[index] * data[index]);
        }
        return sumOfSquares;
    }
}
