package com.minorfish.car.twoth.ui.upload.add;//package com.minorfish.hospitalwaste.uploadwaste.add;
//
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.minorfish.hospitalwaste.abs.Constants;
//import com.minorfish.hospitalwasteyq.XBaseActivity;
//import com.tangjd.common.abs.PermissionManager;
//
///**
// * Created by tangjd on 2016/9/26.
// */
//public class BleSignInHelper extends XBaseActivity {
//    private BluetoothAdapter mBluetoothAdapter;
//    private boolean mSigningIn;
//    private Handler mHandler = new Handler();
//    private static final int RSST_ULTIMATE_VALUE = -60;
//    public String mFilterDeviceNameContains = Constants.ConnectScaleKey;
//    private static final int REQUEST_CODE_ENABLE_BT = 1001;
//
//    private Object mLockObj = new Object();
//    // Device scan callback.
//    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] data) {
//            if (!TextUtils.isEmpty(device.getName()) && device.getName().toLowerCase().contains(mFilterDeviceNameContains.toLowerCase())) {
//                if (!isSigningIn()) {
//                    setSigningIn(true);
//                    BtBean bean = new BtBean();
//                    bean.mDeviceName = device.getName();
//                    bean.mMajor = (data[25] << 8 & 65280) + (data[26] & 255);
//                    bean.mMinor = (data[27] << 8 & 65280) + (data[28] & 255);
//                    bean.mMac = device.getAddress();
//                    bean.mRssi = rssi;
//                    Log.e("TTT", "Rssi:" + rssi + " | " + bean.mDeviceName + " | " + bean.mMac + " | " + bean.mMajor + " | " + bean.mMinor);
//                    mBluetoothAdapter.stopLeScan(this);
//                    processLogin(device, bean);
//                }
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Use this check to determine whether BLE is supported on the device.  Then you can
//        // selectively disable BLE-related features.
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            showTipDialog("此设备不支持BLE", false, null);
//            return;
//        }
//
//        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
//        // BluetoothAdapter through BluetoothManager.
//        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        // Checks if Bluetooth is supported on the device.
//        if (mBluetoothAdapter == null) {
//            showTipDialog("此设备不支持蓝牙", false, null);
//            return;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                PermissionManager.mayRequestAccessFineLocation(BleSignInHelper.this);
//                // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
//                // fire an intent to display a dialog asking the user to grant permission to enable it.
//                if (!mBluetoothAdapter.isEnabled()) {
//                    if (!mBluetoothAdapter.isEnabled()) {
//                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BT);
//                    }
//                }
//                if (!isSigningIn()) {
//                    mBluetoothAdapter.startLeScan(mLeScanCallback);
//                }
//            }
//        }, 1000);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // User chose not to enable Bluetooth.
//        if (requestCode == REQUEST_CODE_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            showTipDialog("蓝牙已被禁止打开");
//            return;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    public void processLogin(BluetoothDevice bluetoothDevice, BtBean bean) {
//    }
//
//    public void setSigningIn(boolean signingIn) {
//        mSigningIn = signingIn;
//    }
//
//    private boolean isSigningIn() {
//        return mSigningIn;
//    }
//
//    public void restartScan() {
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setSigningIn(false);
//                mBluetoothAdapter.startLeScan(mLeScanCallback);
//            }
//        }, 500);
//    }
//
//}
