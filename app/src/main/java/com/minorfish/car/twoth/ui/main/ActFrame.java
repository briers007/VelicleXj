package com.minorfish.car.twoth.ui.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.extend.ViewExt;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.ui.boxout.ActBoxOut;
import com.minorfish.car.twoth.ui.history.HistoryActivity;
import com.minorfish.car.twoth.ui.housing.WarehousingActivity;
import com.minorfish.car.twoth.ui.newguan.ActNew;
import com.minorfish.car.twoth.ui.sign.ActSignIn;
import com.minorfish.car.twoth.ui.sign.SignInBean;
import com.minorfish.car.twoth.ui.upload.ActScanQr;
import com.minorfish.car.twoth.ui.upload.UploadData;
import com.minorfish.car.twoth.ui.upload.WardModel;
import com.minorfish.car.twoth.usb.PrinterHelperSerial;
import com.minorfish.car.twoth.usb.UsbService;
import com.minorfish.car.twoth.util.PreferenceKit;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.Log;
import com.tangjd.common.utils.StringKit;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActFrame extends XBaseActivity implements View.OnClickListener {

    private static final String TAG = ActFrame.class.getSimpleName();
    @Bind(R.id.tvUser)
    TextView tvUser;
    @Bind(R.id.tvTitle)
    TextView tvTitle;

    @Bind(R.id.etMM)
    EditText et;

    @Bind(R.id.cbxSound)
    CheckBox cbxSound;

    @Bind(R.id.iv1)
    ImageView iv1;
    @Bind(R.id.iv2)
    ImageView iv2;
    @Bind(R.id.iv3)
    ImageView iv3;
    @Bind(R.id.iv4)
    ImageView iv4;
    @Bind(R.id.iv5)
    ImageView iv5;
    @Bind(R.id.iv6)
    ImageView iv6;

    @Bind(R.id.vMain)
    View vMain;
    @Bind(R.id.vSelect)
    View vSelect;
    @Bind(R.id.btnOk)
    Button btnOk;
    @Bind(R.id.rvList)
    RvCar rvList;
    @Bind(R.id.fragment4)
    FrameLayout fragment4;
    @Bind(R.id.btnCancle2)
    Button btnCancle2;

    private long exitTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_frame);
        ButterKnife.bind(this);
        try {

            ImmersionBar.with(this)
                    .statusBarDarkFont(false)//设置状态栏图片为深色，(如果android 6.0以下就是半透明)
                    .transparentStatusBar()// 透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()
                    .init();

            ButterKnife.bind(this);

            initView();
            //跳转到砝码页面
            tvUser.setOnClickListener(view -> startAct(this, ActCalibration.class));
            //声音按钮开关
            if (PreferenceKit.getBoolean(this, "sound_open", true)) {
                cbxSound.setChecked(true);
            } else {
                cbxSound.setChecked(false);
            }

            cbxSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    PreferenceKit.putBoolean(ActFrame.this, "sound_open", b);
                }
            });

            et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String txt = et.getText().toString().trim();
                    Log.w(TAG, "actionId:" + actionId + ",txt=" + txt);
                    if (actionId == KeyEvent.ACTION_DOWN && !TextUtils.isEmpty(txt)) {
                        try {
                            getWardName(txt);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            et.setText("");
                        }
                        return false;
                    }
                    return false;
                }
            });

            onUsbCreate();//开启USB服务

            PrinterHelperSerial.getInstance(this).connect();//连接串口打印机打印

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private void setBind(String id, boolean refresh) {
        if(id != null && id.trim().length() > 0 && !"null".equalsIgnoreCase(id) && !"0".equalsIgnoreCase(id.trim())) {
            PreferenceKit.putString(this, "bind_car_id", id.trim());
            vMain.setVisibility(View.VISIBLE);
            vSelect.setVisibility(View.GONE);
            rvList.setData(null);
            et.setEnabled(true);
            et.requestFocus();
            et.setText("");
        } else {
            vMain.setVisibility(View.GONE);
            vSelect.setVisibility(View.VISIBLE);
            et.setEnabled(false);
            if(refresh) {
                getBindList();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 上传垃圾
            case R.id.iv1:
                String str = App.getApp().getWaitData();
                android.util.Log.i(TAG, "onItemClick: "+str);
                if(TextUtils.isEmpty(str)){
                    ActScanQr.startActivity(this, 0);
                }else{
                    WaitUploadData.startActivity(this);
                }
//                        clearSp();

                break;
            // 垃圾入库
            case R.id.iv2:
                startAct(this, WarehousingActivity.class);
                break;
            //历史垃圾
            case R.id.iv3:
                startAct(this, HistoryActivity.class);
//                startAct(this, TestActivity.class);
//                startAct(this, ActSignIn.class);
                break;
            // 新冠出库
            case R.id.iv4:
//                startAct(this, WeightUploadActivity.class);
                startAct(this, ActNew.class);
                break;
            // 垃圾出库
            case R.id.iv5:
                startAct(this, ActBoxOut.class);
//                startAct(this, StayWasteListActivity.class);
                break;
            // 退出登录
            case R.id.iv6:
                App.getApp().signOut();
                App.sharedUtility.loginOut();
                ActSignIn.startActivity(ActFrame.this);
                break;
            case R.id.btnCancle2:
                App.getApp().signOut();
                ActSignIn.startActivity(ActFrame.this);
                break;
            case R.id.btnOk: {
                if (rvList.selects.isEmpty()) {
                    Toast.makeText(this, "请选择对应车辆", Toast.LENGTH_SHORT).show();
                    return;
                }
                String id = rvList.selects.get(0).id;

                if (id == null || id.trim().length() == 0 || "null".equalsIgnoreCase(id) || "0".equalsIgnoreCase(id.trim())) {

                    Toast.makeText(this, "错误的id", Toast.LENGTH_SHORT).show();
                    return;
                }
                onCarSelect(rvList.selects.get(0).id);
            }
            break;
        }
    }

    private void  initView() {
        ViewExt.rippleOval(iv1);
        ViewExt.rippleOval(iv2);
        ViewExt.rippleOval(iv3);
        ViewExt.rippleOval(iv4);
        ViewExt.rippleOval(iv5);
        ViewExt.rippleOval(iv6);

        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);
        iv3.setOnClickListener(this);
        iv4.setOnClickListener(this);
        iv5.setOnClickListener(this);
        iv6.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnCancle2.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            onUsbDestroy();

            App.getApp().setLastWeight("");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (App.getApp().getSignInBean() != null) {
//                int code=App.getApp().getSignInBean().specialType;
                int code = App.sharedUtility.getTypeCode();
                Log.i(TAG,"initView: " + code);

                if (code == 0) {
                    //0无新冠 1有新冠
                    fragment4.setVisibility(View.GONE);
                }
                if (code == 1) {
                    fragment4.setVisibility(View.VISIBLE);
                }

                if (!TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
                    tvUser.setText(App.getApp().getSignInBean().name + "");
                    tvTitle.setText(App.getApp().getSignInBean().instName);
                }
                if (App.getApp().getSignInBean().bindCar == 0) {
                    PreferenceKit.putString(this, "bind_car_id", "");
                } else {
                    PreferenceKit.putString(this, "bind_car_id", "1");
                }
                setBind(PreferenceKit.getString(this, "bind_car_id", ""), true);
//                setBind("1",false);  //测试取消绑车
            } else {
                ActSignIn.startActivity(ActFrame.this);
            }
            if (vMain.getVisibility() == View.VISIBLE) {
                et.setEnabled(true);
                et.requestFocus();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        et.clearFocus();
        et.setEnabled(false);
    }

    private void onUsbCreate() {
        try {
            setFilters();  // Start listening notifications from UsbService
            startService(usbConnection); // Start UsbService(if it was not started before) and Bind it
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void onUsbDestroy() {
        try {
            unregisterReceiver(mUsbReceiver);
            unbindService(usbConnection);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getAction() == null) return;
                switch (intent.getAction()) {
                    case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                        Log.w(TAG, "USB Ready " + device.toString());
                        break;
                    case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                        Log.w(TAG, "USB Permission not granted" + device.toString());
                        break;
                    case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                        Log.w(TAG, "USB disconnected" + device.toString());
                        break;
                    case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                        Log.w(TAG, "USB device not supported" + device.toString());
                        break;
                    case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                        Log.w(TAG, "No USB connected");
                        break;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    };
    //    private UsbService usbService;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
//            usbService = ((UsbService.UsbBinder) arg1).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
//            usbService = null;
        }
    };


    private void startService(ServiceConnection serviceConnection) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, UsbService.class);
//            if (null != null && !((Bundle) null).isEmpty()) {
//                Set<String> keys = ((Bundle) null).keySet();
//                for (String key : keys) {
//                    String extra = ((Bundle) null).getString(key);
//                    startService.putExtra(key, extra);
//                }
//            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, UsbService.class);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    public void getWardName(String data) {
        try {
            JSONObject obj = new JSONObject(data);
            int tagId = obj.optInt("id");

            final int type = 2;
            showProgressDialog();
            Api.getWardNameById(tagId + "", new JsonApiBase.OnJsonResponseListener() {
                @Override
                public void onResponse(JSONObject data) {
                    Result result = Result.parse(data);
                    if (result.isSuccess()) {
                        JSONObject resp = (JSONObject) result.mData;
                        UploadData.getInstance().clear();
                        WardModel bean = UploadData.getInstance().keshi;
                        if (resp != null && bean != null) {
                            String name = resp.optString("name");
                            if (StringKit.isNotEmpty(name)) {
                                if (type == 1) {//nfc扫码
//                                bean.mId = null;
//                                bean.nfcCode = tagId+"";
                                } else {
                                    bean.wardId = tagId + "";
                                    bean.hospitalName = resp.optString("instName");
                                }
                                bean.keShiName = name;
                                ActScanQr.startActivity(ActFrame.this, 1);
                            } else {
                                String msg = resp.optString("message");
                                if (StringKit.isNotEmpty(msg)) {
                                    onError(msg);
                                } else {
                                    onError("此卡尚未绑定科室");
                                }
                            }
                        } else {
                            onError("连接失败");
                        }
                    } else {
                        onError(result.mMsg);
                    }
                }

                @Override
                public void onError(String error) {
                    showTipDialog(error);
                }

                @Override
                public void onFinish(boolean withoutException) {
                    dismissProgressDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void getBindList() {
        try {
            showProgressDialog();
            Api.bindList(new JsonApiBase.OnJsonResponseListener() {
                @Override
                public void onResponse(JSONObject data) {
                    Result result = Result.parse(data);
                    if (result.isSuccess() && result.mData != null) {
//                        String json = "[{\"id\":\"0\",\"name\":\"test\"},{\"id\":\"1\",\"name\":\"test2\"}]";
//                        List<CarBean> list = CarBean.parse(json);
                        List<CarBean> list = CarBean.parse(result.mData.toString());
                        if (list != null && !list.isEmpty()) {
                            rvList.selects.add(list.get(0));
                            rvList.setData(list);
                        }
                    } else {
                        onError(result.mMsg);
                    }
                }

                @Override
                public void onError(String error) {
                    showTipDialog(error);
                }

                @Override
                public void onFinish(boolean withoutException) {
                    dismissProgressDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void onCarSelect(final String id) {
        try {
            showProgressDialog();
            Api.bindCar(id, new JsonApiBase.OnJsonResponseListener() {
                @Override
                public void onResponse(JSONObject data) {
                    Result result = Result.parse(data);
                    if (result.isSuccess()) {
                        SignInBean bean = App.getApp().getSignInBean();
                        if (bean != null) {
                            bean.bindCar = 1;
                            App.getApp().setSignInBean(new Gson().toJson(bean), bean);
                        }
                        setBind(id, false);
                    } else {
                        onError(result.mMsg);
                    }
                }

                @Override
                public void onError(String error) {
                    showTipDialog(error);
                }

                @Override
                public void onFinish(boolean withoutException) {
                    dismissProgressDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(),
                    "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }*/
}
