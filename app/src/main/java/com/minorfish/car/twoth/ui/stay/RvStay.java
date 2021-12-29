package com.minorfish.car.twoth.ui.stay;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.car.twoth.R;
import com.tangjd.common.widget.RvBase;


public class RvStay extends RvBase<StayBean> {

    public RvStay(Context context) {
        super(context);
    }

    public RvStay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvStay(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.adapter_stay_item;
    }

    @Override
    public BaseQuickAdapter<StayBean, BaseViewHolder> instanceCustomAdapter() {
//        return super.instanceCustomAdapter();
//        return new RvBase.CustomAdapter(this.customSetItemLayoutId());
        return new RvBase.CustomAdapter(this.customSetItemLayoutId()) {
            @Override
            public void onBindViewHolder(BaseViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                try {
                    int index = position+1;
                    if(index < 10) {
                        holder.setText(R.id.tvIndex, "0" + (position + 1));
                    } else {
                        holder.setText(R.id.tvIndex, "" + (position + 1));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void customConvert(BaseViewHolder holder, StayBean bean) {
        holder.setText(R.id.tv_time, "时间："+bean.time)
                .setText(R.id.tv_name, "科室："+ bean.ward)
                .setText(R.id.tv_waste_type, "科室："+bean.type)
                .setText(R.id.tv_person_sj, "收集人："+bean.staff)
                .setText(R.id.tv_person_hs, "交接人："+bean.nurse)
                .setText(R.id.tv_no, "编号："+bean.no);

        try {
            holder.setText(R.id.tv_weight, bean.weight + "kg");
        } catch (Exception e) {}
        if(!TextUtils.isEmpty(bean.specialType) && "1".equalsIgnoreCase(bean.specialType)) {
            holder.setVisible(R.id.tvSpeType, true);
        } else  {
            holder.setVisible(R.id.tvSpeType, false);
        }
    }
}
