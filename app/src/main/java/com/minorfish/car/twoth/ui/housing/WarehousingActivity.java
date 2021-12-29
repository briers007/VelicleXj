package com.minorfish.car.twoth.ui.housing;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.aio.serial.SerialPortHelper;
import com.minorfish.car.twoth.extend.ViewExt;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.ui.dialog.NfcScanDialog;
import com.minorfish.car.twoth.ui.stay.StayBean;
import com.minorfish.car.twoth.ui.upload.ActUpload;
import com.minorfish.car.twoth.ui.upload.UploadData;
import com.minorfish.car.twoth.util.OkHttpUtil;
import com.minorfish.car.twoth.util.Utils;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.ByteUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class WarehousingActivity extends XBaseActivity implements View.OnClickListener {

    private static final String TAG = WarehousingActivity.class.getSimpleName();
    @Bind(R.id.ivSubmit)
    ImageView ivSubmit;
    @Bind(R.id.tvWeight1)
    TextView tvWeight1;
    @Bind(R.id.tvNum1)
    TextView tvNum1;
    @Bind(R.id.tvWeight2)
    TextView tvWeight2;
    @Bind(R.id.tvNum2)
    TextView tvNum2;
    @Bind(R.id.rvList)
    RvHouse rvList;
    private int unWeightCount = 0;

    private String updateWeight = "";
    private String mTotalWeight = "";

    private int sameNume = 0;
    private float mLastWeight = 0;
    private boolean mGetWeight = false;



    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x001:
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            Toast.makeText(WarehousingActivity.this, "入库成功!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }, 1500);
                    break;
                case 0x002:
                    dismissProgressDialog();
                    String error= (String) msg.obj;
                    Toast.makeText(WarehousingActivity.this, "入库失败"+error, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    private SerialPortHelper serialPortHelper = new SerialPortHelper(new SerialPortHelper.OnGetData() {
        @Override
        public void onDataReceived(byte[] bytes) {
            parseData(bytes);
        }
    });

    private void parseData(byte[] array) {
        try {
            if (array != null && array.length >= 8 && array[0] == 0x0A && array[1] == 0x0D && (array[2] == 0x2B || array[2] == 0x2D)) {
                com.tangjd.common.utils.Log.e(TAG, ByteUtil.ByteArrayToHexString(array));
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
            e.printStackTrace();
        }
    }

    private void onBtResultGet(float value) {
//        if(value <= 0) return;
        float num=(float)Math.round(value*100)/100;
        final float mWeight = num;

        try {
            if (mWeight > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvWeight2.setText(mWeight+"");
                    }
                });
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvWeight2.setText("0.0");
                    }
                });
            }
            mGetWeight = false;
            if(Math.abs(mLastWeight - mWeight) <= .3) {
                sameNume++;
            } else {
                sameNume = 1;
            }
            mLastWeight = mWeight;
            if(sameNume < 5) return;

            mGetWeight = true;
            onGetWeight(mWeight+"");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onGetWeight(String weight) {
        mTotalWeight = weight;
        playSound("ding.wav");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ware_housing);

        ButterKnife.bind(this);

        enableBackFinish();

        setToolbarTitle("垃圾入库");

        initView();

        getInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        serialPortHelper.openSerialPort();
    }

    @Override
    protected void onPause() {
        super.onPause();
        serialPortHelper.closeSerialPort();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSubmit: {
                // 未称重数据大于0提示
                if(unWeightCount > 0) {
                    showAlertDialog("车内有未称重垃圾，是否继续入库？", "继续", "取消", true, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showNfcDialog();
                        }
                    }, null);
                } else {
                    if (rvList.mAdapter.getData() == null || rvList.mAdapter.getData().size() == 0) {
                        showShortToast("没有可入库垃圾");
                        return;
                    }
                    showNfcDialog();
                }
                break;
            }
        }
    }

    private void initView() {
        ViewExt.rippleOval(ivSubmit);
        ivSubmit.setOnClickListener(this);
    }

    private void showNfcDialog() {
        NfcScanDialog dialog = new NfcScanDialog(new NfcScanDialog.OnNfcResult() {
            @Override
            public void onReslut(String data) {
                doSubmit(data);
            }
        }, R.drawable.ic_scan_nfcno);
        dialog.show(getSupportFragmentManager(), "nfcno");
    }

    private void getInfo() {
        showProgressDialog();
        Api.getBagInSummary(new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                String res = response.toString();
                Log.i(TAG, "onResponse: "+res);
                if(result.isSuccess()) {
                    JSONObject data = (JSONObject) result.mData;
                    tvWeight1.setText(""+data.optDouble("uploadWeight"));

                    updateWeight = data.optDouble("uploadWeight")+"";
                    mTotalWeight = ""+data.optDouble("uploadWeight");

                    tvNum1.setText(data.optString("uploadCount"));
//                    tvWeight2.setText(data.optString("unweightCount"));
                    unWeightCount = data.optInt("unweightCount");
                    tvNum2.setText(String.valueOf(unWeightCount));
                    rvList.setData(StayBean.parse(data));
                } else {

                }
            }

            @Override
            public void onError(String s) {
                showTipDialog(s);
            }

            @Override
            public void onFinish(boolean b) {
                dismissProgressDialog();
            }
        });
    }

    //垃圾入库
    private void doSubmit(String nfcno) {
        showProgressDialog(false);
        String url = Api.getDomainName() + "/hw/car/v2/bag/in";
        Log.i(TAG, "doSubmit: "+url);
        String exceptionMessage="";
        int status=1;
        if(updateWeight.equals(mTotalWeight)){
            exceptionMessage="";
            status=1;
        }else{
            exceptionMessage="上传车内重量不一致";
            status=2;
        }

        String finalWeight ="";
        float mm = Float.parseFloat(mTotalWeight);
        if(mm<=0){
            finalWeight = "0.0";
        }else{
            finalWeight = mTotalWeight;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("exceptionMessage", exceptionMessage);
            params.put("nfccode", nfcno);
            params.put("status", status);
            params.put("weight", finalWeight);

            Log.i(TAG, "doSubmit: "+App.getApp().getSignInBean().token);
            OkHttpUtil.getInstance().postJsonAsyn(url, params.toString(), App.getApp().getSignInBean().token, new OkHttpUtil.NetCall() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    String str = response.body().string();
                    Log.i(TAG, "success: "+str);
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        int mCode = jsonObject.optInt("code");
                        if(mCode==200){
                            mHandler.sendEmptyMessage(0x001);
                        }else{
                            String msg=jsonObject.optString("message");
                            Message message=Message.obtain();
                            message.what=0x002;
                            message.obj=msg;
                            mHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(Call call, IOException e) {
                    Log.i(TAG, "failed: "+e.getMessage());
//                    showLongToast("请求失败");
                    String errorStr=e.getMessage();
                    Message message=Message.obtain();
                    message.what=0x002;
                    message.obj=errorStr;
                    mHandler.sendMessage(message);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }



        /*Api.doBagIn(updateWeight.equals(mTotalWeight) ? "" : "上传车内重量不一致", nfcno, updateWeight.equals(mTotalWeight) ? 1 : 2, mTotalWeight, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Result result = Result.parse(jsonObject);
                String str=new Gson().toJson(jsonObject);
                Log.i(TAG, "onResponse: "+str);
                if(result.isSuccess()){
                    Toast.makeText(WarehousingActivity.this, "交接入库成功", Toast.LENGTH_LONG).show();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1500);
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String s) {
                showTipDialog(s);
            }

            @Override
            public void onFinish(boolean b) {
                dismissProgressDialog();
            }
        });*/
    }

}
