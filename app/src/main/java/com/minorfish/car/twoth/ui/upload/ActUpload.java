package com.minorfish.car.twoth.ui.upload;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.bus.EventUpload;
import com.minorfish.car.twoth.util.LogUtils;
import com.minorfish.car.twoth.util.OkHttpUtil;
import com.minorfish.car.twoth.util.QRCodeUtil;
import com.minorfish.car.twoth.R;
import com.tangjd.common.abs.BaseActivity;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.StringKit;

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


public class ActUpload extends XBaseActivity {

    private static final String TAG = ActUpload.class.getSimpleName();
    @Bind(R.id.tv_header)
    TextView tv_header;
    @Bind(R.id.tvNumber)
    TextView tvNumber;
    @Bind(R.id.rvUpload)
    RvUpload rvUpload;
    @Bind(R.id.et)
    EditText et;


    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x001:
                    dismissProgressDialog();
                    App.getApp().clearSp();
                    UploadData.getInstance().clear();
                    Toast.makeText(ActUpload.this, "上传成功!", Toast.LENGTH_SHORT).show();
                    thisFinish();
                    break;
                case 0x002:
                    dismissProgressDialog();
                    String error= (String) msg.obj;
                    Log.i(TAG, "handleMessage: "+error);
                    LogUtils.i("上传失败",error);
                    Toast.makeText(ActUpload.this, "上传失败"+error, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_upload_layout);
        ButterKnife.bind(this);
        setToolbarTitle("待交接列表");
        enableBackFinish();

        List<WasteModel> list = UploadData.getInstance().mAddedWasteBeans;
        rvUpload.setData(list);
        tv_header.setText(UploadData.getInstance().keshi.keShiName+" 合计：");
        tvNumber.setText(list.size()+"包");

        if(list!=null && list.size()>0){
            printWaste(list);
        }else{
            printWaste(null);
        }

