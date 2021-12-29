package com.minorfish.car.twoth.ui.sign;

import com.google.gson.Gson;

/**
 * Author: tangjd
 * Date: 2017/6/8
 */

public class SignInBean {

    public String instName;
    public int userId;
    public String token;
    public String name;
    public String phone;
    public boolean pass;
    public String examToken;
    public int weightType;
    public int needBox;
    public int specialType;
    public int bindCar;
    public String carDeviceCode;
    public String nfccode;

    public static SignInBean objectFromData(String str) {

        return new Gson().fromJson(str, SignInBean.class);
    }
}
