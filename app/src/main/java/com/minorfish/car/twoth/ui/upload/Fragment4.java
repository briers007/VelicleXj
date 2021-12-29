package com.minorfish.car.twoth.ui.upload;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.minorfish.car.twoth.abs.Api;
import com.minorfish.car.twoth.abs.App;
import com.minorfish.car.twoth.abs.Result;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.ui.boxout.WasteTypeBean;
import com.tangjd.common.abs.JsonApiBase;

import org.json.JSONObject;

import java.util.List;

public class Fragment4 extends Fragment {

    private static final String TAG = Fragment4.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private RadioGroup rg_xg;
    private RadioGroup rg21;
    private RadioGroup rg22;
    private RadioGroup rg23;
    private RadioGroup rg2;
    private View vT;
    private View vXg;
    private View vType;
    private EditText etNum;
    private TextView tvNext;

    // 是否有新冠
    private boolean hasXg = true;
    private int checkedTypeId = -1;


    public Fragment4() {
        // Required empty public constructor
    }

    private XBaseActivity getAct() {
        if(mListener == null) return null;

        return (XBaseActivity) mListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment4, container, false);
        rg_xg = root.findViewById(R.id.rg_xg);
        rg21 = root.findViewById(R.id.rg21);
        rg22 = root.findViewById(R.id.rg22);
        rg23 = root.findViewById(R.id.rg23);
        rg2 = root.findViewById(R.id.rg2);
        vT = root.findViewById(R.id.vT);
        vXg = root.findViewById(R.id.vXg);
        vType = root.findViewById(R.id.vType);
        etNum = root.findViewById(R.id.etNum);
        tvNext = root.findViewById(R.id.tvNext);
        if(UploadData.getInstance().typeList.isEmpty()) {
            getTypeData();
        } else {
            showTypeSelectDialog();
        }

//        int isXG= App.getApp().getSignInBean().specialType;
        int isXG=App.sharedUtility.getTypeCode();
        Log.i(TAG, "onCreateView: "+isXG);
        if(isXG==0){
            vXg.setVisibility(View.GONE);
        }
        if(isXG==1){
            vXg.setVisibility(View.VISIBLE);
        }

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rg2.getCheckedRadioButtonId() == R.id.rbtn1) {
                    checkNext(true);
                } else {
                    getAct().showToast("请选择是否胎盘");
                }
            }
        });
        return root;
    }

    private void setListener() {
        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(rg2.getCheckedRadioButtonId() == R.id.rbtn2) {
                    etNum.setText("");
                    etNum.setFocusable(false);
                    etNum.setFocusableInTouchMode(false);
                    tvNext.setVisibility(View.GONE);
                    checkNext(false);
                } else {
                    etNum.setText("1");
                    etNum.setFocusable(true);
                    etNum.setFocusableInTouchMode(true);
                    etNum.requestFocus();
                    tvNext.setVisibility(View.VISIBLE);
                }
            }
        });
        rg_xg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i != -1 && radioGroup.findViewById(i) != null) {
                    checkNext(false);
                }
            }
        });

        RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                try {
                    if(checkedId == -1) return;
                    View rbtn = group.findViewById(checkedId);
                    if(rbtn == null || !((RadioButton) rbtn).isChecked()) return;
                    if(group == rg21) {
                        rg22.clearCheck();
                        rg23.clearCheck();
                    } else if(group == rg22) {
                        rg21.clearCheck();
                        rg23.clearCheck();
                    } else if(group == rg23) {
                        rg21.clearCheck();
                        rg22.clearCheck();
                    }
                    final List<WasteTypeBean> typeList = UploadData.getInstance().typeList;
                    WasteTypeBean mWasteTypeBean = typeList.get(checkedId);
                    checkedTypeId = checkedId;
                    if(mWasteTypeBean != null && "病理类".equals(mWasteTypeBean.mName) && mWasteTypeBean.children != null) {
                        vT.setVisibility(View.VISIBLE);
                        etNum.setText("");
                        etNum.setFocusable(false);
                        etNum.setFocusableInTouchMode(false);
//                        rg2.clearCheck();
                        ((RadioButton) rg2.findViewById(R.id.rbtn1)).setChecked(true);
                    } else {
                        vT.setVisibility(View.GONE);
                        rg2.clearCheck();
                        checkNext(false);
                    }
                }catch (Exception e){}
            }
        };
        rg21.setOnCheckedChangeListener(listener);
        rg22.setOnCheckedChangeListener(listener);
        rg23.setOnCheckedChangeListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mListener != null) {
            mListener.onFragmentResume(this);
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setListener();
            }
        }, 600);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mListener != null) {
            mListener.onFragmentPause(this);
        }
        rg2.setOnCheckedChangeListener(null);
        rg_xg.setOnCheckedChangeListener(null);
        rg21.setOnCheckedChangeListener(null);
        rg22.setOnCheckedChangeListener(null);
        rg23.setOnCheckedChangeListener(null);
    }

    // 非胎盘是下一步
    private void checkNext(boolean tips) {
        boolean canNext = true;
        // 0 未开启新冠。1 是新冠，2 非新冠
        int xinguan = 0;
        if(vXg.getVisibility() == View.VISIBLE) {
            if(rg_xg.getCheckedRadioButtonId() == -1) {
                if(tips) {
                    Toast.makeText(getContext(), "请选择是否新冠", Toast.LENGTH_LONG).show();
                }
                return;
            }
            if(rg_xg.getCheckedRadioButtonId() == R.id.rb_xb1) {
                xinguan = 1;
            } else {
                xinguan = 2;
            }
        }
        int type = checkedTypeId;
        if(type == -1) {
            if(tips) {
                Toast.makeText(getContext(), "请选择类型", Toast.LENGTH_LONG).show();
            }
            return;
        }

        nextStep();
    }

    private void nextStep() {
        if(getAct() == null) return;
        try {
            WasteModel wasteBean = UploadData.getInstance().mWaste;
            if (vT.getVisibility() == View.VISIBLE) {
                wasteBean.placenta = rg2.getCheckedRadioButtonId() == R.id.rbtn1 ? 1 : 0;
                if(!TextUtils.isEmpty(etNum.getText().toString().toLowerCase())) {
                    wasteBean.count = Integer.parseInt(etNum.getText().toString().trim());
                }
            } else {
                wasteBean.placenta = 0;
                wasteBean.count = 0;
            }
            wasteBean.specialType = rg_xg.getCheckedRadioButtonId() == R.id.rb_xb1 ? 1 : 0;
            wasteBean.mWasteTypeBean2 = UploadData.getInstance().typeList.get(checkedTypeId);

            App.getApp().setType(wasteBean.mWasteTypeBean2.mName);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Navigation.findNavController(getAct(), R.id.nav_scan_host).navigate(R.id.nav_action_4to5);
    }

    private void showTypeSelectDialog() {
        if(getAct() == null) return;
//        Point screenSize = DisplayUtils.getScreenSize(this);
//        int widgetWidth = DisplayUtils.dpToPx(120, getAct());
        int widgetWidth = getResources().getDimensionPixelOffset(R.dimen.rb_btn_width);
        int widgetHeight = getResources().getDimensionPixelSize(R.dimen.rb_btn_height);
        int left = getResources().getDimensionPixelSize(R.dimen.rb_btn_left);
        LayoutInflater inflater = LayoutInflater.from(getAct());
        final List<WasteTypeBean> typeList = UploadData.getInstance().typeList;
        rg21.removeAllViews();
        rg22.removeAllViews();
        rg23.removeAllViews();
        int column = 5;
        for (int i = 0; i < typeList.size(); i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.rb_type, null);
            radioButton.setGravity(Gravity.CENTER);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(widgetWidth, widgetHeight);
            layoutParams.setMargins(i % column == 0 ? 0 : left, 0, 0, 0);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            radioButton.setBackgroundResource(R.drawable.bg_rb_selector);
            radioButton.setId(i);
            radioButton.setTextColor(getResources().getColorStateList(R.color.color_rb_txt));
            radioButton.setButtonDrawable(null);
            radioButton.setText(typeList.get(i).mName);

            (i / column == 0 ? rg21 : i / column == 1 ? rg22 : rg23).addView(radioButton, layoutParams);
        }
    }

    private void getTypeData() {
        Api.getWasteType(new JsonApiBase.OnJsonResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                Result result = Result.parse(response);
                if (result.isSuccess()) {
                    List<WasteTypeBean> list = WasteTypeBean.parse(result.mData);
                    if(list != null && !list.isEmpty()) {
                        UploadData.getInstance().typeList.clear();
                        UploadData.getInstance().typeList.addAll(list);
                        showTypeSelectDialog();
                    }
                } else {
                    onError(result.mMsg + " " + result.mCode);
                }
            }

            @Override
            public void onError(String error) {
            }

            @Override
            public void onFinish(boolean withoutException) {
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
//        hasXg = App.getApp().getSignInBean().specialType == 1;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
