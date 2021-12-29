package com.minorfish.car.twoth.ui.weight;

import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.ui.upload.Fragment6;
import com.minorfish.car.twoth.usb.PrinterHelperSerial;
import com.tangjd.common.abs.JsonApiBase;

import org.json.JSONObject;

public class FragmentW3 extends Fragment6 {

    public FragmentW3() {

    }

    @Override
    protected WeightUploadActivity getAct() {
        return super.getAct();
    }

    @Override
    protected void printClick() {
//        PrinterHelperSerial.getInstance(requireContext()).printBagIn(new BagInBean());

        doSubmit();
    }

    @Override
    protected void printNoClick() {
        doSubmit();
    }

    private void doSubmit() {
        getAct().showProgressDialog();
        Api.doSupply(getAct().sourceCode, getAct().weight, new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject data) {
                Result result = Result.parse(data);
                if (result.isSuccess()) {
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
        });
    }
}
