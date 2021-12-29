package com.minorfish.car.twoth.ui.weight;

import java.io.Serializable;

public class BagInBean implements Serializable {
    public String bagCode;
    public String reason;
    public int status; // 0正常 1异常
    public String weight;
    public String weightStr;
    public String typeCode;
    public String time;

    public String wardName;
    public String wasteType;

    public int count = 0;
    public int placenta = 0;
}