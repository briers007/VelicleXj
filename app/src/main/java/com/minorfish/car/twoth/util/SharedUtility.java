package com.minorfish.car.twoth.util;

import android.content.SharedPreferences;

public class SharedUtility {
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;


    public void loginOut() {
        editor.remove("typeCode");
        editor.commit();
    }

    public int getTypeCode(){
        return pref.getInt("typeCode", 0);
    }

    public void setTypeCode(int code){
        editor.putInt("typeCode", code);
        editor.commit();
    }
}