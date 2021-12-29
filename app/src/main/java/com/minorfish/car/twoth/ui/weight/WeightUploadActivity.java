package com.minorfish.car.twoth.ui.weight;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.minorfish.car.twoth.ui.housing.WarehousingActivity;
import com.minorfish.car.twoth.ui.upload.OnFragmentInteractionListener;
import com.minorfish.car.twoth.ui.upload.SetFragmentData;
import com.tangjd.common.utils.Log;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WeightUploadActivity extends XBaseActivity implements OnFragmentInteractionListener {

    private final String TAG = WarehousingActivity.class.getSimpleName();

    @Bind(R.id.et)
    EditText et;
    @Bind(R.id.tv1)
    TextView tv1;
    @Bind(R.id.tv2)
    TextView tv2;
    @Bind(R.id.tv3)
    TextView tv3;

    public String sourceCode;
    public String weight;

    String[] title = {"扫描扎带", "称 重", "打印标签"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_weight_upload);

        ButterKnife.bind(this);

        enableBackFinish();

        setToolbarTitle("称重上传");

        tv1.setText(title[0]);
        tv2.setText(title[1]);
        tv3.setText(title[2]);

        initView();
    }

    private void initView() {

        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String txt = et.getText().toString().trim();
                System.out.println("actionId:" + actionId + ",txt=" + txt);
                if (actionId == KeyEvent.ACTION_DOWN && !TextUtils.isEmpty(txt)) {
                    try {
                        callbackResult(txt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        et.setText("");
                    }
                    return false;
                }
                return false;
            }
        });
    }

    // 扫描科室结构
    protected void callbackResult(String scanResult) {
        Log.e(TAG, "callbackResult scanResult:" + scanResult);
        Fragment fragment = getCurFragment();
        if (fragment == null) {
            Log.e(TAG, "callbackResult page:" + null);
            return;
        }
        Log.e(TAG, "callbackResult:" + fragment.getClass().getSimpleName());
        if (fragment instanceof SetFragmentData) {
            ((SetFragmentData) fragment).setData(scanResult);
        }
    }

    @Override
    public void onFragmentResume(Fragment fragment) {

    }

    @Override
    public void onFragmentPause(Fragment fragment) {

    }

    private Fragment getCurFragment() {
        try {
            Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_upload_host);
            if (navHostFragment == null) return null;
            return navHostFragment.getChildFragmentManager().getFragments().get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_upload_host).navigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        if (getIntent().getIntExtra("at", 0) == 2) {
//            Fragment fragment = getCurFragment();
//            if ((fragment instanceof Fragment3 || fragment instanceof Fragment4)) {
//                UploadData.getInstance().reScan();
//                ActUpload.startActivity(this);
//                finish();
//                return;
//            }
//        }
//        if (onSupportNavigateUp()) return;
//        super.onBackPressed();
//    }
}
