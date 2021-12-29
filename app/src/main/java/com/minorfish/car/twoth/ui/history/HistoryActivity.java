package com.minorfish.car.twoth.ui.history;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.ui.stay.StayBean;
import com.tangjd.common.abs.JsonApiBase;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryActivity extends XBaseActivity implements View.OnClickListener, BaseQuickAdapter.RequestLoadMoreListener {

    @Bind(R.id.srl)
    SwipeRefreshLayout mSrl;

    private static final int size = 50;
    private int page = 0;
    private int inStatus = 0;

    @Bind(R.id.tvTime1)
    TextView tvTime1;
    @Bind(R.id.tvTime2)
    TextView tvTime2;
    @Bind(R.id.tv1)
    TextView tv1;
    @Bind(R.id.tv2)
    TextView tv2;
    @Bind(R.id.tv3)
    TextView tv3;
    @Bind(R.id.tv4)
    TextView tv4;

    @Bind(R.id.rvList)
    RvHistory rvList;

    private SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    private Date start;
    private Date end;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_history);
        ButterKnife.bind(this);
        tv4.setVisibility(View.GONE);

        enableBackFinish();

        setToolbarTitle("历史垃圾");

        initView();

        mSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataOrRefresh();
            }
        });

//      rvList.addOnItemTouchListener(new OnItemClickListener() {
//          @Override
//          public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
//          }
//      });
        rvList.setOnEmptyViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataOrRefresh();
            }
        });

        Calendar cur = Calendar.getInstance();
        end = new Date();
        cur.add(Calendar.MONTH, -1);
        start = cur.getTime();

        setDay();

        tvTime1.setOnClickListener(this);
        tvTime2.setOnClickListener(this);

        selectTab(0);
    }

    private void setDay() {


        tvTime1.setText(day.format(start));
        tvTime2.setText(day.format(end));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvTime1:
                timePicker(0, start);
                break;
            case R.id.tvTime2:
                timePicker(1, end);
                break;
            case R.id.tv1:
                selectTab(0);
                break;
            case R.id.tv2:
                selectTab(1);
                break;
            case R.id.tv3:
                selectTab(2);
                break;
            case R.id.tv4:
                selectTab(3);
                break;
        }
    }

    private void timePicker(final int at, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //时间选择器
        TimePickerView build = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if(at == 0) {
                    start.setTime(date.getTime());
                } else {
                    end.setTime(date.getTime());
                }

                setDay();
                getDataOrRefresh();
            }
        })
        .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
        .setCancelText("取消")//取消按钮文字
        .setSubmitText("确定")//确认按钮文字
        .setContentTextSize(22)
        .setTitleSize(24)//标题文字大小
        .setTitleText(at == 0 ? "请选择开始日期" : "请选择结束日期")//标题文字
        .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
        .isCyclic(true)//是否循环滚动
        .setTitleColor(Color.BLACK)//标题文字颜色
        .setSubmitColor(Color.BLUE)//确定按钮文字颜色
        .setCancelColor(Color.BLUE)//取消按钮文字颜色
        .setTitleBgColor(0xFF78C8F0)//标题背景颜色 Night mode
        .setBgColor(0xFFE6FAF9)//滚轮背景颜色 Night mode
        .setDate(calendar)// 如果不设置的话，默认是系统时间*/
//        .setRangDate(startDate,endDate)//起始终止年月日设定
        .setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
        .isDialog(false)//是否显示为对话框样式
        .build();

        build.show();
    }

    private void selectTab(int at) {
        inStatus = at;
        tv1.setSelected(at == 0);
        tv2.setSelected(at == 1);
        tv3.setSelected(at == 2);
        tv4.setSelected(at == 3);
        rvList.setData(null);
        getDataOrRefresh();
    }

    private void initView() {
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
    }

    private void getDataOrRefresh() {
        getStat();
        page = 1;
        rvList.showLoadingView();
        Api.getHistoryByPage(day.format(start),day.format(end), 30, page, inStatus, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if(result.isSuccess()) {
                    rvList.onGetDataSuccess(((JSONObject) result.mData).optBoolean("hasNextPage"), StayBean.parse(result.mData), HistoryActivity.this);
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String s) {
                rvList.onGetDataFail(s+"", true);
                showTipDialog(s);
            }

            @Override
            public void onFinish(boolean b) {
                mSrl.setRefreshing(false);
            }
        });
    }

    @Override
    public void onLoadMoreRequested() {

        mSrl.setEnabled(false);
        page++;
        Api.getHistoryByPage(day.format(start),day.format(end), 30, page, inStatus, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    rvList.onLoadMoreSuccess(((JSONObject) result.mData).optBoolean("hasNextPage"), StayBean.parse(result.mData));
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String error) {
                showShortToast(error + "");
                rvList.onLoadMoreFail();
            }

            @Override
            public void onFinish(boolean withoutException) {
                mSrl.setEnabled(true);
            }
        });
    }

    private void getStat() {
        Api.getHistoryStat(day.format(start),day.format(end), new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if(result.isSuccess()) {
                    JSONObject data = (JSONObject) result.mData;
                    if(data != null) {
                        tv1.setText("待入库（" + data.optString("uploadCount") + ")");
                        tv2.setText("已入库（" + data.optString("inCount") + ")");
                        tv3.setText("已出库（" + data.optString("outCount") + ")");
                        tv4.setText("异常入库（" + data.optString("errInCount") + ")");
                    }
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String s) {
                System.out.println(s);
            }

            @Override
            public void onFinish(boolean b) {

            }
        });
    }


}