        // 点击上传
        findViewById(R.id.ivSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 150
                UploadData.getInstance().keshi.trashes = UploadData.getInstance().mAddedWasteBeans;
                processUpload();
            }
        });


        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String txt = et.getText().toString().trim();
                if (actionId == KeyEvent.ACTION_DOWN && !TextUtils.isEmpty(txt)) {
                    try {
                        callbackResult(txt);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        et.requestFocus();
        playSound("sound_upload_more.wav");
    }

    @Override
    protected void onPause() {
        super.onPause();
        et.clearFocus();
    }
    private void callbackResult(String data) {
        try {
            String code = "";
            if(data.contains("\t")){
                data = data.replace("\t","");
            }
            code = data.substring(data.lastIndexOf("=")+1);

            UploadData.getInstance().qrcode = data;
            UploadData.getInstance().ercode = QRCodeUtil.createQRCodeBitmap(data, 140, 140);
            checkCode(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
    }

    private void nextStep() {
        ActScanQr.startActivity(this, 2);
        thisFinish();
    }

    public void checkCode(final String code) {
        List<WasteModel> mAddedWasteBeans = UploadData.getInstance().mAddedWasteBeans;
        if (mAddedWasteBeans != null && !mAddedWasteBeans.isEmpty()) {
            for (WasteModel item : mAddedWasteBeans) {
                if (item.mId.equals(code)) {
                    this.showTipDialog("该扎带已经被使用");
                    return;
                }
            }
        }
//        nextStep();
        this.showProgressDialog();
        Api.checkWasteUUID(code, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject data) {
                Result result = Result.parse(data);
                if (result.isSuccess()) {
                    JSONObject resp = (JSONObject) result.mData;
                    boolean validation = resp.optBoolean("validation");
                    if (validation) {
                        UploadData.getInstance().mWaste = new WasteModel();
                        UploadData.getInstance().mWaste.mId = code;
                        nextStep();
                    } else {
                        String msg =  resp.optString("message");
                        if(StringKit.isNotEmpty(msg)){
                            onError(msg);
                        }else {
                            onError("该扎带已经被使用");
                        }
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
    }

    private void processUpload() {
        showProgressDialog(false);
        String url=Api.getDomainName()+"/hw/car/v2/bag/create";
        WardModel wardModel = UploadData.getInstance().keshi;
        JSONObject param = new JSONObject();
        JSONArray array = new JSONArray();
        if(wardModel.trashes == null) {
            wardModel.trashes = new ArrayList<>();
        }
        for (int i = 0; i < wardModel.trashes.size(); i++) {
            WasteModel bean = wardModel.trashes.get(i);
            JSONObject obj = new JSONObject();
            try {
                obj.put("trashNo", bean.mId);
                obj.put("weight", bean.mWeight);
//                if(bean.mWasteTypeBean2!=null){
                obj.put("trashTypeCode", bean.mWasteTypeBean2.mCode);
//                }else {
//                    obj.put("trashTypeCode", bean.mWasteTypeBean.mCode);
//                }
                obj.put("count", bean.count);
                obj.put("placenta", bean.placenta);
                obj.put("specialType", bean.specialType);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }
        try {
            param.put("wardId", wardModel.wardId);
            param.put("nfcCode", wardModel.nfcCode);
            param.put("nurseId", wardModel.nurseId);
            param.put("nurseSignPic", wardModel.nurseSignPic);
            param.put("trashes", array);

            Log.i(TAG, "processUpload: "+App.getApp().getSignInBean().token);

            String jsonStr = param.toString();
            Log.i(TAG, "processUpload: " + jsonStr);
            LogUtils.i("确认垃圾上传所提交的参数",jsonStr);

            String canshu = "url:"+url+"----token:"+App.getApp().getSignInBean().token;
            LogUtils.i("请求参数",canshu);

            OkHttpUtil.getInstance().postJsonAsyn(url, jsonStr, App.getApp().getSignInBean().token, new OkHttpUtil.NetCall() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    String str = response.body().string();
                    Log.i(TAG, "success: "+str);
                    LogUtils.i("服务器返回响应数据",str);
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        int mCode = jsonObject.optInt("code");
                        if(mCode==200){
                            mHandler.sendEmptyMessage(0x001);
                           /* showLongToast("上传成功");
                            UploadData.getInstance().clear();
                            thisFinish();*/
                        }else{
                            String msg=jsonObject.optString("message");
                            Message message=Message.obtain();
                            message.what=0x002;
                            message.obj=msg;
                            mHandler.sendMessage(message);
//                            showLongToast("上传失败："+msg);
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
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


       /* Api.doWasteUpload(UploadData.getInstance().keshi, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                String str=new Gson().toJson(response);
                Log.i(TAG, "onResponse: "+str);
                if (result.isSuccess()) {
                    // ActUploadSuccess.startActivity(ActWasteList.this);
                    showLongToast("上传成功");
                    UploadData.getInstance().clear();
                    thisFinish();
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

    public static void startActivity(BaseActivity act) {
        act.startActivityForResult(new Intent(act, ActUpload.class), 9901);
    }

    private void thisFinish() {
        com.hwangjr.rxbus.RxBus.get().post(new EventUpload(true));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 20);
    }

    @Override
    public void onBackPressed() {
        if (UploadData.getInstance().mAddedWasteBeans == null || UploadData.getInstance().mAddedWasteBeans.isEmpty()) {
            super.onBackPressed();
            return;
        }
        showAlertDialog("是否确定取消本次上传垃圾操作？", "取消上传", "继续上传", true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UploadData.getInstance().clear();
                thisFinish();
            }
        }, null);
    }

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    public void playSound(String sound) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        try {
            AssetFileDescriptor fileDescriptor = getAssets().openFd(sound);
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void printWaste(List<WasteModel> list){
        WardModel wardModel = UploadData.getInstance().keshi;
        JSONObject param = new JSONObject();
        JSONArray array = new JSONArray();
        if (list == null) {
            App.getApp().setWaitData("");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            WasteModel bean = list.get(i);
            JSONObject obj = new JSONObject();
            try {
                obj.put("trashNo", bean.mId);
                obj.put("weight", bean.mWeight);
                obj.put("trashTypeCode", bean.mWasteTypeBean2.mCode);
                obj.put("count", bean.count);
                obj.put("placenta", bean.placenta);
                obj.put("specialType", bean.specialType);
                obj.put("createTime",bean.mCreateTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }

        try {
            param.put("wardId", wardModel.wardId);
            param.put("nfcCode", wardModel.nfcCode);
            param.put("nurseId", wardModel.nurseId);
            param.put("nurseSignPic", wardModel.nurseSignPic);
            param.put("trashes", array);

            String jsonStr = param.toString();
            Log.i(TAG, "printWaste: " + jsonStr);

            App.getApp().setWaitData(jsonStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
