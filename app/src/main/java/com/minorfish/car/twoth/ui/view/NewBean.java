package com.minorfish.car.twoth.ui.view;

import android.text.TextUtils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewBean {

    public String inTime;
    public int id;
    public int index;
    public double weight;
    public String no;
    public String specitalTypeName;
    public String subTypeName;
    public String typeName;
    public String wardName;

    public static List<NewBean> parseList(Object data) {
        JSONObject obj;
        try {
            obj = (JSONObject) data;
        } catch (Exception e) {
            return null;
        }
        JSONArray arr = obj.optJSONArray("garbages");
        if (arr == null) {
            return null;
        }
        List<NewBean> beans = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject item = arr.optJSONObject(i);
            if (item == null) {
                continue;
            }
            NewBean bean = new NewBean();
            bean.inTime = item.optString("inTime", "");
            bean.id = item.optInt("id", 0);
            bean.index = item.optInt("index");
            bean.weight = item.optDouble("weight",0);
            bean.no = item.optString("no", "");
            bean.specitalTypeName = item.optString("specitalTypeName", "");
            bean.subTypeName = item.optString("subTypeName", "");
            if(TextUtils.isEmpty(bean.subTypeName) || "null".equalsIgnoreCase(bean.subTypeName)) {
                bean.subTypeName = "";
            }
            bean.typeName = item.optString("typeName", "");
            bean.wardName = item.optString("wardName", "");
            beans.add(bean);
        }
        return beans;
    }
}
