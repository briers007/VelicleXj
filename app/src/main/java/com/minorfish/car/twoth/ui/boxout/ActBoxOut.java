package com.minorfish.car.twoth.ui.boxout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.extend.ViewExt;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.ui.dialog.ChoiseTypeDialog;
import com.minorfish.car.twoth.ui.dialog.NfcScanDialog;
import com.minorfish.car.twoth.util.LogUtils;
import com.minorfish.car.twoth.util.OkHttpUtil;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.DecimalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class ActBoxOut extends XBaseActivity implements View.OnClickListener {

    private static final String TAG = ActBoxOut.class.getSimpleName();
    @Bind((R.id.rvList))
    RvOut rvList;
    @Bind(R.id.tvDuty)
    TextView tvDuty;
    @Bind(R.id.tvAgencyName)
    TextView tvAgencyName;
    @Bind(R.id.tvGanran)
    TextView tvGanran;
    @Bind(R.id.tvSunshang)
    TextView tvSunshang;
    @Bind(R.id.tvBingli)
    TextView tvBingli;
    @Bind(R.id.tvYaowu)
    TextView tvYaowu;
    @Bind(R.id.tvHuaxue)
    TextView tvHuaxue;
    @Bind(R.id.tvSuliaoping)
    TextView tvSuliaoping;
    @Bind(R.id.tvBoliping)
    TextView tvBoliping;
    @Bind(R.id.tvTotal)
    TextView tvTotal;
    @Bind(R.id.ivSubmit)
    ImageView ivSubmit;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x001:
                    dismissProgressDialog();
                    Toast.makeText(ActBoxOut.this, "出库成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 0x002:
                    dismissProgressDialog();
                    String faildStr = (String) msg.obj;
                    LogUtils.i("出库失败",faildStr);
                    Toast.makeText(ActBoxOut.this, "出库失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
                    dismissProgressDialog();
                    Toast.makeText(ActBoxOut.this, "请求失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSubmit: {
                if (rvList.mAdapter.getData() == null || rvList.mAdapter.getData().size() == 0) {
                    showShortToast("没有可出库垃圾");
                    return;
                }
                showNfcDialog();

                try{
                    playSound("gongpai.wav");
                }catch (Exception e){}
                break;
            }
        }
    }
    private void showNfcDialog() {
        NfcScanDialog dialog = new NfcScanDialog(data -> bagOut(data), R.drawable.ic_scan_nfcno2);
        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_box_out);

        ButterKnife.bind(this);
        setToolbarTitle("垃圾出库");
        enableBackFinish();

        ViewExt.rippleOval(ivSubmit);
        ivSubmit.setOnClickListener(this);

        getData();
    }

    private void showChoiceDialog(ArrayList<WasteTypeBean> list) {
        ChoiseTypeDialog dialog = ChoiseTypeDialog.showDialog(getSupportFragmentManager(), list);
        dialog.mOnChoiceLister = new ChoiseTypeDialog.OnChoiceLister() {
            @Override
            public void onChoick(List<WasteTypeBean> list) {
                multiChoiceDone(list);
            }
        };
    }

    private void getData() {
        Api.getWasteType(new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    ArrayList<WasteTypeBean> list = WasteTypeBean.parse(result.mData);
                    if(list != null && !list.isEmpty()) {
                        showChoiceDialog(list);
                    }
                } else {
                    onError(result.mMsg + " " + result.mCode);
                }
            }

            @Override
            public void onError(String error) {
            }

            @Override
            public void onFinish(boolean withoutException) {
            }
        });
    }

    JSONArray checkedTypeCodes = new  JSONArray();
    private void multiChoiceDone(List<WasteTypeBean> list){
        if (list == null || list.size() == 0) {
            showLongToast("请选择要出库的类型");
            finish();
            return;
        }
        checkedTypeCodes = new  JSONArray();
        for(WasteTypeBean b : list) {
            checkedTypeCodes.put(b.mCode);
        }
        Api.getOutDetail(0, "", 0, checkedTypeCodes, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    BoxBean boxBean = BoxBean.objectFromData(result.mData + "");
                    setBoxData(boxBean);
                    List<BagBean> bagItemBeen = BagBean.arrayBagBeanFromData(((JSONObject) result.mData).optJSONArray("garbages").toString());
                    setBagListData(bagItemBeen);
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String error) {
                showShortToast(error + "");
                finish();
            }

            @Override
            public void onFinish(boolean withoutException) {
                dismissProgressDialog();
            }
        });
    }

    private void setBagListData(List<BagBean> bagItemBeen) {
        rvList.setData(bagItemBeen);
    }

    private void setBoxData(BoxBean bean) {
        tvAgencyName.setText("回收站："+bean.instName);
        tvDuty.setText("负责人："+App.getApp().getSignInBean().name);
        tvGanran.setText("感染类："+bean.type1Num + "袋/" + DecimalUtil.simpleFormat(bean.type1Weight) + "Kg");
        tvSunshang.setText("损伤类："+bean.type2Num + "袋/" + DecimalUtil.simpleFormat(bean.type2Weight) + "Kg");
        tvBingli.setText("病理类："+bean.type3Num + "袋/" + DecimalUtil.simpleFormat(bean.type3Weight) + "Kg");
        tvYaowu.setText("药物类："+bean.type4Num + "袋/" + DecimalUtil.simpleFormat(bean.type4Weight) + "Kg");
        tvHuaxue.setText("化学类："+bean.type5Num + "袋/" + DecimalUtil.simpleFormat(bean.type5Weight) + "Kg");
        tvSuliaoping.setText("盐水类："+bean.type6Num + "袋/" + DecimalUtil.simpleFormat(bean.type6Weight) + "Kg");
        tvBoliping.setText("玻璃瓶："+bean.type7Num + "袋/" + DecimalUtil.simpleFormat(bean.type7Weight) + "Kg");
        tvTotal.setText("总  计："+bean.totalNum + "袋/" + DecimalUtil.simpleFormat(bean.totalWeight) + "Kg");

        tvGanran.setVisibility(bean.type1Show ? View.VISIBLE : View.GONE);
        tvSunshang.setVisibility(bean.type2Show ? View.VISIBLE : View.GONE);
        tvBingli.setVisibility(bean.type3Show ? View.VISIBLE : View.GONE);
        tvYaowu.setVisibility(bean.type4Show ? View.VISIBLE : View.GONE);
        tvHuaxue.setVisibility(bean.type5Show ? View.VISIBLE : View.GONE);
        tvSuliaoping.setVisibility(bean.type6Show ? View.VISIBLE : View.GONE);
        tvBoliping.setVisibility(bean.type7Show ? View.VISIBLE : View.GONE);

//        llGanran.setVisibility(bean.type1Num!=0 ? View.VISIBLE : View.GONE);
//        llSunshang.setVisibility(bean.type2Num!=0 ? View.VISIBLE : View.GONE);
//        llBingli.setVisibility(bean.type3Num!=0 ? View.VISIBLE : View.GONE);
//        llYaowu.setVisibility(bean.type4Num!=0 ? View.VISIBLE : View.GONE);
//        llHuaxue.setVisibility(bean.type5Num!=0 ? View.VISIBLE : View.GONE);
//        llSuliaoping.setVisibility(bean.type6Num!=0 ? View.VISIBLE : View.GONE);
//        llBoliping.setVisibility(bean.type7Num!=0 ? View.VISIBLE : View.GONE);
    }

    private void bagOut(String tagId){
        showProgressDialog();

        String url = Api.getDomainName() + "/hw/car/v2/bag/out";
        String token = App.getApp().getSignInBean().token;

        Log.i(TAG, "bagOut: "+url+"----"+token);
        JSONObject params = new JSONObject();
        try {
            params.put("nfcCode", tagId);
            params.put("specialType", 0);
            params.put("typeCodes", checkedTypeCodes);

            String jsonStr = params.toString();
            Log.i(TAG, "bagOut: "+jsonStr);
            LogUtils.i("垃圾出库上传参数",jsonStr+"---url---"+url+"--token---"+token);

            OkHttpUtil.getInstance().postJsonAsyn(url, jsonStr, token, new OkHttpUtil.NetCall() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    String res =response.body().string();
                    Log.i(TAG, "success: "+res);
                    LogUtils.i("垃圾出库服务器返回数据",res);

                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        int mCode = jsonObject.optInt("code");
                        if(mCode==200){
                            mHandler.sendEmptyMessage(0x001);
                        }else{
                            String faild = jsonObject.optString("message");
                            Log.i(TAG, "success: "+faild);
                            Message message = Message.obtain();
                            message.what = 0x002;
                            message.obj = faild;
                            mHandler.sendMessage(message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(Call call, IOException e) {
                    mHandler.sendEmptyMessage(0x003);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*showProgressDialog();
        Api.bagOut(0, tagId, 0, checkedTypeCodes, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    showLongToast("出库成功");
                    finish();
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String error) {
                showTipDialog(error + "");
            }

            @Override
            public void onFinish(boolean withoutException) {
                dismissProgressDialog();
            }
        });*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_COMMON && resultCode == RESULT_OK) {
            finish();
        }
    }
}
