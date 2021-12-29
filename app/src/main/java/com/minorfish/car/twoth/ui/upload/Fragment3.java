package com.minorfish.car.twoth.ui.upload;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.Navigation;
import okhttp3.Call;
import okhttp3.Response;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.util.OkHttpUtil;
import com.minorfish.car.twoth.util.QRCodeUtil;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.StringKit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class Fragment3 extends Fragment implements SetFragmentData {

    private static final String TAG = Fragment3.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
//    private TextView tvTip;


    public Fragment3() {
        // Required empty public constructor
    }

    protected <A extends XBaseActivity> A getAct() {
        if (mListener == null) return null;

        return (A) mListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment3, container, false);
//        tvTip = ((TextView) root.findViewById(R.id.tvTip));
//        if(getAct() != null && UploadData.getInstance().mWaste != null && !TextUtils.isEmpty(UploadData.getInstance().mWaste.mId)) {
//            tvTip.setText(UploadData.getInstance().mWaste.mId);
//        } else {
//            tvTip.setText("请扫描扎带二维码");
//        }
//        root.findViewById(R.id.ivQr).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                setData("\"id\"=54bbbb1Hbbbb43a549dddd65c76a6m0I");
//                nextStep();
//            }
//        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.onFragmentResume(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListener != null) {
            mListener.onFragmentPause(this);
        }
    }

    @Override
    public void setData(String data) {
        UploadData.getInstance().qrcode = data;
        UploadData.getInstance().ercode = QRCodeUtil.createQRCodeBitmap(data, 300, 300);

        String code = "";
        if (data.contains("\t")) {
            data = data.replace("\t", "");
        }
        code = data.substring(data.lastIndexOf("=") + 1);

        try {
            checkCode(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void nextStep() {
        if (getAct() == null) return;
        Navigation.findNavController(getAct(), R.id.nav_scan_host).navigate(R.id.nav_action_3to4);
    }

    public void checkCode(final String code) {
        if (getAct() == null) return;
        List<WasteModel> mAddedWasteBeans = UploadData.getInstance().mAddedWasteBeans;
        if (mAddedWasteBeans != null && !mAddedWasteBeans.isEmpty()) {
            for (WasteModel item : mAddedWasteBeans) {
                if (item.mId.equals(code)) {
                    getAct().showTipDialog("该扎带已经被使用");
                    return;
                }
            }
        }
        getAct().showProgressDialog();

        String url = Api.getDomainName() + "/hw/car/v2/sourceCode/validation?sourceCode="+code;
        String token = App.getApp().getSignInBean().token;
        Log.i(TAG, "checkCode: "+url+"--token:--"+token);


        //get 获取校验溯源码
        OkHttpUtil.getInstance().getDataAsyn(url, token, new OkHttpUtil.NetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.i(TAG, "success: "+res);//success: {"code":200,"message":"请求成功","data":{"validation":true,"message":"成功"}}
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    int mCode = jsonObject.optInt("code");
                    if(mCode==200){
                        JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                        boolean validation = jsonObject1.optBoolean("validation");
                        String resultMsg = jsonObject1.optString("message");
                        String name = jsonObject1.optString("name");

                        if(validation){
                            //校验成功
                            getAct().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getAct().dismissProgressDialog();
                                    UploadData.getInstance().mWaste.mId = code;
                                    UploadData.getInstance().mWaste.name = name;
                                    nextStep();
                                }
                            });
                        }else{
                            //校验失败
                            getAct().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getAct().dismissProgressDialog();
                                    Toast.makeText(getAct(), "校验失败"+resultMsg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else{
                        //请求失败
                        getAct().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getAct().dismissProgressDialog();
                                Toast.makeText(getAct(), "请求失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Call call, IOException e) {
                //请求失败
                getAct().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getAct().dismissProgressDialog();
                        Toast.makeText(getAct(), "请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

       /* Api.checkWasteUUID(code, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject data) {
                Result result = Result.parse(data);
                if (result.isSuccess()) {
                    JSONObject resp = (JSONObject) result.mData;
                    boolean validation = resp.optBoolean("validation");
                    if (validation) {
                        UploadData.getInstance().mWaste.mId = code;
                        UploadData.getInstance().mWaste.name = resp.optString("name");
//                        tvTip.setText(code);
                        nextStep();
                    } else {
                        String msg = resp.optString("message");
                        if (StringKit.isNotEmpty(msg)) {
                            onError(msg);
                        } else {
                            onError("该扎带已经被使用");
                        }
                    }
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String error) {
                if (getAct() != null)
                    getAct().showTipDialog(error);
            }

            @Override
            public void onFinish(boolean withoutException) {
                if (getAct() != null)
                    getAct().dismissProgressDialog();
            }
        });*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
