package com.minorfish.car.twoth.ui.upload;//package com.minorfish.car.twoth.ui.upload;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.RemoteException;
//import android.text.TextUtils;
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.chad.library.adapter.base.listener.OnItemClickListener;
//import com.gprinter.command.EscCommand;
//import com.gprinter.command.GpUtils;
//import com.gprinter.command.LabelCommand;
//import com.minorfish.hospitalwasteyq.R;
//import com.minorfish.hospitalwasteyq.abs.Constants;
//import com.minorfish.hospitalwasteyq.bus.BleDialogFragment;
//import com.minorfish.hospitalwasteyq.newwaste.ThreadPool;
//import com.minorfish.hospitalwasteyq.util.PreferenceKit;
//import com.minorfish.car.twoth.R;
//import com.tangjd.common.bluetooth.BluetoothBaseActivity;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Vector;
//
//public class ActPring extends BluetoothBaseActivity {
//
//    @Override
//    protected void onCreate(Bundle bundle) {
//        super.onCreate(bundle);
//        setContentView(R.layout.act_print_new);
//        if(UploadData.getInstance().printBitmap == null) {
//            finish();
//        }
//        setToolbarTitle("打印标签");
//        enableBackFinish();
//        ((ImageView) findViewById(R.id.ivPrint)).setImageBitmap(UploadData.getInstance().printBitmap);
//
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(!isFinishing()) {
//                    onBtConnectBtnClick();
//                }
//            }
//        }, 1);
//    }
//
//    @Override
//    public String getBtFilterKey() {
//        return Constants.GPrinterKey;
//    }
//
//    @Override
//    public void processOnBtConnected() {
//        if(!TextUtils.isEmpty(print_mac)) {
//            PreferenceKit.putString(this, "print_mac", print_mac);
//        }
//        Bitmap bitmap = UploadData.getInstance().printBitmap;
//        bitmap = adjustPhotoRotation(bitmap, 90);
//        sendLabel(bitmap);
//    }
//
//    private Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
//        Matrix m = new Matrix();
//        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
//        float targetX, targetY;
//        if (orientationDegree == 90) {
//            targetX = bm.getHeight();
//            targetY = 0;
//        } else {
//            targetX = bm.getHeight();
//            targetY = bm.getWidth();
//        }
//
//        final float[] values = new float[9];
//        m.getValues(values);
//
//        float x1 = values[Matrix.MTRANS_X];
//        float y1 = values[Matrix.MTRANS_Y];
//
//        m.postTranslate(targetX - x1, targetY - y1);
//
//        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.RGB_565);
//        Paint paint = new Paint();
//        Canvas canvas = new Canvas(bm1);
//        canvas.drawBitmap(bm, m, paint);
//
//        return bm1;
//    }
//
//    public void sendLabel(Bitmap b) {
//        LabelCommand tsc = new LabelCommand();
//        tsc.addSize(75, 50); // 设置标签尺寸，按照实际尺寸设置
//        tsc.addGap(2); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
//        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);// 设置打印方向
//        tsc.addReference(0, 0);// 设置原点坐标
//        tsc.addTear(EscCommand.ENABLE.ON); // 撕纸模式开启
//        tsc.addCls();// 清除打印缓冲区
//        tsc.addBitmap(40, 25, LabelCommand.BITMAP_MODE.OVERWRITE,  Math.min(b.getWidth(), 520), b);
//        //tsc.addText(30, 25, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, "简体字" );
//        tsc.addPrint(1, 1); // 打印标签
//        tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
//        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
//        Vector<Byte> datas = tsc.getCommand(); // 发送数据
//        byte[] bytes = GpUtils.ByteTo_byte(datas);
//        final String str = Base64.encodeToString(bytes, Base64.DEFAULT);
//        try {
//            threadPool = ThreadPool.getInstantiation();
//            threadPool.addTask( new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    try {
//                        sendLabelCommand(str);
//                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                onSuccess();
//                            }
//                        }, 2500);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } );
//            setResult(RESULT_OK);
////            if (mConnectStatus == BluetoothChatService.STATE_CONNECTED) {
////                // 断开连接，理解为disconnect，效果Nice
////                stopBTListener();
////            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void onSuccess() {
//        setResult(RESULT_OK);
//        ActUpload.startActivity(this);
//        finish();
//    }
//
//    private ThreadPool	threadPool;
//
//    private void sendLabelCommand(String b64) throws RemoteException {
//        byte[] datas = Base64.decode(b64, 0);
//        Vector vector = new Vector();
//        byte[] var9 = datas;
//        int var8 = datas.length;
//        for (int var7 = 0; var7 < var8; ++var7) {
//            byte b = var9[var7];
//            vector.add(Byte.valueOf(b));
//        }
//        sendDataImmediately(vector);
//    }
//
//    private static final int SUCCESS = 1;
//
//    private void sendDataImmediately(Vector<Byte> Command) {
//        Vector data = new Vector(Command.size());
//        for (int k = 0; k < Command.size(); ++k) {
//            if (data.size() >= 1024) {
//                writeDataImmediately(data);
//                data.clear();
//            }
//            data.add((Byte) Command.get(k));
//        }
//        writeDataImmediately(data);
//    }
//
//    private void writeDataImmediately(Vector<Byte> data) {
//        if (data != null && data.size() > 0) {
//            byte[] sendData = new byte[data.size()];
//            if (data.size() > 0) {
//                for (int e = 0; e < data.size(); ++e) {
//                    sendData[e] = ((Byte) data.get(e)).byteValue();
//                }
//                sendMessage(sendData);
//            }
//        }
//    }
//
//
//
//    private String print_mac = "";
//
//
//    private static final String TAG = "BleListActivity";
//    public static String EXTRA_DEVICE_ADDRESS = "device_address";
//    private BluetoothAdapter mBtAdapter;
//    private ArrayList<String> mNewDevicesArrayList;
//    private String mFilterDeviceNameContains;
//    private int requestCode;
//    private SearchDevicesType mSearchDeviceType;
//
////    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
////        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
////            mBtAdapter.cancelDiscovery();
////            String info = ((TextView)v).getText().toString();
////            String address = info.substring(info.length() - 17);
////            setResult(address);
////        }
////    };
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if ("android.bluetooth.device.action.FOUND".equals(action)) {
//                BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
//                String mac = device.getAddress();
//                String name = device.getName();
//                Log.e(TAG, "search "+name+","+mac);
//                if (TextUtils.isEmpty(name) || name.equalsIgnoreCase("null")) {
//                    return;
//                }
//
//                if(!TextUtils.isEmpty(print_mac) && print_mac.equalsIgnoreCase(mac)) {
//                    mNewDevicesArrayList.clear();
//                    addDeviceToList(mNewDevicesArrayList, name, mac);
//                    if (mBtAdapter.isDiscovering()) {
//                        Log.e(TAG, "cancelDiscovery hand");
//                        mBtAdapter.cancelDiscovery();
//                    }
//                    return;
//                }
//
//                if (TextUtils.isEmpty(mFilterDeviceNameContains)) {
//                    addDeviceToList(mNewDevicesArrayList, name, mac);
//                    return;
//                }
//
//                if (name.toLowerCase().contains(mFilterDeviceNameContains.toLowerCase())) {
//                    addDeviceToList(mNewDevicesArrayList, name, mac);
////                    if (mSearchDeviceType == BluetoothBaseActivity.SearchDevicesType.Connect) {
////                        ActPring.this.setResult(mac);
////                    }
//
//                    return;
//                }
//            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
//                setProgressBarIndeterminateVisibility(false);
//                mHandler.removeCallbacks(connect);
//////                setTitle("选择设备进行连接");
////                if (mNewDevicesArrayList.size() == 0) {
//////                    String noDevices = "没有发现设备";
//////                    mNewDevicesArrayList.add(noDevices);
////                    showToast("没有发现设备");
////                } else if(mNewDevicesArrayList.size() == 1) {
////                    ActPring.this.setResult(mNewDevicesArrayList.get(0));
////                } else {
////
////                }
//                doWork();
//            }
//
//        }
//    };
//
//    private void doWork() {
//        try {
//            Log.e(TAG, "doWork " + mNewDevicesArrayList.size());
//            if (mNewDevicesArrayList.size() == 0) {
//                showToast("没有发现设备");
//            } else if (mNewDevicesArrayList.size() == 1) {
//                ActPring.this.setResult(mNewDevicesArrayList.get(0), false);
//            } else {
//                BleDialogFragment dialog = BleDialogFragment.newInstance(mNewDevicesArrayList, new OnItemClickListener() {
//                    @Override
//                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
//                        setResult(mNewDevicesArrayList.get(position), true);
//                    }
//                });
//                dialog.show(getSupportFragmentManager(), "BleDialog");
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Handler mHandler = new Handler(Looper.getMainLooper());
//    private Runnable connect = new Runnable() {
//        @Override
//        public void run() {
//
//            if (mBtAdapter.isDiscovering()) {
//                Log.e(TAG, "cancelDiscovery hand");
//                mBtAdapter.cancelDiscovery();
//            }
//            Log.e(TAG, "connect "+mNewDevicesArrayList.size());
//            if(mNewDevicesArrayList.size() == 1) {
//                setResult(mNewDevicesArrayList.get(0), false);
//            }
//        }
//    };
//
//    protected void bleCreate(String name, SearchDevicesType type, int requestCode) {
//        this.mFilterDeviceNameContains = name;
//        this.mSearchDeviceType = type;
//        this.requestCode = requestCode;
////        this.setResult(0);
//        this.mNewDevicesArrayList = new ArrayList<>();
//        try {
//            print_mac = PreferenceKit.getString(this, "print_mac");
//            IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
//            this.registerReceiver(this.mReceiver, filter);
//            filter = new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
//            this.registerReceiver(this.mReceiver, filter);
//            this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//        try {
//            this.doDiscovery();
//            mHandler.removeCallbacks(connect);
//            mHandler.postDelayed(connect, 3000);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private synchronized void addDeviceToList(List<String> adapter, String name, String mac) {
//        boolean contains = false;
//
//        for(int i = 0; i < adapter.size(); ++i) {
//            if (((String)adapter.get(i)).contains(mac)) {
//                contains = true;
//                break;
//            }
//            try {
//                String str = adapter.get(i).split("\n")[0];
//                if (str.equalsIgnoreCase(name + "_BLE")) {
//                    contains = true;
//                    break;
//                }
//                if ((str + "_BLE").equalsIgnoreCase(name)) {
//                    adapter.remove(i);
//                    break;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (!contains) {
//            adapter.add(name + "\n" + mac);
//        }
//
//    }
//
//    private void setResult(String address, boolean click) {
//        try {
//            if (address.contains("\n")) {
//                address = address.substring(address.length() - 17);
//            }
//            Intent intent = new Intent();
//            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
////        this.setResult(-1, intent);
////        this.finish();
//            if(click) {
//                print_mac = address;
//                PreferenceKit.putString(this, "print_mac", "");
//            }
//            onActivityResult(requestCode, RESULT_OK, intent);
//            destroy();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        destroy();
//    }
//
//    void destroy() {
//        try {
//            if (this.mBtAdapter != null) {
//                this.mBtAdapter.cancelDiscovery();
//            }
//
//            this.unregisterReceiver(this.mReceiver);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void doDiscovery() {
//        this.setProgressBarIndeterminateVisibility(true);
////        this.setTitle("正在搜索设备...");
//        if (this.mBtAdapter.isDiscovering()) {
//            this.mBtAdapter.cancelDiscovery();
//        }
//
//        this.mBtAdapter.startDiscovery();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode == RESULT_OK) {
//
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    public void searchDeviceForSecureConnection(String filterDeviceNameContains) {
////        Intent serverIntent = new Intent(this, BleListActivity.class);
////        serverIntent.putExtra("extra_filter_device_name_contains", filterDeviceNameContains);
////        serverIntent.putExtra("extra_search_device_type", BluetoothBaseActivity.SearchDevicesType.ShowList);
////        this.startActivityForResult(serverIntent, 1);
//        bleCreate(filterDeviceNameContains, SearchDevicesType.ShowList, 1);
//    }
//
////    public void searchDeviceForInsecureConnection() {
////        this.searchDeviceForInsecureConnection((String)null);
////    }
//
//    public void searchDeviceForInsecureConnection(String filterDeviceNameContains) {
////        Intent serverIntent = new Intent(this, BleListActivity.class);
////        serverIntent.putExtra("extra_filter_device_name_contains", filterDeviceNameContains);
////        serverIntent.putExtra("extra_search_device_type", BluetoothBaseActivity.SearchDevicesType.ShowList);
////        this.startActivityForResult(serverIntent, 2);
//        bleCreate(filterDeviceNameContains, SearchDevicesType.ShowList, 2);
//    }
//
//    public void searchAndConnectDeviceForSecure(String filterDeviceNameContains) {
////        Intent serverIntent = new Intent(this, BleListActivity.class);
////        serverIntent.putExtra("extra_filter_device_name_contains", filterDeviceNameContains);
////        serverIntent.putExtra("extra_search_device_type", BluetoothBaseActivity.SearchDevicesType.Connect);
////        this.startActivityForResult(serverIntent, 1);
//        bleCreate(filterDeviceNameContains, SearchDevicesType.Connect, 1);
//    }
//
//    public void searchAndConnectDeviceForInsecure(String filterDeviceNameContains) {
////        Intent serverIntent = new Intent(this, BleListActivity.class);
////        serverIntent.putExtra("extra_filter_device_name_contains", filterDeviceNameContains);
////        serverIntent.putExtra("extra_search_device_type", BluetoothBaseActivity.SearchDevicesType.Connect);
////        this.startActivityForResult(serverIntent, 2);
//        bleCreate(filterDeviceNameContains, SearchDevicesType.Connect, 2);
//    }
//
//    public static void startActivity(Context context) {
//        Intent intent = new Intent(context, ActPring.class);
//        context.startActivity(intent);
//    }
//}