package com.minorfish.car.twoth.ui.upload;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.aio.serial.SerialPortHelper;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.util.Utils;
import com.tangjd.common.utils.ByteUtil;

import java.util.Arrays;

import javax.security.auth.login.LoginException;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment5 extends Fragment {
    private static final String TAG=Fragment5.class.getSimpleName();
    private TextView tvWeight;
    private TextView tvZero;
    private TextView tvAgain;
    private TextView tvValue;
    private int sameNume = 0;
    private float mLastWeight = 0;
    private float mWeight = 0;
    private float zeroWeight = 0;
    private boolean mGetWeight = false;

    private float totalWeight = 0f;// 已上传总重量

    private float qupiWeight = 0f; // 已去皮重量

    private float sjWeight = 0f; // 实际重量

    /**
     * SerialPortHelper回调方法传回串口通信的数据，返回byte数据
     */

    private SerialPortHelper serialPortHelper = new SerialPortHelper(new SerialPortHelper.OnGetData() {
        @Override
        public void onDataReceived(byte[] bytes) {
           parseData(bytes);
        }
    });

    //解析并处理数据
    private void parseData(byte[] array) {
        Log.i(TAG, "parseData--传感器返回数据: "+ Arrays.toString(array));
        try {
            if (array != null && array.length >= 8 && array[0] == 0x0A && array[1] == 0x0D && (array[2] == 0x2B || array[2] == 0x2D)) {
                String resultStr=ByteUtil.ByteArrayToHexString(array);
                String str = Utils.getChars(array).trim();
                Log.i(TAG, "parseData: "+"resultStr: "+resultStr+"----str: "+str);
                int rstInt = Integer.parseInt(str);
                Log.i(TAG, "parseData---rstInt: "+rstInt);
                float result = ((float) rstInt) / 100f;
                Log.i(TAG, "parseData float result: "+"称重"+result);
                if (array[2] == 0x2D) {
                    onBtResultGet(-result);
                } else {
                    onBtResultGet(result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OnFragmentInteractionListener mListener;


    protected <A extends XBaseActivity> A getAct() {
        if (mListener == null) return null;

        return (A) mListener;
    }


    public Fragment5() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment5, container, false);
        tvWeight = root.findViewById(R.id.tv_weight);
        tvZero = root.findViewById(R.id.tvZero);
        tvAgain = root.findViewById(R.id.tvAgain);
        tvValue = root.findViewById(R.id.tvValue);

        if(UploadData.getInstance().mAddedWasteBeans.isEmpty()) {
            tvZero.setVisibility(View.VISIBLE);
        } else {
            tvZero.setVisibility(View.VISIBLE);
        }
        tvZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setZero();
            }
        });

        tvAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serialPortHelper!=null){
                   /* boolean isOpen = App.getApp().getOpenPort();
                    if(isOpen){
                        serialPortHelper.closeSerialPort();
                    }*/
                    if(serialPortHelper!=null){
                        serialPortHelper.openSerialPort();
                    }
                    Toast.makeText(getAct(), "串口已打开", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭串口
                if(serialPortHelper!=null){
                    serialPortHelper.closeSerialPort();
                }

                String value = UploadData.getInstance().mWaste.mWeight;
                Toast.makeText(getAct(), "串口已关闭当前值："+value, Toast.LENGTH_SHORT).show();

                tvWeight.setText(value+"");
            }
        });

        root.findViewById(R.id.tvNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=tvWeight.getText().toString().trim();
                float mm = Float.parseFloat(text);

                if(UploadData.getInstance().mWaste.mCreateTime==0){
                    UploadData.getInstance().mWaste.mCreateTime = System.currentTimeMillis();
                }
                Log.i(TAG, "onClick: "+UploadData.getInstance().mWaste.mCreateTime);

                if(mm<0){
                    Toast.makeText(getContext(), "当前称重异常，请重新称重", Toast.LENGTH_SHORT).show();
                    return;
                }else if(mm>0){
                    gotoActGprint();
                }else{
                    Toast.makeText(getContext(), "请先称重", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        UploadData.getInstance().doWeight();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGetWeight = false;
        if (mListener != null) {
            mListener.onFragmentResume(this);
        }
        serialPortHelper.openSerialPort();
        Log.e("Fragment5", "onResume:"+this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mListener != null) {
            mListener.onFragmentPause(this);
        }
        serialPortHelper.closeSerialPort();
        Log.e("Fragment5", "onPause:"+this);
    }

    protected void nextStep() {
        try {
            if(getAct() == null) return;
            Navigation.findNavController(getAct(), R.id.nav_scan_host).navigate(R.id.nav_action_5to6);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onBtResultGet(float value) {
//        if(value <= 0 || getAct() == null) return;
        final float weight = Math.round((value - UploadData.getInstance().carWeigth()) * 100) / 100f;
        Log.i(TAG, "onBtResultGet: "+"carWeigth: "+UploadData.getInstance().carWeigth());
        final float zWeight = Math.round((value - UploadData.getInstance().totalWeight) * 100) / 100f;
        Log.i(TAG, "onBtResultGet: "+"totalWeight: "+UploadData.getInstance().totalWeight);

        Log.i(TAG, "onBtResultGet: "+"weight: "+weight+"--zWeight:"+zWeight);
        try {
            getAct().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        tvWeight.setText(weight+"");
                    }
                });
            mGetWeight = false;
            if(Math.abs(mLastWeight - weight) <= .3) {
                sameNume++;
            } else {
                sameNume = 1;
            }
            mLastWeight = weight;
            zeroWeight = zWeight;
            if(sameNume < 5) return;

            mGetWeight = true;
            mWeight = weight;
            onGetWeight(weight+"");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onGetWeight(String weight) {
        UploadData.getInstance().mWaste.mWeight = weight;//有效重量值
        UploadData.getInstance().mWaste.mCreateTime = System.currentTimeMillis();
        getAct().playSound("ding.wav");

    }

    protected void setZero() {
        if(mGetWeight) {
            UploadData.getInstance().zeroWeight = zeroWeight;
            getAct().showToast("置零成功");
        }
    }

    protected void gotoActGprint() {
        try {
            if(!mGetWeight) {
                getAct().showToast("请先获取重量并稳定2秒");
                return;
            }
            nextStep();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mListener!=null){
            mListener = null;
        }
    }
}
