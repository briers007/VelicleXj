package com.minorfish.car.twoth.ui.main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.App;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WaitDataAdapter extends BaseAdapter {

    private Context mContext;
    private List<WardsBean.Trashe> mList;

    public SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());

    public WaitDataAdapter(Context mContext, List<WardsBean.Trashe> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view==null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.wait_item,viewGroup,false);

            viewHolder.tv_name = view.findViewById(R.id.tv_name);
            viewHolder.tv_type = view.findViewById(R.id.tv_waste_type);

            viewHolder.tv_spe_type = view.findViewById(R.id.tv_SpeType);
            viewHolder.tv_sj = view.findViewById(R.id.tv_person_sj);
            viewHolder.tv_hs = view.findViewById(R.id.tv_person_hs);
            viewHolder.tv_time = view.findViewById(R.id.tv_time);
            viewHolder.tv_no = view.findViewById(R.id.tv_no);
            viewHolder.tv_index = view.findViewById(R.id.tvIndex);

            viewHolder.tv_weight = view.findViewById(R.id.tvWeight);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        int mIndex = i+1;
        if(mIndex<10){
            viewHolder.tv_index.setText("0"+mIndex);
        }else{
            viewHolder.tv_index.setText(""+mIndex);
        }

        WardsBean.Trashe trashe = mList.get(i);

        String type = App.getApp().getType();

        if(trashe.getPlacenta()==1){
            type+=" 胎盘";
        }

        String name = App.getApp().getWardName();
        String nurseName = App.getApp().getRecyclePerson();

        Log.i("WaitDataAdapter", "getView: "+type+"\n"+"\n"+name+"\n"+nurseName);

        viewHolder.tv_type.setText("类型："+type);

        if(trashe.getSpecialType()==1){
            viewHolder.tv_spe_type.setVisibility(View.VISIBLE);
        }else{
            viewHolder.tv_spe_type.setVisibility(View.GONE);
        }

        viewHolder.tv_time.setText("时间："+mFormat.format(trashe.getmCreateTime()));
        viewHolder.tv_name.setText("科室："+name);
        viewHolder.tv_sj.setText("收集人："+ App.getApp().getSignInBean().name);
        viewHolder.tv_hs.setText("交接人："+nurseName);
        viewHolder.tv_weight.setText("重量："+trashe.getWeight()+"Kg");
        viewHolder.tv_no.setText("编号："+trashe.getTrashNo());

        return view;
    }


    class ViewHolder{
        TextView tv_name;
        TextView tv_type;
        TextView tv_spe_type;
        TextView tv_sj;
        TextView tv_hs;
        TextView tv_time;
        TextView tv_no;
        TextView tv_index;
        TextView tv_weight;
    }
}
