package com.vise.baseble.exception.handler;

import android.util.Log;

import com.vise.baseble.exception.ConnectException;
import com.vise.baseble.exception.GattException;
import com.vise.baseble.exception.InitiatedException;
import com.vise.baseble.exception.OtherException;
import com.vise.baseble.exception.TimeoutException;

/**
 * @Description: 异常默认处理
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/8/14 10:35.
 */
public class DefaultBleExceptionHandler extends BleExceptionHandler {
    private static String TAG = "DefaultBleExceptionHandler";
    
    @Override
    protected void onConnectException(ConnectException e) {
        Log.e(TAG, e.getDescription());
    }
    
    @Override
    protected void onGattException(GattException e) {
        Log.e(TAG, e.getDescription());
    }
    
    @Override
    protected void onTimeoutException(TimeoutException e) {
        Log.e(TAG, e.getDescription());
    }
    
    @Override
    protected void onInitiatedException(InitiatedException e) {
        Log.e(TAG, e.getDescription());
    }
    
    @Override
    protected void onOtherException(OtherException e) {
        Log.e(TAG, e.getDescription());
    }
}
