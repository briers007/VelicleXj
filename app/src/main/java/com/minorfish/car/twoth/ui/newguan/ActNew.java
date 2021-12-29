package com.minorfish.car.twoth.ui.newguan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.extend.ViewExt;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.ui.boxout.BagBean;
import com.minorfish.car.twoth.ui.boxout.BoxBean;
import com.minorfish.car.twoth.ui.boxout.RvOut;
import com.minorfish.car.twoth.ui.dialog.NfcScanDialog;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.DecimalUtil;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActNew extends XBaseActivity implements View.OnClickListener {

    @Bind((R.id.rvList))
    RvNew rvList;
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
        NfcScanDialog dialog = new NfcScanDialog(new NfcScanDialog.OnNfcResult() {
            @Override
            public void onReslut(String data) {
                bagOut(data);
            }
        }, R.drawable.ic_scan_nfcno2);
        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_new);

        ButterKnife.bind(this);
        setToolbarTitle("新冠出库");
        enableBackFinish();

        ViewExt.rippleOval(ivSubmit);
        ivSubmit.setOnClickListener(this);

        getData();
    }


    private void getData(){
        Api.getOutDetailNew(new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    BoxBean boxBean = BoxBean.objectFromData(result.mData + "");
                    setBoxData(boxBean);
                    List<NewBean> bagItemBeen = NewBean.arrayBagBeanFromData(((JSONObject) result.mData).optJSONArray("garbages").toString());
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

    private void setBagListData(List<NewBean> bagItemBeen) {
        rvList.setData(bagItemBeen);
    }

    private void setBoxData(BoxBean bean) {
        tvAgencyName.setText("回收站："+bean.instName);
        tvDuty.setText("负责人："+ App.getApp().getSignInBean().name);
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
        Api.bagOutNew(tagId, new JsonApiBase.OnJsonResponseListener() {
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
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_COMMON && resultCode == RESULT_OK) {
            finish();
        }
    }
}
