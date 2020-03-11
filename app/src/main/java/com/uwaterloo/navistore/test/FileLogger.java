package com.uwaterloo.navistore.test;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogger {

    private static FileLogger mFileLogger = null;

    private Context mContext = null;

    private FileWriter mLogFileWriter = null;

    private FileLogger() {}

    public void setContext(Context context) {
        mContext = context;
    }

    public void open(String fileName) {
        File root = new File(mContext.getExternalFilesDir(null).getAbsolutePath());
        File logFile = new File(root, fileName);
        try {
            mLogFileWriter = new FileWriter(logFile);
        } catch (IOException e) {
            android.util.Log.e("FileLogger", "Failed to create new FileWriter", e);
        }
    }

    public void close() {
        try {
            mLogFileWriter.close();
        } catch (IOException e) {
            android.util.Log.e("FileLogger", "Failed to create new FileWriter", e);
        }
        mLogFileWriter = null;
    }

    public void logToFile(String line) {
        try {
            mLogFileWriter.append(line + "\n");
            mLogFileWriter.flush();
        } catch (IOException e) {
            android.util.Log.e("FileLogger", "Faileed to log line to file", e);
        }
    }

    public static FileLogger getInstance() {
        if (null == mFileLogger) {
            mFileLogger = new FileLogger();
        }
        return mFileLogger;
    }
}
