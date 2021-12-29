package com.minorfish.car.twoth.bus;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.minorfish.car.twoth.R;
import com.tangjd.common.bluetooth.BluetoothBaseActivity;

public class BleListActivity extends Activity {
    private static final String TAG = "BleListActivity";
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private String mFilterDeviceNameContains;
    private BluetoothBaseActivity.SearchDevicesType mSearchDeviceType;
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            BleListActivity.this.mBtAdapter.cancelDiscovery();
            String info = ((TextView)v).getText().toString();
            String address = info.substring(info.length() - 17);
            BleListActivity.this.setResult(address);
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.device.action.FOUND".equals(action)) {
                BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                String mac = device.getAddress();
                String name = device.getName();
                Log.e(TAG, "search "+name+","+mac);
                if (TextUtils.isEmpty(name) || name.equalsIgnoreCase("null")) {
                    return;
                }

                if (TextUtils.isEmpty(BleListActivity.this.mFilterDeviceNameContains)) {
                    BleListActivity.this.addDeviceToList(BleListActivity.this.mNewDevicesArrayAdapter, name, mac);
                    return;
                }

                if (name.toLowerCase().contains(BleListActivity.this.mFilterDeviceNameContains.toLowerCase())) {
                    BleListActivity.this.addDeviceToList(BleListActivity.this.mNewDevicesArrayAdapter, name, mac);
                    if (BleListActivity.this.mSearchDeviceType == BluetoothBaseActivity.SearchDevicesType.Connect) {
                        BleListActivity.this.setResult(mac);
                    }

                    return;
                }
            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                BleListActivity.this.setProgressBarIndeterminateVisibility(false);
                BleListActivity.this.setTitle("选择设备进行连接");
                if (BleListActivity.this.mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "没有发现设备";
                    BleListActivity.this.mNewDevicesArrayAdapter.add(noDevices);
                }
            }

        }
    };

    public BleListActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(5);
        this.setContentView(R.layout.act_ble_list);
//        TextView tvTitle = (TextView)this.findViewById(16908310);
//        tvTitle.setTextSize(2, 14.0F);
        this.mFilterDeviceNameContains = this.getIntent().getStringExtra("extra_filter_device_name_contains");
        this.mSearchDeviceType = (BluetoothBaseActivity.SearchDevicesType)this.getIntent().getSerializableExtra("extra_search_device_type");
        this.setResult(0);
        this.mNewDevicesArrayAdapter = new ArrayAdapter(this, R.layout.adapter_ble_item);
        ListView newDevicesListView = (ListView)this.findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(this.mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(this.mDeviceClickListener);
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
        this.registerReceiver(this.mReceiver, filter);
        filter = new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        this.registerReceiver(this.mReceiver, filter);
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        this.doDiscovery();
    }

    private synchronized void addDeviceToList(ArrayAdapter<String> adapter, String name, String mac) {
        boolean contains = false;

        for(int i = 0; i < adapter.getCount(); ++i) {
            if (((String)adapter.getItem(i)).contains(mac)) {
                contains = true;
                break;
            }
        }

        if (!contains) {
            adapter.add(name + "\n" + mac);
        }

    }

    private void setResult(String address) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        this.setResult(-1, intent);
        this.finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.mBtAdapter != null) {
            this.mBtAdapter.cancelDiscovery();
        }

        this.unregisterReceiver(this.mReceiver);
    }

    private void doDiscovery() {
        this.setProgressBarIndeterminateVisibility(true);
        this.setTitle("正在搜索设备...");
        if (this.mBtAdapter.isDiscovering()) {
            this.mBtAdapter.cancelDiscovery();
        }

        this.mBtAdapter.startDiscovery();
    }
}
