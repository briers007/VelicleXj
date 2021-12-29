package com.minorfish.car.twoth.ui.stay;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StayBean {

    public String id;
    public String ward;
    public String type;
    public String specialType;
    public String weight;
    public String staff;
    public String nurse;
    public String time;
    public String no;

    public static List<StayBean> parse(Object data) {
        JSONObject obj;
        try {
            obj = (JSONObject) data;
        } catch (Exception e) {
            return null;
        }

        return parse(obj.optString("list"));
    }

    public static List<StayBean> parse(String data) {

        List<StayBean> beans = new Gson().fromJson(data, new TypeToken<List<StayBean>>(){}.getType());

        return beans;
    }
}
