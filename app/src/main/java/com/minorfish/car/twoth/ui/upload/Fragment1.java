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
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.StringKit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Fragment1 extends Fragment implements SetFragmentData {

    private static final String TAG = Fragment1.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
//    private TextView tvTip;


    public Fragment1() {
        // Required empty public constructor
    }

    private XBaseActivity getAct() {
        if(mListener == null) return null;

        return (XBaseActivity) mListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment1, container, false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mListener != null) {
            mListener.onFragmentResume(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mListener != null) {
            mListener.onFragmentPause(this);
        }
    }

    @Override
    public void setData(String data) {
        try {
            JSONObject obj = new JSONObject(data);

            //获取科室名称
            getWardName(obj.optInt("id"),2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextStep() {
        try {

            if(getAct() == null) return;
            Navigation.findNavController(getAct(), R.id.nav_scan_host).navigate(R.id.nav_action_1to2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //调用API接口获取科室相关信息
    public void getWardName(final int tagId,final int type) {
        if(getAct() == null) return;
        getAct().showProgressDialog();

        String url = Api.getDomainName() + "/hw/car/v2/ward/getName?param="+tagId;
        String token = App.getApp().getSignInBean().token;
        Log.i(TAG, "getWardName: "+url+"----"+token);

        OkHttpUtil.getInstance().getDataAsyn(url, token, new OkHttpUtil.NetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.i(TAG, "success: "+res);

                try {
                    JSONObject jsonObject = new JSONObject(res);
                    int mCode = jsonObject.optInt("code");
                    if(mCode==200){
                        JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                        WardModel bean = UploadData.getInstance().keshi;
                        if(jsonObject1!=null && bean!=null){
                            String mName= jsonObject1.optString("name");
                            if(StringKit.isNotEmpty(mName)){
                                if(type == 1) {//nfc扫码
//                                bean.mId = null;
//                                bean.nfcCode = tagId+"";
                                } else {
                                    bean.wardId = tagId+"";
                                    bean.hospitalName = jsonObject1.optString("instName");
                                }
                                bean.keShiName = mName;
                                getAct().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getAct().dismissProgressDialog();
                                        nextStep();
                                    }
                                });
                            }else{
                                String faild = jsonObject1.optString("message");
                                Log.i(TAG, "success: "+faild);
                                getAct().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getAct().dismissProgressDialog();
                                        Toast.makeText(getAct(), "获取失败"+faild, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    }else{
                        String error = jsonObject.optString("message");
                        getAct().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getAct().dismissProgressDialog();
                                Toast.makeText(getAct(), "请求失败"+error, Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Call call, IOException e) {
                getAct().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getAct().dismissProgressDialog();
                        Toast.makeText(getAct(), "请求失败"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        /*Api.getWardNameById(tagId+"", new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject data) {
                Result result = Result.parse(data);
                if (result.isSuccess()) {
                    JSONObject resp = (JSONObject) result.mData;
                    WardModel bean = UploadData.getInstance().keshi;
                    if(resp!=null && bean != null) {
                        String name = resp.optString("name");
                        if (StringKit.isNotEmpty(name)) {
                            if(type == 1) {//nfc扫码
//                                bean.mId = null;
//                                bean.nfcCode = tagId+"";
                            } else {
                                bean.wardId = tagId+"";
                                bean.hospitalName = resp.optString("instName");
                            }
                            bean.keShiName = name;
                            nextStep();
                        } else {
                            String msg = resp.optString("message");
                            if (StringKit.isNotEmpty(msg)) {
                                onError(msg);
                            } else {
                                onError("此卡尚未绑定科室");
                            }
                        }
                    }else{
                        onError("连接失败");
                    }
                } else {
                    onError(result.mMsg);
                }
            }

            @Override
            public void onError(String error) {
                if(getAct() != null)
                getAct().showTipDialog(error);
            }

            @Override
            public void onFinish(boolean withoutException) {
                if(getAct() != null)
                getAct().dismissProgressDialog();
            }
        });*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}