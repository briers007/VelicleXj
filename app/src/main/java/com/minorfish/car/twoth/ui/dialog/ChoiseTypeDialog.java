package com.minorfish.car.twoth.ui.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.ui.boxout.RvType;
import com.minorfish.car.twoth.ui.boxout.WasteTypeBean;

import java.util.ArrayList;
import java.util.List;

public class ChoiseTypeDialog extends DialogFragment implements View.OnClickListener {

    private RvType rvList;
    List<WasteTypeBean> allList;

    public OnChoiceLister mOnChoiceLister;
    public Button btnAll;

    public interface OnChoiceLister {
        void onChoick(List<WasteTypeBean> list);
    }

    public static ChoiseTypeDialog showDialog(FragmentManager manager, ArrayList<WasteTypeBean> list) {
        Bundle arguments = new Bundle();
        arguments.putSerializable("list", list);
        ChoiseTypeDialog dialog = new ChoiseTypeDialog();
        dialog.allList = list;
        dialog.setArguments(arguments);
        dialog.show(manager, "");
        return dialog;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAll : {
                rvList.selects.clear();
                rvList.selects.addAll(allList);
                rvList.mAdapter.notifyDataSetChanged();
                /*String txt=btnAll.getText().toString().trim();
                if(txt.equals("全选")){
                    btnAll.setText("取消全选");
                    rvList.selects.clear();
                    rvList.selects.addAll(allList);
                    rvList.mAdapter.notifyDataSetChanged();
                }
                if(txt.equals("取消全选")){
                    btnAll.setText("全选");
                    rvList.selects.clear();
                    rvList.selects.remove(allList);
                    rvList.mAdapter.notifyDataSetChanged();
                }*/
                break;
            }
            case R.id.btnCancel : {
                dismiss();
                if(mOnChoiceLister != null) {
                    mOnChoiceLister.onChoick(null);
                }
                break;
            }
            case R.id.btnOk : {
                if(rvList.selects.isEmpty()) {
                    Toast.makeText(v.getContext(), "请选择要出库的类型", Toast.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
                if(mOnChoiceLister != null) {
                    mOnChoiceLister.onChoick(rvList.selects);
                }
                break;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), getTheme());

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_choice_type, null, false);
        builder.setView(view);
        rvList = view.findViewById(R.id.rvList);
        allList = (ArrayList<WasteTypeBean>) getArguments().getSerializable("list");
        rvList.setData(allList);
        btnAll=view.findViewById(R.id.btnAll);
        btnAll.setOnClickListener(this);
        view.findViewById(R.id.btnCancel).setOnClickListener(this);
        view.findViewById(R.id.btnOk).setOnClickListener(this);

        Dialog dialog = builder.create();
        return dialog;
    }
}
