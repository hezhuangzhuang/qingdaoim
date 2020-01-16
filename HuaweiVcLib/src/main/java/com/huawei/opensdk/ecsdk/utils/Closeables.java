package com.huawei.opensdk.ecsdk.utils;

import android.util.Log;

import com.huawei.opensdk.ecsdk.common.UIConstants;

import java.io.Closeable;
import java.io.IOException;


public class Closeables {
    public static void closeCloseable(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }
}
