package com.minorfish.car.twoth.ui.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minorfish.car.twoth.R;

public class TimerTipsDialog extends DialogFragment {

    ImageView ivImg;
    TextView tvMsg;

    int time = 1;
    String msg= "后自动返回首页";

    public TimerTipsDialog() {

    }

    @SuppressLint("ValidFragment")
    public TimerTipsDialog(int time, String msg) {
        this.time = time;
        this.msg = msg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_timer_tips, container, false);


        return view;
    }
}
