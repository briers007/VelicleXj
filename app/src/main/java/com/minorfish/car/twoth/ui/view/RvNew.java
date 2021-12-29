package com.minorfish.car.twoth.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.car.twoth.R;
import com.tangjd.common.widget.RvBase;

import org.json.JSONObject;


public class RvNew extends RvBase<NewBean> {
    public RvNew(Context context) {
        super(context);
    }

    public RvNew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvNew(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private View mHeaderView;

    @Override
    public BaseQuickAdapter<NewBean, BaseViewHolder> instanceCustomAdapter() {
        BaseQuickAdapter adapter = super.instanceCustomAdapter();
        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_rv_new_header, null);
        adapter.addHeaderView(mHeaderView);
        return adapter;
    }

    public void setHeaderData(Object data) {
        try {
            JSONObject obj = (JSONObject) data;
            if(mHeaderView != null) {
                ((TextView)mHeaderView.findViewById(R.id.tv_name)).setText(obj.optString("instName"));
                ((TextView)mHeaderView.findViewById(R.id.tv_total)).setText(String.format("%1$d袋%2$skg", obj.optInt("totalNum", 0), obj.optString("totalWeight", "")));
                ((TextView)mHeaderView.findViewById(R.id.tv_person)).setText(obj.optString("chargePerson"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.rv_new_item_layout;
    }

    @Override
    public void customConvert(BaseViewHolder holder, NewBean bean) {
        holder.setText(R.id.tv_time, bean.inTime)
                .setText(R.id.tv_name, bean.wardName)
                .setText(R.id.tv_waste_type, bean.typeName)
                .setText(R.id.tv_sub_type, bean.subTypeName)
                .setText(R.id.tv_weight, bean.weight+"kg")
                .setText(R.id.tvSpeType, bean.specitalTypeName)
                .setText(R.id.tv_no, "编号："+bean.no);
        if(TextUtils.isEmpty(bean.specitalTypeName) || "null".equalsIgnoreCase(bean.specitalTypeName)) {
            holder.setVisible(R.id.tvSpeType, false);
        } else  {
            holder.setVisible(R.id.tvSpeType, true);
        }
        holder.setGone(R.id.tv_header, bean.index == 1);
//        TextView tvStatus = holder.getView(R.id.tv_status);
//        BagStatusHelper.setBagStatus2View(tvStatus, bean.mStatus);
    }
}
