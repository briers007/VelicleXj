package com.minorfish.car.twoth.abs;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Spannable;
import android.text.TextUtils;

import com.minorfish.car.twoth.ui.sign.SignInBean;
import com.minorfish.car.twoth.util.CrashHandler;
import com.minorfish.car.twoth.util.SharedUtility;
import com.tangjd.common.manager.SPManager;
import com.tangjd.common.manager.VolleyManager;
import com.tangjd.common.utils.Log;
import com.umeng.commonsdk.UMConfigure;

/**
 * Created by Administrator on 2016/7/2.
 */
public class App extends Application {
    private static App sApp;

    public Handler mBackHandler;
    public HandlerThread mHandlerThread;
    public String mToken;
    public SignInBean mUserBean;
    public static SharedUtility sharedUtility;

    public synchronized static App getApp() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        CrashHandler.getInstance().init(getApplicationContext());
        Log.setLoggable(Configs.LOGABLE);
        SPManager.getInstance().init(this);
        VolleyManager.getInstance().init(this);

        mHandlerThread = new HandlerThread("AppBackHandlerThread");
        mHandlerThread.start();
        mBackHandler = new Handler(mHandlerThread.getLooper());
        sharedUtility = getSharedUtility(this);

        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "5b96053b8f4a9d29ab000401");
    }

    public void signOut() {
        SPManager.getInstance().putString(Constants.PREF_KEY_SIGN_IN_BEAN, null);
        mUserBean = null;
        mToken = null;
        App.getApp().setLastWeight("");
    }

    public static SharedUtility getSharedUtility(Context ctx) {
        SharedUtility sharedUittly = new SharedUtility();
        sharedUittly.pref = ctx.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        sharedUittly.editor = sharedUittly.pref.edit();
        return sharedUittly;
    }

    public void setSignInBean(String data, SignInBean bean) {
        SPManager.getInstance().putString(Constants.PREF_KEY_SIGN_IN_BEAN, data);
        mUserBean = bean;
    }

    public SignInBean getSignInBean() {
        if (mUserBean == null) {
            String data = SPManager.getInstance().getString(Constants.PREF_KEY_SIGN_IN_BEAN, null);
            mUserBean = SignInBean.objectFromData(data);
            if (mUserBean == null) {
                return null;
            }
            if (TextUtils.isEmpty(mUserBean.token)) {
                return null;
            }
            mToken = mUserBean.token;
        }
        return mUserBean;
    }

  /*  //本次新的重量
    private String curSumWeight;

    public String getSumWeight() {
        curSumWeight = SPManager.getInstance().getString(Constants.EXTRA_CURSUM_WEIGHT,"");
        return curSumWeight;
    }

    public void setSumWeight(String weight) {
        this.curSumWeight = weight;
        SPManager.getInstance().putString(Constants.EXTRA_CURSUM_WEIGHT,weight);
    }*/


    //上一次的重量
    private String lastCurWeight;

    public String getLastWeight() {
        lastCurWeight = SPManager.getInstance().getString(Constants.EXTRA_LAST_WEIGHT,"");
        return lastCurWeight;
    }

    public void setLastWeight(String weight) {
        this.lastCurWeight = weight;
        SPManager.getInstance().putString(Constants.EXTRA_LAST_WEIGHT,weight);
    }

    //默认串口未开启
    private boolean openPort;

    public boolean getOpenPort() {
        openPort = SPManager.getInstance().getBoolean(Constants.EXTRA_OPEN_PORT,false);
        return openPort;
    }

    public void setOpenPort(boolean openPort) {
        this.openPort = openPort;
        SPManager.getInstance().getBoolean(Constants.EXTRA_OPEN_PORT,openPort);
    }

    public String waitData;

    public String getWaitData() {
        waitData = SPManager.getInstance().getString(Constants.WAIT_DATA,"");
        return waitData;
    }

    public void setWaitData(String waitData) {
        this.waitData = waitData;
        SPManager.getInstance().putString(Constants.WAIT_DATA,waitData);
    }


    public String wardName;

    public String getWardName() {
        wardName = SPManager.getInstance().getString(Constants.WARD_NAME,"");
        return wardName;
    }

    public void setWardName(String name) {
        this.wardName = name;
        SPManager.getInstance().putString(Constants.WARD_NAME,name);
    }


    public String type;

    public String getType() {
        type = SPManager.getInstance().getString(Constants.EXTRA_WASTE_TYPE,"");
        return type;
    }

    public void setType(String type) {
        this.type = type;
        SPManager.getInstance().putString(Constants.EXTRA_WASTE_TYPE,type);
    }

    public String createTime;

    public String getCreateTime() {
        createTime = SPManager.getInstance().getString(Constants.EXTRA_WASTE_TIME,"");
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        SPManager.getInstance().putString(Constants.EXTRA_WASTE_TIME,createTime);
    }

    public String recyclePerson;

    public String getRecyclePerson() {
        recyclePerson = SPManager.getInstance().getString(Constants.EXTRA_WASTE_RECYCLE,"");
        return recyclePerson;
    }

    public void setRecyclePerson(String recyclePerson) {
        this.recyclePerson = recyclePerson;
        SPManager.getInstance().putString(Constants.EXTRA_WASTE_RECYCLE,recyclePerson);
    }


    public void clearSp(){
        App.getApp().setWardName("");
        App.getApp().setWaitData("");
        App.getApp().setRecyclePerson("");
        App.getApp().setType("");
        App.getApp().setCreateTime("");
    }


}
