package com.minorfish.car.twoth.ui.boxout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.car.twoth.R;
import com.tangjd.common.widget.RvBase;
import com.tangjd.common.widget.RvBaseWithSlideDelete;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RvOut extends RvBaseWithSlideDelete<BagBean> {
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());

    public RvOut(Context context) {
        super(context);
    }

    public RvOut(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvOut(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.adapter_boxout_item;
    }

    @Override
    public BaseQuickAdapter<BagBean, BaseViewHolder> instanceCustomAdapter() {
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
                    Log.e("RvUpload", e.getMessage());
                }
            }
        };
    }

    @Override
    public void customConvert(final BaseViewHolder holder, BagBean bean) {

        holder.setText(R.id.tvType, ""+bean.trashTypeName);
        holder
                .setText(R.id.tvName, ""+ bean.wardName)
                .setText(R.id.tvWeight, "重量："+bean.weight+"Kg");

//        if(!TextUtils.isEmpty(bean.specialType) && "1".equalsIgnoreCase(bean.specialType)) {
//            holder.setVisible(R.id.tvSpeType, true);
//        } else  {
//            holder.setVisible(R.id.tvSpeType, false);
//        }
    }
}
