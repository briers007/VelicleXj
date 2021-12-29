package com.minorfish.car.twoth.ui.dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.minorfish.car.twoth.R;

public class YestNoDialog extends DialogFragment {

    private TextView tvMsg;
    private Button btnOk;
    private Button btnCancel;

    private Handler mHandler = new Handler();

    public interface OnNfcResult {

        void onReslut(String data);
    }

    public YestNoDialog() {
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            getDialog().setCanceledOnTouchOutside(true); //点击边际可消失
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题

            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.dialog_nfc_scan,container);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setResult("");
            }
        }, 3000);

        tvMsg = view.findViewById(R.id.tvMsg);
        btnOk = view.findViewById(R.id.btOk);
        btnCancel = view.findViewById(R.id.btCancel);
        return view;
    }

    private void setResult(String result) {
    }
}
