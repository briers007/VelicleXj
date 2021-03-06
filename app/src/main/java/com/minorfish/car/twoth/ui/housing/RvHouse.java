package com.minorfish.car.twoth.ui.housing;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.ui.stay.StayBean;
import com.tangjd.common.widget.RvBase;

public class RvHouse extends RvBase<StayBean> {

    public RvHouse(Context context) {
        super(context);
    }

    public RvHouse(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvHouse(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.adapter_housing_item;
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
                    Log.e("RvHoust", e.getMessage());
                }
            }
        };
    }

    @Override
    public void customConvert(BaseViewHolder holder, StayBean bean) {
        holder.setText(R.id.tv_time, "?????????"+bean.time)
                .setText(R.id.tv_name, "?????????"+ bean.ward)
                .setText(R.id.tv_waste_type, "?????????"+bean.type)
                .setText(R.id.tv_person_sj, "????????????"+bean.staff)
                .setText(R.id.tv_person_hs, "????????????"+bean.nurse)
                .setText(R.id.tvWeight, "?????????"+bean.weight+"Kg")
                .setText(R.id.tv_no, "?????????"+bean.no);

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
