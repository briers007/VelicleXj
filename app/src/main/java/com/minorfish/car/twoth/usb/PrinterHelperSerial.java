package com.minorfish.car.twoth.usb;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import com.dp.dp_serialportlist.Serialport_Factory;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.ui.main.PrintBean;
import com.minorfish.car.twoth.ui.weight.BagInBean;
import com.tangjd.common.utils.Log;

import java.text.SimpleDateFormat;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Author: Administrator
 * Date: 2018/4/11
 */

public class PrinterHelperSerial {

    private final int CONNECTRESULT= 001;
    private static PrinterHelperSerial sPrinterHelper;
    private Context mContext;
    private static final String TAG=PrinterHelperSerial.class.getSimpleName();

    public static Serialport_Factory sf;

    public PrinterHelperSerial(Context context) {
        mContext = context;
        sf=Serialport_Factory.getSerialport_Factory(context);
    }

    public Boolean printBagIn(PrintBean bean) {
        if(sf == null || !sf.isConnection())
        {
            Toast.makeText(mContext,  "未连接，请稍等", LENGTH_LONG).show();
            connect();
            return false;
        }
        //sf.LabelBegin(384, 250);    //小纸尺寸
        sf.LabelBegin(384, 560);    //大纸尺寸
        if(bean.hospitalName == null) {
            bean.hospitalName = "";
        }
        if(bean.hospitalName.length() <= 3) {
            sf.LableText(100, 50, 2, 0, bean.hospitalName);
        } else if(bean.hospitalName.length() <= 6) {
            sf.LableText(80, 50, 2, 0, bean.hospitalName);
        } else if(bean.hospitalName.length() <= 11) {
            sf.LableText(80, 50, 1, 0, bean.hospitalName);
        } else {
            sf.LableText(39, 50, 1, 0, bean.hospitalName);
        }
        sf.LableText(180, 105, 1, 0, bean.xinguan);
        sf.LableText(33, 175, 1, 0, bean.keShiName);
        sf.LableText(33, 220, 1, 0, bean.typeName);
        sf.LableText(33, 265, 1, 0, bean.weight);
        sf.LableText(33, 310, 1, 0, bean.time);
        sf.LableText(33, 410, 1, 0, bean.sjr);
        sf.LableText(33, 460, 1, 0, bean.jjr);
        sf.LabelQRCode(230, 380, 3, bean.qrcode);

        sf.Labelend();
        sf.PaperCut();

        return true;
    }

    public Boolean printBagInView(View view, Bitmap bitmap) {
        if(sf == null || !sf.isConnection())
        {
            Toast.makeText(mContext,  "未连接，请稍等", LENGTH_LONG).show();
            connect();
            return false;
        }
        sf.LabelBegin(384, 560);    //大纸尺寸
//        sf.LableViewImage(13, 40, 1, view);
        sf.LableImage(13, 40, 3, bitmap);

        sf.Labelend();
        sf.PaperCut();

        return true;
    }

    public static PrinterHelperSerial getInstance(Context context) {
        if (sPrinterHelper == null) {
            sPrinterHelper = new PrinterHelperSerial(context);
        }
        return sPrinterHelper;
    }

    public void connect(){
        String baudrate="ttyS2";
        String com_name="115200";
        boolean isopen=false;
//        isopen=sf.OpenPort("ttyS0", "115200");
        isopen=sf.OpenPort("ttyS1", "115200");
        if(isopen)
        {
            Log.e(TAG, "串口打开成功 ");
        }else {
            Log.e(TAG, "串口打开失败 ");
        }
    }

}