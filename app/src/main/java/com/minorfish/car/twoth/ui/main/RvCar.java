package com.minorfish.car.twoth.ui.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.car.twoth.R;
import com.tangjd.common.widget.RvBase;

import java.util.ArrayList;
import java.util.List;

public class RvCar extends RvBase<CarBean> {

    public BaseQuickAdapter mAdapter;
    public GridLayoutManager manager;
    public List<CarBean> selects = new ArrayList<>();

    public RvCar(Context context) {
        super(context);
    }

    public RvCar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvCar(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setData(List<CarBean> data) {
        super.setData(data);
        try {

            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int i) {
                    if(mAdapter.getItemCount() == 2) {
                        return 2;
                    }if(mAdapter.getItemCount() == 1) {
                        return 4;
                    }

                    return 1;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseQuickAdapter<CarBean, BaseViewHolder> instanceCustomAdapter() {
        mAdapter = super.instanceCustomAdapter();
        return mAdapter;
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        manager = new GridLayoutManager(context, 4);
        return manager;
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.adapter_car_item;
    }

    @Override
    public void customConvert(BaseViewHolder holder, final CarBean bean) {
        TextView tv = holder.getView(R.id.tv);
        tv.setText(bean.name);
        tv.setSelected(selects.contains(bean));

        tv.setOnClickListener(view -> {
            selects.clear();
            selects.add(bean);
//            if(selects.contains(bean)) {
//                selects.remove(bean);
//            } else  {
//                selects.add(bean);
//            }
            mAdapter.notifyDataSetChanged();
        });
    }
}
