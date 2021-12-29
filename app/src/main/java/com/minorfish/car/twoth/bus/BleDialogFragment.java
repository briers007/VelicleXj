package com.minorfish.car.twoth.bus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.minorfish.car.twoth.R;

import java.util.ArrayList;

public class BleDialogFragment extends DialogFragment {

    private OnItemClickListener listener;

    public static BleDialogFragment newInstance(ArrayList<String> items, OnItemClickListener listener) {
        BleDialogFragment fragment = new BleDialogFragment();
        fragment.listener = listener;
        Bundle args = new Bundle();
        args.putStringArrayList("items", items);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.act_ble_list, container, false);
        RvBle rvBle = root.findViewById(R.id.rv_new);
        ArrayList<String> data = getArguments().getStringArrayList("items");
        rvBle.setData(data);

        rvBle.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
//                String name = adapter.getData().get(position);
//                if(listener != null) {
                    listener.onSimpleItemClick(adapter, view, position);
                    dismiss();
//                }
            }
        });
        root.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setCancelable(false);
        return root;
    }
}
