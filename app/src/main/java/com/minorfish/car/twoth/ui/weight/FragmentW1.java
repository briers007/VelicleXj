package com.minorfish.car.twoth.ui.weight;

import androidx.navigation.Navigation;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.ui.upload.Fragment3;
import com.minorfish.car.twoth.ui.upload.UploadData;
import com.minorfish.car.twoth.ui.upload.WasteModel;
import com.tangjd.common.abs.JsonApiBase;
import com.tangjd.common.utils.StringKit;

import org.json.JSONObject;

import java.util.List;

public class FragmentW1 extends Fragment3 {

    public FragmentW1() {

    }

    @Override
    protected WeightUploadActivity getAct() {
        return super.getAct();
    }

    @Override
    protected void nextStep() {
        if(getAct() == null) return;
        Navigation.findNavController(getAct(), R.id.nav_upload_host).navigate(R.id.nav_action_w_1to2);
    }

    @Override
    public void checkCode(final String code) {
        getAct().showProgressDialog();
        Api.checkSupplyUuid(code, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject data) {
                Result result = Result.parse(data);
//                if (result.isSuccess()) {
//                    JSONObject resp = (JSONObject) result.mData;
//                    boolean validation = resp.optBoolean("validation");
//                    if (validation) {
                        getAct().sourceCode = code;
                        nextStep();
//                    } else {
//                        String msg = resp.optString("message");
//                        if (StringKit.isNotEmpty(msg)) {
//                            onError(msg);
//                        } else {
//                            onError("该扎带已经被使用");
//                        }
//                    }
//                } else {
//                    onError(result.mMsg);
//                }
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
        });
    }
}
