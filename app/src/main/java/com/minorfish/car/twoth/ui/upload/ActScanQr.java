package com.minorfish.car.twoth.ui.upload;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.bus.EventUpload;
import com.minorfish.car.twoth.ui.XBaseActivity;
import com.tangjd.common.abs.BaseActivity;
import com.tangjd.common.utils.Log;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActScanQr extends XBaseActivity implements OnFragmentInteractionListener {

    public static final String TAG = "ActScanQr";

    String[] title = {"扫描科室", "扫描交接人二维码", "扎带扫描", "选择类型", "称 重", "打印标签"};

    @Bind(R.id.et)
    EditText et;
    @Bind(R.id.tv1)
    TextView tv1;
    @Bind(R.id.tv2)
    TextView tv2;
    @Bind(R.id.tv3)
    TextView tv3;
    @Bind(R.id.tv4)
    TextView tv4;
    @Bind(R.id.tv5)
    TextView tv5;
    @Bind(R.id.tv6)
    TextView tv6;
    @Bind(R.id.v1)
    View v1;
    @Bind(R.id.v2)
    View v2;
    @Bind(R.id.v3)
    View v3;
    @Bind(R.id.v4)
    View v4;
    @Bind(R.id.v5)
    View v5;
    @Bind(R.id.v6)
    View v6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_scanqr);

        ButterKnife.bind(this);

        tv1.setText(title[0]);
        tv2.setText(title[1]);
        tv3.setText(title[2]);
        tv4.setText(title[3]);
        tv5.setText(title[4]);
        tv6.setText(title[5]);

        RxBus.get().register(this);

        enableBackFinish();

        int at = getIntent().getIntExtra("at", 0);

        if (at > 0) {
            Navigation.findNavController(this, R.id.nav_scan_host).navigate(R.id.fragment2);
            if (at > 1) {
                Navigation.findNavController(this, R.id.nav_scan_host).navigate(R.id.fragment3);
                if (!TextUtils.isEmpty(UploadData.getInstance().mWaste.mId)) {
                    at += 1;
                }
            }
            if (at > 2) {
                Navigation.findNavController(this, R.id.nav_scan_host).navigate(R.id.fragment4);
            }
            if (at > 3) {
                Navigation.findNavController(this, R.id.nav_scan_host).navigate(R.id.fragment5);
            }
            if (at > 4) {
                Navigation.findNavController(this, R.id.nav_scan_host).navigate(R.id.fragment6);
            }
        }
        et.requestFocus();
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

    @Subscribe(thread = EventThread.MAIN_THREAD)
    public void onEventUpload(EventUpload event) {
        if (event.success) {
            finish();
        }
    }

    // 初始化扫描二维码
    protected void startScanDevice() {
    }

    protected void stopScanDevice() {
    }

    private void setIndex(int at) {
        setToolbarTitle(title[at]);
        tv1.setSelected(true);
        v1.setSelected(true);
        tv2.setSelected(at > 0);
        v2.setSelected(at > 0);
        tv3.setSelected(at > 1);
        v3.setSelected(at > 1);
        tv4.setSelected(at > 2);
        v4.setSelected(at > 2);
        tv5.setSelected(at > 3);
        v5.setSelected(at > 3);
        tv6.setSelected(at > 4);
        v6.setSelected(at > 4);
    }


    @Override
    public void onFragmentResume(Fragment fragment) {
        Log.e(TAG, "onFragmentResume:" + fragment.getClass().getSimpleName());
        if (fragment instanceof Fragment1 || fragment instanceof Fragment2 || fragment instanceof Fragment3) {
            startScanDevice();
        } else {
            stopScanDevice();
        }
        if (fragment instanceof Fragment1) {
            setIndex(0);
            playSound("scan_ks.wav");
        } else if (fragment instanceof Fragment2) {
            setIndex(1);
            playSound("scan_jjr.wav");
        } else if (fragment instanceof Fragment3) {
            setIndex(2);
            playSound("scan_zd.wav");
        } else if (fragment instanceof Fragment4) {
            setIndex(3);
            playSound("sound_xzlx.wav");
        } else if (fragment instanceof Fragment5) {
            setIndex(4);
            playSound("sound_cz.wav");
        } else if (fragment instanceof Fragment6) {
            setIndex(5);
            playSound("sound_dy.wav");
        }

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                callbackResult("11111");
//            }
//        }, 2000);
    }

    @Override
    public void onFragmentPause(Fragment fragment) {
        Log.e(TAG, "onFragmentPause:" + fragment.getClass().getSimpleName());
//        if (fragment instanceof Fragment1) {
//        } else if (fragment instanceof Fragment2) {
//        } else if (fragment instanceof Fragment3) {
//        } else if (fragment instanceof Fragment4) {
//        }
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

    private Fragment getCurFragment() {
        try {
            Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_scan_host);
            if (navHostFragment == null) return null;
            return navHostFragment.getChildFragmentManager().getFragments().get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_scan_host).navigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getIntExtra("at", 0) == 2) {
            Fragment fragment = getCurFragment();
            if ((fragment instanceof Fragment3 || fragment instanceof Fragment4)) {
                UploadData.getInstance().reScan();
                ActUpload.startActivity(this);
                finish();
                return;
            }
        }
        if (onSupportNavigateUp()) return;
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        et.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        et.clearFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            stopScanDevice();
            RxBus.get().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startActivity(BaseActivity context, int at) {
        if (at == 0) {
            UploadData.getInstance().clear();
        } else {
//            UploadData.getInstance().reScan();
            UploadData.getInstance().doWeight();
        }
        Intent intent = new Intent(context, ActScanQr.class);
        intent.putExtra("at", at);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_scan_host);
        if (fragment instanceof Fragment5) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (fragment instanceof Fragment6) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
