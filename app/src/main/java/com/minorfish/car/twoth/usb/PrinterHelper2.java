package com.minorfish.car.twoth.usb;//package com.minorfish.clinicwaste.usb;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.hardware.usb.UsbDevice;
//import android.hardware.usb.UsbManager;
//import android.os.Handler;
//import android.os.Message;
//import android.widget.Toast;
//
//import com.dp.dp_usblist.USBFactory;
//import com.minorfish.clinicwaste.R;
//import com.minorfish.clinicwaste.abs.App;
//import com.tangjd.common.utils.Log;
//import static android.widget.Toast.LENGTH_LONG;
//
///**
// * Author: Administrator
// * Date: 2018/4/11
// */
//
//public class PrinterHelper2 {
//    private final int CONNECTRESULT= 001;
//    private static PrinterHelper2 sPrinterHelper;
//    private Context mContext;
//
//    private USBFactory factory;
//    private UsbManager usbManager;
//    private UsbDevice device;
//
//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case CONNECTRESULT:
//                    if(factory!=null) {
//                        if(factory.is_connecusb())
//                        {
//                            mHandler.removeMessages(CONNECTRESULT);
//                            mHandler.sendEmptyMessageDelayed(CONNECTRESULT, 2000);
//                        } else {
//                            reConnect();
//                        }
//                    }
//                    break;
//                case 2:
//                    reConnect();
//                    break;
//            }
//        }
//    };
//
//    public PrinterHelper2(Context context) {
//        mContext = context;
//        factory = USBFactory.getUsbFactory();
//    }
//
//    public void printTest() {
////        if(factory == null || !factory.is_connecusb()) {
////            automaticConnection();
////            Toast.makeText(mContext, "请稍等", LENGTH_LONG).show();
////            return;
////        }
//        if(factory == null || !factory.is_connecusb())
//        {
//            Toast.makeText(mContext,  "未连接，请稍等", LENGTH_LONG).show();
//            reConnect();
//            return;
//        }
//        if(!factory.Check_Paper())
//        {
//            Toast.makeText(mContext,  R.string.printer_does_not_have_paper, LENGTH_LONG).show();
//            return;
//        }
//        factory.LabelBegin(384, 250);    //?????????????????
//        factory.LableText(10, 50, 1, 0, "fdsfdsfsdfsd");
//        factory.LableText(10, 100, 2, 0, "fdsfdsfsdfsd");
//        factory.Labelend();
//        factory.PaperCut();
//    }
//
//    public static PrinterHelper2 getInstance() {
//        if (sPrinterHelper == null) {
//            sPrinterHelper = new PrinterHelper2(App.getApp().getApplicationContext());
//        }
//        return sPrinterHelper;
//    }
//
//    private boolean t=false;
//    //private boolean isConnect = false;
//    private boolean isconnecting=false;
//    public void connect(final UsbManager usbManager,final UsbDevice device) {
//        this.device = device;
//        this.usbManager = usbManager;
//        if(isconnecting){
//            return;
//        }
//        isconnecting = true;
//        Log.e("TTTTTT", "portName " + device.getDeviceName().substring(5));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    t = factory.connectUsb(usbManager, device);
//                    Thread.sleep(2000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//        isconnecting = false;
//        if(!t) {
//            mHandler.removeMessages(CONNECTRESULT);
//            mHandler.sendEmptyMessageDelayed(CONNECTRESULT, 5000);
//        }else{
//            Log.e("TTTTTT", "连接成功哈哈哈哈");
//        }
//    }
//
//    public void reConnect() {
//        if(isconnecting){
//            return;
//        }
//        isconnecting = true;
//        Log.e("TTTTTT", "portName " + device.getDeviceName().substring(5));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    t = factory.connectUsb(usbManager, device);
//                    Thread.sleep(2000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        isconnecting = false;
//        if(!t) {
//            mHandler.removeMessages(CONNECTRESULT);
//            mHandler.sendEmptyMessageDelayed(CONNECTRESULT, 5000);
//        }else{
//            Log.e("TTTTTT", "重新连接连接成功哈哈哈哈哈哈哈哈哈 ");
//        }
//    }
//
////    private boolean accredit=false;
////    private boolean isfind=false;
////    private HashMap<String, UsbDevice> deviceList;
////    private Iterator<UsbDevice> deviceIterator;
////    public void AutomaticConnection() {
////        int mvid= PreferenceKit.getInt(mContext,"vid",-1);
////        if(isconnecting)
////        {
////            return;
////        }
////        isfind=false;
////        deviceList = usbManager.getDeviceList();
////        deviceIterator = deviceList.values().iterator();
////        if (deviceList.size() > 0) {
////            while (deviceIterator.hasNext()) {
////                final UsbDevice device = deviceIterator.next();
////                device.getInterfaceCount();
////                int vid=device.getVendorId();
////                if(vid==mvid) {
////                    isfind=true;
////                    PendingIntent mPermissionIntent ;
////                    mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(App.getApp().getApplicationInfo().packageName), 0);
////                    if (!usbManager.hasPermission(device)) {
////                        if(!accredit)
////                        {
////                            usbManager.requestPermission(device,mPermissionIntent);
////                            accredit=true;
////                        }
////                        mHandler.sendEmptyMessageDelayed(2, 2000);
////                    } else {
////                        accredit=false;
////                        isconnecting=true;
////                        t=factory.connectUsb(usbManager, device);
////                        isconnecting=false;
////                        if(t) {
////                            Log.e("TTTTTT", "连接成功六六六六六");
////                        }else {
////                            Log.e("TTTTTT", "连接失败六六六六六");
////                        }
////                        mHandler.removeMessages(CONNECTRESULT);
////                        mHandler.sendEmptyMessage(CONNECTRESULT);
////                    }
////                }
////            }
////            if(!isfind)
////            {
////                mHandler.removeMessages(2);
////                mHandler.sendEmptyMessageDelayed(2, 1000);
////            }
////        }
////    }
//
//    public void close() {
//        if(factory != null) {
//            factory.CloseUSB();
//        }
//    }
//}
