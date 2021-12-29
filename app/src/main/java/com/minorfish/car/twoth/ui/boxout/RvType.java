package com.minorfish.car.twoth.ui.boxout;

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

public class RvType extends RvBase<WasteTypeBean> {

    public BaseQuickAdapter mAdapter;
    public List<WasteTypeBean> selects = new ArrayList<>();

    public RvType(Context context) {
        super(context);
    }

    public RvType(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvType(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setData(List<WasteTypeBean> data) {
        super.setData(data);
        selects.clear();
    }

    @Override
    public BaseQuickAdapter<WasteTypeBean, BaseViewHolder> instanceCustomAdapter() {
        mAdapter = super.instanceCustomAdapter();

        return mAdapter;
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new GridLayoutManager(context, 3);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.adapter_type_item;
    }

    @Override
    public void customConvert(BaseViewHolder holder, final WasteTypeBean bean) {
        TextView tv = holder.getView(R.id.tv);
        tv.setText(bean.mName);
        tv.setSelected(selects.contains(bean));

        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selects.contains(bean)) {
                    selects.remove(bean);
                } else  {
                    selects.add(bean);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
