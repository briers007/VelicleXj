package com.minorfish.car.twoth.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.util.Utils;
import com.tangjd.common.utils.ByteUtil;
import com.tangjd.common.utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author honghui @time: 2020/4/25 12:15
 * class:{@link TestActivity}
 */
public class TestActivity extends XBaseActivity {

    private TextView tv;
    SerialPortManager mSerialPortManager;
    private Device mDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test);

        tv = findViewById(R.id.tv);

        findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trySerialTest();
            }
        });

        findViewById(R.id.btnJz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jz();
            }
        });

        findViewById(R.id.btnJz2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jz2();
            }
        });
    }

    private void parseData(byte[] array) {
        try {
            System.out.println("data to 16hex:"+byte2Hex(array));
            if (array != null && array.length >= 8 && array[0] == 0x0A && array[1] == 0x0D && (array[2] == 0x2B || array[2] == 0x2D)) {
                Log.e("TTTTTT", ByteUtil.ByteArrayToHexString(array));
                String ffff = Utils.getChars(array).trim();
                int rstInt = Integer.parseInt(ffff);
                float result = ((float) rstInt) / 100f;
                if (array[2] == 0x2D) {
                    System.out.println("???????????????" + (-result));
                } else {
                    System.out.println("???????????????" + result);
                }
            }
        } catch (Exception e) {
            int a = 0;
        }
    }

    byte[] read = new byte[]{0X02, 0X52, 0X53, 0X45, 0X40, (byte) 0XAA, 0X0D};
    // ????????????
    byte[] zero = new byte[]{0x02, 0x52, 0x5A, 0x52, 0x7F, (byte)0xFD, 0x0D};
    // ???????????????0.1kg ????????????600kg - ???????????????
//    byte[] jd1 = new byte[]{0x02, 0x53, 0x45, 0x54, 0x40, 0x20, 0x36, 0x30, 0x30, 0x2E, 0x30, 0x30, 0x69, 0x30, 0x31, 0x32, (byte)0xE8, 0x0D};
    // ??????????????????0.05kg ????????????600kg - ???????????????
    byte[] jd05 = new byte[]{0x02, 0x53, 0x45, 0x54, 0x40, 0x20, 0x36, 0x30, 0x30, 0x2E, 0x30, 0x30, 0x68, 0x30, 0x31, 0x32, (byte)0xEB, 0x0D};
    // ???????????????0.02kg ????????????200kg - ???????????????
    byte[] jd02 = new byte[]{0x02, 0x53, 0x45, 0x54, 0x40, 0x20, 0x32, 0x30, 0x30, 0x2E, 0x30, 0x30, 0x67, 0x30, 0x31, 0x32, (byte)0xE6, 0x0D};
    private void jz() {
        if(mSerialPortManager != null && mDevice != null) {
//            boolean openSerialPort = mSerialPortManager.openSerialPort(mDevice.getFile(), 9600);
//            if (openSerialPort) {
                try {
                    System.out.println("?????????????????????"+mSerialPortManager.sendBytes(read));
                    Thread.sleep(3000);
                    System.out.println("?????????????????????"+mSerialPortManager.sendBytes(jd02));
                    Thread.sleep(3000);
                    System.out.println("???????????????????????????"+mSerialPortManager.sendBytes(zero));
                    Thread.sleep(3000);//????????????????????????
                    System.out.println("??????????????????");
//                    mSerialPortManager.closeSerialPort();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }
        }
    }

    byte[] fm25 = new byte[]{0X02, 0X43, 0X41, 0X53, 0X7F, 0X20, 0X20, 0X32, 0X35, 0X2E, 0X30, 0X30, (byte) 0X8B, 0X0D,};
    // ????????????
    private void jz2() {
        if(mSerialPortManager != null && mDevice != null) {
//            boolean openSerialPort = mSerialPortManager.openSerialPort(mDevice.getFile(), 9600);
//            if (openSerialPort) {
                System.out.println("???????????????????????????"+mSerialPortManager.sendBytes(fm25));
                try {
                    Thread.sleep(3000);//????????????????????????
                    System.out.println("????????????????????????");
//                    mSerialPortManager.closeSerialPort();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }
        }
    }

    public void trySerialTest() {
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        ArrayList<Device> devices = serialPortFinder.getDevices();
        System.out.println("???????????????????????????" + devices.size());
        mSerialPortManager = new SerialPortManager();

        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                System.out.println("???????????????" + Arrays.toString(bytes));
                parseData(bytes);
            }

            @Override
            public void onDataSent(byte[] bytes) {
                System.out.println("???????????????" + Arrays.toString(bytes));
            }
        });

        mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File device) {
                System.out.print("?????????????????????????????????");
                System.out.println(device.getName());//????????????ID
            }

            @Override
            public void onFail(File device, Status status) {
                System.out.print("?????????????????????????????????");
                System.out.println(device.getName() + status);
            }
        });

        for (Device dev : devices) {
            if ("ttyS2".equalsIgnoreCase(dev.getName())) {
                boolean openSerialPort = mSerialPortManager.openSerialPort(dev.getFile(), 9600);
                if (openSerialPort) {
                    mDevice = dev;
//                    mSerialPortManager.sendBytes("send something".getBytes());
                    mSerialPortManager.sendBytes(new byte[]{0X02, 0X52, 0X53, 0X45, 0X40, (byte) 0XAA, 0X0D});

                    try {
                        Thread.sleep(1000);//????????????????????????
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        System.out.println("????????????");
//        mSerialPortManager.closeSerialPort();
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1????????????????????????0??????
                stringBuffer.append("0");
            }
            stringBuffer.append(temp).append(" ");
        }
        return stringBuffer.toString();
    }
}
