package com.minorfish.car.twoth.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.aio.serial.SerialPortHelper;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.util.Utils;
import com.tangjd.common.utils.ByteUtil;
import com.tangjd.common.utils.Log;

/*
* 称重 校验砝码*/
public class ActCalibration extends XBaseActivity {

    private static final String TAG = ActCalibration.class.getSimpleName();
    TextView tvWeight;
    Button btJz02;
    Button btJz05;
    Button btJz1;
    Button btFm;
    Handler mHandler = new Handler();

    SerialPortHelper serialPortHelper = new SerialPortHelper(new SerialPortHelper.OnGetData() {
        @Override
        public void onDataReceived(byte[] bytes) {
            parseData(bytes);
        }
    });

    private void parseData(byte[] array) {
        try {
            if (array != null && array.length >= 8 && array[0] == 0x0A && array[1] == 0x0D && (array[2] == 0x2B || array[2] == 0x2D)) {
                Log.e("TTTTTT", ByteUtil.ByteArrayToHexString(array));
                String ffff = Utils.getChars(array).trim();
                int rstInt = Integer.parseInt(ffff);
                float result = ((float) rstInt) / 100f;
                if (array[2] == 0x2D) {
                    onBtResultGet(-result);
                } else {
                    onBtResultGet(result);
                }
            }
        } catch (Exception e) {
            int a = 0;
        }
    }


    private void onBtResultGet(final float weight) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                float mm = weight;

                float num=(float)Math.round(mm*100)/100;
                android.util.Log.i(TAG, "run: "+mm+"---"+num);
                tvWeight.setText("重量："+num+"kg");
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_calibration);

        setToolbarTitle("电子秤校准");
        enableBackFinish();

        tvWeight = findViewById(R.id.tvWeight);
        btJz02 = findViewById(R.id.btJz02);
        btJz05 = findViewById(R.id.btJz05);
        btJz1 = findViewById(R.id.btJz1);
        btFm = findViewById(R.id.btFm);
        tvWeight.setText("重量:");

        btJz02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEable(false);
                showProgressDialog("设置参数...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        serialPortHelper.openSerialPort();
                        serialPortHelper.jiaoZhun(3);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setEable(true);
                                showToast("请放置砝码");
                                dismissProgressDialog();
                            }
                        });
                    }
                }).start();
            }
        });

        btJz05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEable(false);
                showProgressDialog("设置参数...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        serialPortHelper.openSerialPort();
                        serialPortHelper.jiaoZhun(2);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setEable(true);
                                showToast("请放置砝码");
                                dismissProgressDialog();
                            }
                        });
                    }
                }).start();
            }
        });
        btJz1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEable(false);
                showProgressDialog("设置参数...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        serialPortHelper.openSerialPort();
                        serialPortHelper.jiaoZhun(1);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setEable(true);
                                showToast("请放置砝码");
                                dismissProgressDialog();
                            }
                        });
                    }
                }).start();
            }
        });
        btJz1.setVisibility(View.GONE);


        btFm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEable(false);
                showProgressDialog("砝码校准...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        serialPortHelper.putFaMa();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setEable(true);
                                showToast("校准结束");
                                dismissProgressDialog();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void setEable(boolean eable) {
        btJz05.setClickable(eable);
        btJz1.setClickable(eable);
        btFm.setClickable(eable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serialPortHelper != null) {
            serialPortHelper.closeSerialPort();
            serialPortHelper = null;
        }
    }
}
