package com.minorfish.car.twoth.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.util.PreferenceKit;
import com.tangjd.common.abs.BaseActivity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public abstract class XBaseActivity<T> extends BaseActivity<T> {

    public Handler mHandler = new Handler();

    public static void startAct(Context cxt, Class clazz) {
        cxt.startActivity(new Intent(cxt, clazz));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .transparentStatusBar()
//                .transparentNavigationBar()
                .init();
    }

    public void enableBackFinish() {
        super.enableBackFinish();
        this.getToolbar().setNavigationIcon(R.drawable.ic_menu_back);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private MediaPlayer mMediaPlayer = new MediaPlayer();

    public void playSound(String sound) {
        try {
            if(!PreferenceKit.getBoolean(this, "sound_open", true)) return;
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            AssetFileDescriptor fileDescriptor = getAssets().openFd(sound);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            fileDescriptor.close();
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setToolbarTitle(String title) {
        try {
            ((TextView) this.findViewById(R.id.toolbar_title)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        } catch (Exception e) {}
        super.setToolbarTitle(title);
    }

    private void setDialogSize(AlertDialog dialog) {
        //必须在调用show方法后才可修改样式
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mController = mAlert.get(dialog);
            Field mMessage = mController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mController);
//                    mMessageView.setTextColor(Color.RED);//message样式修改成红色
            if(mMessageView != null) {
                mMessageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }
            // title 同理
            Field mTitle = mController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mController);
            if(mTitleView != null) {
                mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if(button != null) {
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }
        button = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        if(button != null) {
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }
        button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if(button != null) {
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }
    }

    @Override
    public void showTipDialog(String message, boolean cancelable, String positiveText, DialogInterface.OnClickListener onPositiveClick) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(XBaseActivity.this);
                    builder.setMessage(message);
                    builder.setPositiveButton(positiveText, onPositiveClick);
                    builder.setCancelable(cancelable);
                    if (!isFinishing()) {
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        setDialogSize(dialog);
                    }

                } catch (Exception var6) {
                    var6.printStackTrace();
                }
            }
        });


    }

    @Override
    public void showAlertDialog(String message, String positiveText, String negativeText, boolean cancelable, DialogInterface.OnClickListener onPositiveClick, DialogInterface.OnClickListener onNegativeClick) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(XBaseActivity.this);
                    builder.setMessage(message);
                    builder.setCancelable(cancelable);
                    builder.setPositiveButton(positiveText, onPositiveClick);
                    builder.setNegativeButton(negativeText, onNegativeClick);
                    if (!isFinishing()) {
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        setDialogSize(dialog);
                    }
                } catch (Exception var8) {
                    var8.printStackTrace();
                }
            }
        });

    }

    @Override
    public void showSingleChoiceDialog(String[] items, DialogInterface.OnClickListener listener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(XBaseActivity.this);
                    builder.setItems(items, listener);
                    if (!isFinishing()) {
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        setDialogSize(dialog);
                    }
                } catch (Exception var4) {
                    var4.printStackTrace();
                }
            }
        });

    }

    @Override
    public void showSingleChoiceDialog(List<T> items, DialogInterface.OnClickListener listener) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(XBaseActivity.this);
                    String[] arr = new String[items.size()];

                    for(int i = 0; i < items.size(); ++i) {
                        arr[i] = items.get(i).toString();
                    }

                    builder.setItems(arr, listener);
                    if (!isFinishing()) {
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        setDialogSize(dialog);
                    }
                } catch (Exception var6) {
                    var6.printStackTrace();
                }
            }
        });

    }

//    @Override
//    public void showMultiChoiceDialog(boolean cancelable, List<T> items, android.content.DialogInterface.OnClickListener positiveClickListener, android.content.DialogInterface.OnClickListener negativeClickListener, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
//        try {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            String[] arr = new String[items.size()];
//
//            for(int i = 0; i < items.size(); ++i) {
//                arr[i] = items.get(i).toString();
//            }
//
//            builder.setMultiChoiceItems(arr, (boolean[])null, onMultiChoiceClickListener);
//            builder.setPositiveButton(17039370, positiveClickListener);
//            builder.setNegativeButton(17039360, negativeClickListener);
//            builder.setCancelable(cancelable);
//            if (!this.isFinishing()) {
//                builder.create().show();
//            }
//        } catch (Exception var9) {
//            var9.printStackTrace();
//        }
//
//    }
}
