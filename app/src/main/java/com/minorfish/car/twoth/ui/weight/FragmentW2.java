package com.minorfish.car.twoth.ui.weight;

import android.os.Handler;
import android.os.Looper;

import androidx.navigation.Navigation;

import com.minorfish.car.twoth.R;
import com.minorfish.car.twoth.ui.upload.Fragment5;

public class FragmentW2 extends Fragment5 {

    public FragmentW2() {

    }
    @Override
    protected WeightUploadActivity getAct() {
        return super.getAct();
    }

    @Override
    protected void nextStep() {
        if(getAct() == null) return;
        Navigation.findNavController(getAct(), R.id.nav_upload_host).navigate(R.id.nav_action_w_2to3);
    }

    @Override
    protected void onGetWeight(String weight) {
        super.onGetWeight(weight);
        getAct().weight = weight;
        getAct().playSound("ding.wav");

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoActGprint();
            }
        }, 1500);
    }

    /* @Override
    protected void onGetWeight(float weight) {
        super.onGetWeight(weight);
        getAct().weight = weight;
        getAct().playSound("ding.wav");

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoActGprint();
            }
        }, 1500);
    }*/
}
