package com.minorfish.car.twoth.bus;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.car.twoth.R;
import com.tangjd.common.widget.RvBase;

public class RvBle extends RvBase<String> {

    public RvBle(Context context) {
        super(context);
    }

    public RvBle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvBle(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.adapter_ble_item;
    }

    @Override
    public void customConvert(BaseViewHolder holder, String bean) {
        holder.setText(R.id.tv, bean.split("\n")[0]);
    }
}
