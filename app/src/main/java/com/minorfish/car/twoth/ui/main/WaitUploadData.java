package com.minorfish.car.twoth.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.ui.upload.UploadData;
import com.minorfish.car.twoth.util.LogUtils;
import com.minorfish.car.twoth.util.NetUtil;
import com.minorfish.car.twoth.util.OkHttpUtil;

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

public class WaitUploadData extends XBaseActivity {
    private static final String TAG = WaitUploadData.class.getSimpleName();
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar_left_menu)
    TextView toolbarLeftMenu;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.list_view)
    ListView listView;
    @Bind(R.id.iv_upload)
    TextView ivUpload;

    List<WardsBean.Trashe> list;
    @Bind(R.id.tv_ward)
    TextView tvWard;
    @Bind(R.id.tv_count_num2)
    TextView tvCountNum2;
    private WaitDataAdapter adapter;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x001:
                    //清除系统设置
                    tvCountNum2.setText(" 合计：" + "0包");
                    if (list != null && list.size() > 0) {
                        list.clear();
                        adapter.notifyDataSetChanged();
                    }
                    App.getApp().clearSp();
                    UploadData.getInstance().clear();
                    Toast.makeText(WaitUploadData.this, "上传成功!", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            finish();
                        }
                    }, 1500);
                    break;
                case 0x002:
                    dismissProgressDialog();
                    String errorStr = (String) msg.obj;
                    Log.i(TAG, "handleMessage: " + errorStr);
                    LogUtils.i("上传失败", errorStr);
                    Toast.makeText(WaitUploadData.this, "上传失败" + errorStr, Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
                    dismissProgressDialog();
                    String errorStr2 = (String) msg.obj;
                    LogUtils.e("请求失败", errorStr2);
                    Toast.makeText(WaitUploadData.this, "请求失败" + errorStr2, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_upload_data);
        ButterKnife.bind(this);

        setToolbarTitle("垃圾暂存列表");
        enableBackFinish();

        list = new ArrayList<WardsBean.Trashe>();

        String mJson = App.getApp().getWaitData();
        Log.i(TAG, "onCreate: " + mJson);
        if (TextUtils.isEmpty(mJson)) {
            return;
        } else {
            try {
//                Toast.makeText(this, "您还有待上传的垃圾未上传，请完成此操作", Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = new JSONObject(mJson);
                JSONArray jsonArray = jsonObject.optJSONArray("trashes");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        WardsBean.Trashe trashe = new WardsBean.Trashe();
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        trashe.setTrashNo(jsonObject1.optString("trashNo"));
                        trashe.setWeight(jsonObject1.optString("weight"));
                        trashe.setTrashTypeCode(jsonObject1.optString("trashTypeCode"));
                        trashe.setCount(jsonObject1.optInt("count"));
                        trashe.setPlacenta(jsonObject1.optInt("placenta"));
                        trashe.setSpecialType(jsonObject1.optInt("specialType"));
                        trashe.setmCreateTime(jsonObject1.optLong("createTime"));

                        list.add(trashe);
                    }
                } else {
                    Toast.makeText(this, "暂无数据", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            adapter = new WaitDataAdapter(this, list);
            listView.setAdapter(adapter);

            String mName = App.getApp().getWardName();
            Log.i(TAG, "onCreate: " + mName);

            tvWard.setText("科室：" + mName);
            if (list != null && list.size() > 0) {
                tvCountNum2.setText(" 合计：" + list.size() + "包");
            }

        }

        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //提交数据
                showAlertDialog("是否确定上传暂存垃圾数据？", "确认", "取消", true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadWait();
                    }
                }, null);
            }
        });

    }

    //上传垃圾数据
    private void uploadWait() {
        if (NetUtil.isNetworkAvailable(this)) {
            showProgressDialog(false);
            String url = Api.getDomainName() + "/hw/car/v2/bag/create";
            Log.i(TAG, "processUpload: " + url);

            String token = App.getApp().getSignInBean().token;

            String pamarStr = App.getApp().getWaitData();
            Log.i(TAG, "uploadWait: " + pamarStr);

            try {
                OkHttpUtil.getInstance().postJsonAsyn(url, pamarStr, token, new OkHttpUtil.NetCall() {
                    @Override
                    public void success(Call call, Response response) throws IOException {
                        String str = response.body().string();
                        Log.i(TAG, "success: " + str);
                        LogUtils.i("服务器返回响应数据", str);
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            int mCode = jsonObject.getInt("code");
                            Log.i(TAG, "success: " + mCode);
                            if (mCode == 200) {
                                LogUtils.i("请求成功", mCode + "");
                                mHandler.sendEmptyMessage(0x001);
                            } else {
                                String error = jsonObject.optString("message");
                                LogUtils.i("请求失败", "code非200--" + error);
                                Message message = Message.obtain();
                                message.what = 0x002;
                                message.obj = error;
                                mHandler.sendMessage(message);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failed(Call call, IOException e) {
                        LogUtils.i("请求失败", e.getMessage());
                        Message message = new Message();
                        message.what = 0x003;
                        message.obj = e.getMessage();
                        mHandler.sendMessage(message);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, WaitUploadData.class));
    }
}
