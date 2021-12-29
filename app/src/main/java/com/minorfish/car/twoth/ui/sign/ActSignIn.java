package com.minorfish.car.twoth.ui.sign;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.usb.NfcHelper;
import com.minorfish.car.twoth.usb.UsbService;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.ByteUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

/**
 * @author honghui @time: 2020/4/25 16:27
 * class:{@link ActSignIn}
 */
public class ActSignIn extends XBaseActivity {

    private static final String TAG = ActSignIn.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_sign_in_layout);
        initView();
        //onUsbCreate();

        NfcHelper.getInstance().setOnUsbReadCallback(new UsbService.OnUsbReadCallback() {
            @Override
            public void onUsbDataReceived(byte[] data) {
                Log.e(TAG, ByteUtil.bytesToHexStringWithoutSpace(data));
                String tagId = ByteUtil.bytesToHexStringWithoutSpace(ByteUtil.subByteArr(data, 0, 4));//截取10进制的卡号
                processLogin(tagId);
//                processLogin("E2D95EA6");
            }

            @Override
            public void onError(String error) {
//                showTipDialog(error);
            }
        });
        getSanzeData();
    }

    public void processLogin(final String tagId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressDialog();
                Api.login(tagId, new JsonApiBase.OnJsonResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Result result = Result.parse(response);
                        String str=new Gson().toJson(response);
                        Log.i(TAG, "onResponse: "+str);
                        if (result.isSuccess()) {
                            final SignInBean bean = SignInBean.objectFromData(result.mData + "");
                            if (bean == null) {
                                onError("登录异常，请重试");
                            } else {
                                dismissProgressDialog();
                                showShortToast("登录成功");
                                App.getApp().setSignInBean(result.mData + "", bean);
                                App.getApp().mToken = bean.token;
                                int specialTypes=App.getApp().getSignInBean().specialType;
                                Log.i(TAG, "onResponse: "+specialTypes);

                                int type=bean.specialType;
                                App.sharedUtility.setTypeCode(type);
                                finish();
                            }
                        } else {
                            onError(result.mMsg);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        dismissProgressDialog();
                        showTipDialog(error + "");
                    }

                    @Override
                    public void onFinish(boolean withoutException) {
                    }
                });
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        if (App.getApp().getSignInBean() != null && !TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
//            ActFrame.startActivity(ActSignIn.this);
//            finish();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
//        if (App.getApp().getSignInBean() != null && !TextUtils.isEmpty(App.getApp().getSignInBean().token)) {
//            ActFrame.startActivity(ActSignIn.this);
//            finish();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {
        setToolbarTitle("登录");
        findViewById(R.id.toolbar).setBackgroundColor(Color.TRANSPARENT);

        ImageView ivAnim1 = (ImageView) findViewById(R.id.iv_anim_1);
        ImageView ivAnim2 = (ImageView) findViewById(R.id.iv_anim_2);
        final ImageView ivAnim3 = (ImageView) findViewById(R.id.iv_anim_3);

        // rotate animation of ivAnim1
        RotateAnimation rotateAnimation = new RotateAnimation(0, 359, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        // 单次动画时间
        rotateAnimation.setDuration(6000);
        // 无限循环
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        // 设置不停顿
        rotateAnimation.setInterpolator(new LinearInterpolator());
        ivAnim1.setAnimation(rotateAnimation);
        rotateAnimation.startNow();

        // scale animation of ivAnim2 and ivAnim3
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 2.6f, 1f, 2.6f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2000);
        scaleAnimation.setRepeatCount(ScaleAnimation.INFINITE);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        ivAnim2.setAnimation(scaleAnimation);
        scaleAnimation.startNow();

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //onUsbDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(getDataRunnable);
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void getSanzeData() {
        mHandler.removeCallbacks(getDataRunnable);
        mHandler.postDelayed(getDataRunnable, 600);
    }

    private Handler mHandler = new Handler();
    private Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            NfcHelper.getInstance().getTagId();
            mHandler.postDelayed(this, 600);
        }
    };


    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ActSignIn.class));
    }
}
