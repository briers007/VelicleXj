package com.minorfish.car.twoth.ui.main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class CarBean implements Serializable {

    public String id;
    public String name;

    public static List<CarBean> parse(String data) {
        try {
            return new Gson().fromJson(data, new TypeToken<List<CarBean>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
