package com.minorfish.car.twoth.ui.upload;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.minorfish.car.twoth.ui.XBaseActivity;
import com.tangjd.common.abs.BaseActivity;

import java.util.List;

public abstract class BleScanFragment extends Fragment {

    protected OnFragmentInteractionListener mListener;

    protected XBaseActivity getAct() {
        if(mListener == null) return null;

        return (XBaseActivity) mListener;
    }

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mBackHandler;
    private HandlerThread mBackThread;
    private static final int REQUEST_ENABLE_BT = 1;
    private ConnectState mState;
    private ScanCallback mScanCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    public String mFilterDeviceNameContains;
    public int mFilterRssi;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        this.mState = BleScanFragment.ConnectState.STATE_NONE;
        this.mScanCallback = new ScanCallback() {
            public void onScanResult(int callbackType, final ScanResult result) {
                super.onScanResult(callbackType, result);
                BleScanFragment.this.mBackHandler.post(new Runnable() {
                    public void run() {
                        boolean filterRssi = BleScanFragment.this.mFilterRssi != 0;
                        boolean filterDeviceName = !TextUtils.isEmpty(BleScanFragment.this.mFilterDeviceNameContains);
                        boolean filterRssiMatch = false;
                        if (filterRssi) {
                            filterRssiMatch = result.getRssi() > BleScanFragment.this.mFilterRssi;
                        }

                        boolean filterDeviceNameMatch = false;
                        if (filterDeviceName) {
                            filterDeviceNameMatch = !TextUtils.isEmpty(result.getDevice().getName()) && !TextUtils.isEmpty(BleScanFragment.this.mFilterDeviceNameContains)
                                    && result.getDevice().getName().toLowerCase().contains(BleScanFragment.this.mFilterDeviceNameContains.toLowerCase());
                        }

                        if ((!filterRssi || filterRssiMatch) && (!filterDeviceName || filterDeviceNameMatch)) {
                            BleScanFragment.this.parse(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                        }
                    }
                });
            }

            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
        this.mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                BleScanFragment.this.mBackHandler.post(new Runnable() {
                    public void run() {
                        boolean filterRssi = BleScanFragment.this.mFilterRssi != 0;
                        boolean filterDeviceName = !TextUtils.isEmpty(BleScanFragment.this.mFilterDeviceNameContains);
                        boolean filterRssiMatch = false;
                        if (filterRssi) {
                            filterRssiMatch = rssi > BleScanFragment.this.mFilterRssi;
                        }

                        boolean filterDeviceNameMatch = false;
                        if (filterDeviceName) {
                            filterDeviceNameMatch = !TextUtils.isEmpty(device.getName()) && device.getName().toLowerCase().contains(BleScanFragment.this.mFilterDeviceNameContains.toLowerCase());
                        }

                        if ((!filterRssi || filterRssiMatch) && (!filterDeviceName || filterDeviceNameMatch)) {
                            BleScanFragment.this.parse(device, rssi, scanRecord);
                        }
                    }
                });
            }
        };
    }

    public void setState(BleScanFragment.ConnectState state) {
        this.mState = state;
        if (state != BleScanFragment.ConnectState.STATE_NONE && state != BleScanFragment.ConnectState.STATE_LISTEN && state != BleScanFragment.ConnectState.STATE_CONNECTION_FAILED && state != BleScanFragment.ConnectState.STATE_DISCONNECTED) {
            if (state == BleScanFragment.ConnectState.STATE_CONNECTING || state == BleScanFragment.ConnectState.STATE_CONNECTED) {
                this.scanLeDevice(false);
            }
        } else {
            this.scanLeDevice(true);
        }

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 0) {
                Toast.makeText(getAct(), "蓝牙不可用", Toast.LENGTH_SHORT).show();
            } else if (resultCode == -1) {
                this.scanLeDevice(true);
            }
        }

    }

    public void scanLeDevice(boolean enable) {
        if (!this.mScanning || !enable) {
            if (this.mScanning || enable) {
                try {
                    if (enable) {
                        if (Build.VERSION.SDK_INT < 21) {
                            this.mScanning = true;
                            this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
                        } else {
                            this.mScanning = true;
                            this.mBluetoothAdapter.getBluetoothLeScanner().startScan(this.mScanCallback);
                        }
                    } else if (Build.VERSION.SDK_INT < 21) {
                        this.mScanning = false;
                        this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
                    } else {
                        this.mScanning = false;
                        this.mBluetoothAdapter.getBluetoothLeScanner().stopScan(this.mScanCallback);
                    }

//                    this.invalidateOptionsMenu();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void parse(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        getAct().runOnUiThread(new Runnable() {
            public void run() {
                Log.e("TTTTTT", "----------Rssi:" + rssi + "      " + device.getAddress() + "      " + device.getName() + "      " + ((scanRecord[27] << 8 & '\uff00') + (scanRecord[28] & 255)));
                BleScanFragment.this.processLogin(device, rssi, scanRecord, (scanRecord[25] << 8 & '\uff00') + (scanRecord[26] & 255), (scanRecord[27] << 8 & '\uff00') + (scanRecord[28] & 255));
            }
        });
    }

    public abstract String getFilterDeviceName();

    public abstract int getFilterRssi();

    public abstract void processLogin(BluetoothDevice var1, int var2, byte[] var3, int var4, int var5);

    public static enum ConnectState {
        STATE_NONE("无连接"),
        STATE_LISTEN("监听中"),
        STATE_CONNECTING("连接中"),
        STATE_CONNECTED("已连接"),
        STATE_DISCONNECTED("已断开"),
        STATE_CONNECTION_FAILED("连接失败");

        public String mDesc;

        private ConnectState(String desc) {
            this.mDesc = desc;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    protected void enableBle() {
        try {
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (this.mBluetoothAdapter == null) {
                getAct().showTipDialog("此设备不支持蓝牙", false, (DialogInterface.OnClickListener) null);
            } else {
                this.mFilterDeviceNameContains = this.getFilterDeviceName();
                this.mFilterRssi = this.getFilterRssi();
                getAct().mayRequestPermission(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN"}, new BaseActivity.PermissionRequestCallback() {
                    public void onSuccess() {
                        try {

                            if (BleScanFragment.this.mBluetoothAdapter.enable()) {
                                BleScanFragment.this.scanLeDevice(true);
                            } else {
                                Intent enableBtIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
                                BleScanFragment.this.startActivityForResult(enableBtIntent, 1);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void restartBle() {
        getAct().mayRequestPermission(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN"}, new BaseActivity.PermissionRequestCallback() {
            public void onSuccess() {
                try {
                    if (mBluetoothAdapter != null) {
                        mBluetoothAdapter.disable();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    enableBle();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 2000);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void startBle() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                init();
            }
            this.mBackThread = new HandlerThread("BleScanResultThread");
            this.mBackThread.start();
            this.mBackHandler = new Handler(this.mBackThread.getLooper());
            if (!getAct().getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
                getAct().showTipDialog("此设备不支持BLE", false, (DialogInterface.OnClickListener)null);
            } else {
                enableBle();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void stopBle() {
        try {
            this.scanLeDevice(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
