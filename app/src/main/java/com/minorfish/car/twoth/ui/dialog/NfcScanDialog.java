package com.minorfish.car.twoth.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.usb.NfcHelper;
import com.minorfish.car.twoth.usb.UsbService;
import com.tangjd.common.utils.ByteUtil;
import com.tangjd.common.utils.Log;

public class NfcScanDialog extends DialogFragment {

    private OnNfcResult listener;
    private int img;
    private Handler mHandler = new Handler();

    public interface OnNfcResult {

        void onReslut(String data);
    }

    public NfcScanDialog() {
    }

    @SuppressLint("ValidFragment")
    public NfcScanDialog(OnNfcResult listener, int img) {
        super();
        this.listener = listener;
        this.img = img;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(getDataRunnable);
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void getSanzeData() {
        mHandler.removeCallbacks(getDataRunnable);
        mHandler.postDelayed(getDataRunnable, 600);
    }

    private Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            NfcHelper.getInstance().getTagId();
            mHandler.postDelayed(this, 600);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        try {
//            int dialogHeight = (int) (requireContext().getResources().getDisplayMetrics().heightPixels * 0.8);
//            getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,dialogHeight);
            getDialog().setCanceledOnTouchOutside(false); //点击边际可消失
//            //6秒后让dialog消失
//            if (mHandler != null){
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        dismiss();
//                    }
//                },6000);
//            }

            NfcHelper.getInstance().setOnUsbReadCallback(new UsbService.OnUsbReadCallback() {
                @Override
                public void onUsbDataReceived(byte[] data) {
                    Log.e("TTTTTT", ByteUtil.bytesToHexStringWithoutSpace(data));
                    final String tagId = ByteUtil.bytesToHexStringWithoutSpace(ByteUtil.subByteArr(data, 0, 4));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setResult(tagId);
                            // 入库测试
//                            setResult("0AA9283C");
                            // 出库测试
//                            setResult("42BF2961");
                        }
                    });
//                setResult("E2D95EA6");
                }

                @Override
                public void onError(String error) {
                    //showTipDialog(error);
                }
            });
            getSanzeData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog
                .Builder(requireContext());

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nfc_scan, null, false);
        builder.setView(view);

        Dialog dialog = builder.create();
        try {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ((ImageView) view.findViewById(R.id.iv)).setImageResource(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    private void setResult(String result) {
        Log.e("TTTTTT", "NFC RESULT:"+result);
        if(listener != null) {
            dismiss();
            listener.onReslut(result);
        }
    }
}
