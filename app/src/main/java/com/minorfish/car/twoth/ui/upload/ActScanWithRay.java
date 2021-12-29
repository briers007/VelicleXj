package com.minorfish.car.twoth.ui.upload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;
import android.os.Bundle;

import com.tangjd.common.utils.Log;

/**
 * @author honghui @time: 2020/4/25 17:04
 * class:{@link ActScanWithRay}
 */
public class ActScanWithRay extends ActScanQr {

    private ScanDevice mScanDevice;
    private final static String ACTION_SCAN = "scan.rcv.message";
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                byte[] barocode = intent.getByteArrayExtra("barocode");
                int barocodelen = intent.getIntExtra("length", 0);
                byte temp = intent.getByteExtra("barcodeType", (byte) 0);
                Log.i("debug", "----codetype--" + temp);
                String scanResult = new String(barocode, 0, barocodelen);
                try {
                    mScanDevice.stopScan();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                callbackResult(scanResult);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // 初始化扫描二维码
    @Override
    protected void startScanDevice() {
        try {
            if (mScanDevice == null) {
                mScanDevice = new ScanDevice();
                mScanDevice.setOutScanMode(0); // 接收广播, 1为直接输出到文本框
                mScanDevice.setScanLaserMode(8); // 关闭连续扫码,4为开启连续扫码
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_SCAN);
            registerReceiver(mScanReceiver, filter);
            if (mScanDevice != null) {
                mScanDevice.openScan(); // 开启扫描开关
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void stopScanDevice() {
        try {
            if (mScanDevice != null) {
                mScanDevice.closeScan(); // 关闭扫描开关
                mScanDevice.stopScan(); // 停止扫描
            }
            if (mScanReceiver != null) {
                unregisterReceiver(mScanReceiver);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // 扫描科室结构
    @Override
    protected void callbackResult(String scanResult) {
        super.callbackResult(scanResult);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScanDevice = null;
    }
}

