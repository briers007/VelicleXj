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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class Fragment2 extends Fragment implements SetFragmentData {

    private static final String TAG = Fragment2.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
//    private TextView tvTip;


    public Fragment2() {
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
        View root =  inflater.inflate(R.layout.fragment2, container, false);
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
        String code = data;
        try {
            //校验交接人
            checkCode(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextStep() {
        if(getAct() == null) return;
        Navigation.findNavController(getAct(), R.id.nav_scan_host).navigate(R.id.nav_action_2to3);
    }

    private void checkCode(final String code) {
        if(getAct() == null) return;
        getAct().showProgressDialog();

        String url = Api.getDomainName()+ "/hw/car/v2/get/"+code;
        String token = App.getApp().getSignInBean().token;
        Log.i(TAG, "checkCode--url: "+url+"\n"+"token: "+token);

        OkHttpUtil.getInstance().getDataAsyn(url, token, new OkHttpUtil.NetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.i(TAG, "success: "+res);

                try {
                    JSONObject jsonObject = new JSONObject(res);
                    int mCode = jsonObject.optInt("code");
                    if(mCode==200){
                        JSONObject jsonObject1 = jsonObject.optJSONObject("result");
                        String mId = jsonObject1.optString("id");
                        Log.i(TAG, "onResponse: "+mId);
                        UploadData.getInstance().keshi.nurseId = mId;
                        UploadData.getInstance().keshi.nurseName = jsonObject1.optString("realname");
                        getAct().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getAct().dismissProgressDialog();
                                nextStep();
                            }
                        });

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
        /*Api.checkJiaojieId(code, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject data) {
                Result result = Result.parse2(data);
                if (result.isSuccess()) {
                    JSONObject resp = (JSONObject) result.mData;
                    UploadData.getInstance().keshi.nurseId = code;
                    UploadData.getInstance().keshi.nurseName = resp.optString("realname");
                    nextStep();
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
