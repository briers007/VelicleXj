package com.minorfish.car.twoth.ui.upload;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.ui.main.PrintBean;
import com.minorfish.car.twoth.ui.weight.BagInBean;
import com.minorfish.car.twoth.usb.PrinterHelperSerial;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment6 extends Fragment implements SetFragmentData, View.OnClickListener {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private TextView tvYyName;
    private TextView tvSpeType;
    private TextView tv_ward;
    private TextView tv_type;
    private TextView tv_weight;
    private TextView tv_time;
    private TextView tv_person_sj;
    private TextView tv_person_jj;
    private ImageView ivQrCode;

    private ImageView img;


    private View ll_tag;

    private PrintBean printBean = new PrintBean();

    public Fragment6() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment6, container, false);
        ll_tag = root.findViewById(R.id.ll_tag);
        tvYyName = root.findViewById(R.id.tvYyName);
        tvSpeType = root.findViewById(R.id.tvSpeType);
        tv_ward = root.findViewById(R.id.tv_ward);
        tv_type = root.findViewById(R.id.tv_type);
        tv_weight = root.findViewById(R.id.tv_weight);
        tv_time = root.findViewById(R.id.tv_time);
        tv_person_sj = root.findViewById(R.id.tv_person_sj);
        tv_person_jj = root.findViewById(R.id.tv_person_jj);
        ivQrCode = root.findViewById(R.id.ivQrCode);
        img = root.findViewById(R.id.img);
        root.findViewById(R.id.btn_print).setOnClickListener(this);
        root.findViewById(R.id.btn_no_print).setOnClickListener(this);

        try {

            tvYyName.setText(UploadData.getInstance().keshi.hospitalName);
            tv_ward.setText("科室："+UploadData.getInstance().keshi.keShiName);
            //科室名称
            App.getApp().setWardName(UploadData.getInstance().keshi.keShiName);

            if(UploadData.getInstance().mWaste.specialType == 1) {
                tvSpeType.setText("新 冠");
                tvSpeType.setVisibility(View.VISIBLE);
            } else {
                tvSpeType.setText("");
                tvSpeType.setVisibility(View.INVISIBLE);
            }

            if(UploadData.getInstance().mWaste.mWasteTypeBean2 != null) {
                tv_type.setText("类型：" + UploadData.getInstance().mWaste.mWasteTypeBean2.mName
                        + (UploadData.getInstance().mWaste.placenta == 1 ? " 胎盘" : ""));
            }
            tv_weight.setText("重量："+UploadData.getInstance().mWaste.mWeight+"kg");
            if(UploadData.getInstance().mWaste.mCreateTime == 0) {
                UploadData.getInstance().mWaste.mCreateTime = System.currentTimeMillis();
            }
            tv_time.setText("时间："+format.format(new Date(UploadData.getInstance().mWaste.mCreateTime)));

            tv_person_sj.setText("收集人："+ App.getApp().getSignInBean().name);
            tv_person_jj.setText("交接人："+UploadData.getInstance().keshi.nurseName);

            App.getApp().setRecyclePerson(UploadData.getInstance().keshi.nurseName);

            ivQrCode.setImageBitmap(UploadData.getInstance().ercode);

            printBean = new PrintBean();
            printBean.hospitalName = tvYyName.getText().toString();
            printBean.keShiName = tv_ward.getText().toString();
            printBean.xinguan = tvSpeType.getText().toString();
            printBean.typeName = tv_type.getText().toString();
            printBean.weight = tv_weight.getText().toString();
            printBean.time = tv_time.getText().toString();
            printBean.jjr = tv_person_jj.getText().toString();
            printBean.sjr = tv_person_sj.getText().toString();
            printBean.qrcode = UploadData.getInstance().qrcode;

            PrinterHelperSerial.getInstance(requireContext()).connect();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return root;
    }

    long curTime=0;
    private boolean isClick=true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_print:
//                printClick();
                long nowTime=System.currentTimeMillis();
                if(nowTime-curTime>3000){
                    isClick=true;
                }else{
                    isClick=false;
                }
                curTime=nowTime;

                if(isClick){
                    printClick();
                }else{
                    Toast.makeText(getAct(), "当前正在打印中...", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.btn_no_print:
                printNoClick();
                break;
        }
    }

    protected void printClick() {
        addToList();
        if(PrinterHelperSerial.getInstance(requireContext()).printBagIn(printBean)) {
            getAct().mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActUpload.startActivity(getAct());
                    getAct().finish();
                }
            }, 3000);
        }
    }

    protected void printNoClick() {
        addToList();
        ActUpload.startActivity(getAct());
        getAct().finish();
    }

    private Bitmap convertViewToBitmap(View view) {
        Bitmap desBitmap = null;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap != null) {
            desBitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        }
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        desBitmap = adjustPhotoRotation(desBitmap, 90);
        return desBitmap;
    }
    private Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

//        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.RGB_565);
        Bitmap bm1 = Bitmap.createBitmap(320, 500, Bitmap.Config.RGB_565);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);

        return bm1;
    }

    private void addToList() {
        try {
            for(WasteModel vm : UploadData.getInstance().mAddedWasteBeans) {
                if(vm.mId.equalsIgnoreCase(UploadData.getInstance().mWaste.mId)) {
                    UploadData.getInstance().mAddedWasteBeans.remove(vm);
                    break;
                }
            }
            UploadData.getInstance().mAddedWasteBeans.add(UploadData.getInstance().mWaste);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setData(String data) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 9901) {
                if (resultCode == Activity.RESULT_OK) {
                    if (getAct() != null) {
                        getAct().finish();
                        if (UploadData.getInstance().printBitmap != null) {
                            UploadData.getInstance().printBitmap.recycle();
                            UploadData.getInstance().printBitmap = null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected <A extends XBaseActivity> A getAct() {
        if (mListener == null) return null;

        return (A) mListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mListener != null) {
            mListener.onFragmentResume(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mListener != null) {
            mListener.onFragmentPause(this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private OnFragmentInteractionListener mListener;
}
