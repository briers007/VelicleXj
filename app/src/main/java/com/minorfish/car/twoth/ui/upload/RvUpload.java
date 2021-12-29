package com.minorfish.car.twoth.ui.upload;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.App;
import com.tangjd.common.widget.RvBase;
import com.tangjd.common.widget.RvBaseWithSlideDelete;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RvUpload extends RvBaseWithSlideDelete<WasteModel> {
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());

    public RvUpload(Context context) {
        super(context);
    }

    public RvUpload(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RvUpload(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public LayoutManager customSetLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public int customSetItemLayoutId() {
        return R.layout.adapter_rv_upload_item;
    }

    @Override
    public BaseQuickAdapter<WasteModel, BaseViewHolder> instanceCustomAdapter() {
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
    public void customConvert(final BaseViewHolder holder, WasteModel bean) {
        String type = "";
        if(bean.mWasteTypeBean2!=null){
            type = "类型："+bean.mWasteTypeBean2.mName+" ";
        }

        if(bean.placenta == 1) {
            type += " 胎盘";
        }
        holder.setText(R.id.tv_waste_type, type);

        holder.setText(R.id.tv_time, "时间："+mFormat.format(bean.mCreateTime))
                .setText(R.id.tv_name, "科室："+UploadData.getInstance().keshi.keShiName)
                .setText(R.id.tv_person_sj, "收集人："+ App.getApp().getSignInBean().name)
                .setText(R.id.tv_person_hs, "交接人："+UploadData.getInstance().keshi.nurseName)
                .setText(R.id.tvWeight, "重量："+bean.mWeight+"Kg")
                .setText(R.id.tv_no, "编号："+bean.mId);

        if(bean.specialType == 1) {
            holder.setVisible(R.id.tvSpeType, true);
        } else  {
            holder.setVisible(R.id.tvSpeType, false);
        }
    }
}
