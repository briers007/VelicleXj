package com.minorfish.car.twoth.abs;

import android.text.TextUtils;

import com.minorfish.car.twoth.BuildConfig;
import com.minorfish.car.twoth.ui.upload.WardModel;
import com.minorfish.car.twoth.ui.upload.WasteModel;
import com.tangjd.common.abs.JsonApiBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 省平台一体车（包括西安三院，独墅湖，电力，常州，无锡中医院）
 * 同一平台项目均可为副本，更改项目地址无需修改接口
 */
public class Api {

    private static final String HTTP_SCHEMA;
    private static final String HTTP_SCHEMA_SUFFIX;
    private static final String HOST;

    static {
        HTTP_SCHEMA = "http";
        HTTP_SCHEMA_SUFFIX = "://";
        HOST = BuildConfig.API_HOST;
    }

    public static String getDomainName() {
        return HTTP_SCHEMA + HTTP_SCHEMA_SUFFIX + HOST;
    }

    public static Map<String, String> getHeaders() {
        if (App.getApp().mUserBean == null || TextUtils.isEmpty(App.getApp().mUserBean.token)) {
            return null;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Token", App.getApp().mUserBean.token);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private static void doPost(String url, JSONObject params, Map<String, String> headers, JsonApiBase.OnJsonResponseListener listener) {
        if(headers == null) {
            headers = getHeaders();
        }
        JsonApiBase.doPostRequest(url, params, headers, listener);
    }

    private static void doGet(String url, Map<String,String> params, Map<String, String> headers, JsonApiBase.OnJsonResponseListener listener) {
        if(headers == null) {
            headers = getHeaders();
        }
        JsonApiBase.doGetRequest(url, params, headers, listener);
    }

    public static void login(String nfcId, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/hospital/pda/v2/login";
        JSONObject params = new JSONObject();
        try {
            params.put("nfccode", nfcId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doPost(url, params, null, listener);
    }

    // 科室扫描
    public static void getWardNameById(String id, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/ward/getName";
        Map<String, String> params = new HashMap<>();
        params.put("param", id);
        doGet(url, params, getHeaders(), listener);
    }

    // 交接人
    public static void checkJiaojieId(String id, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/get/"+id;
//        Map<String, String> params = new HashMap<>();
//        params.put("param", id);
        doGet(url, null, getHeaders(), listener);
    }

    // 一体车列表
    public static void bindList(JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/list";
        doGet(url, null, getHeaders(), listener);
    }

    // 一体车绑定
    public static void bindCar(String id, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/bind";
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        doGet(url, params, getHeaders(), listener);
    }

    // 扎带
    public static void checkWasteUUID(String uuid, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/sourceCode/validation";
        Map<String, String> params = new HashMap<>();
        params.put("sourceCode", uuid);
        doGet(url, params, getHeaders(), listener);
    }

    // 类型
    public static void getWasteType(JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/types";
        doGet(url, null, getHeaders(), listener);
    }

    /**
     * 上传垃圾Condition {
     * nfcCode (string, optional): 科室绑定NFC的code ,
     * nurseId (string, optional): 护士ID ,
     * nurseSignPic (string, optional): 护士签名图片（附一院使用） ,
     * trashes (Array[垃圾Condition], optional): 医废详情 ,
     * wardId (string, optional): 科室ID
     * }
     * 垃圾Condition {
     * count (integer, optional): 垃圾数量（只有胎盘需要） ,
     * placenta (integer, optional): 是否是胎盘：1是 0不是 ,
     * specialType (integer, optional): 特殊类型：0 非新冠 1 新冠 ,
     * trashNo (string, optional): 垃圾编号 ,
     * trashTypeCode (string, optional): 垃圾类型编码 ,
     * weight (string, optional): 垃圾重量，没有称重传0.00
     * }
     * @param ward
     * @param listener
     */
    public static void doWasteUpload(WardModel ward, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/bag/create";
//        {
//            "nfcCode": "string",
//                "nurseId": "string",
//                "nurseSignPic": "string",
//                "trashes": [
//            {
//                "count": 0,
//                    "placenta": 0,
//                    "specialType": 0,
//                    "trashNo": "string",
//                    "trashTypeCode": "string",
//                    "weight": "string"
//            }
//  ],
//            "wardId": "string"
//        }
//        JSONObject body = new JSONObject();
//        try {
//            body.put("nfcCode", ward.nfcCode);
//            body.put("nurseId", ward.nurseId);
//            body.put("nurseSignPic", ward.nurseSignPic);
//            body.put("wardId", ward.wardId);
//            body.put("trashes", ward.trashes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        JSONObject param = new JSONObject();
        JSONArray array = new JSONArray();
        if(ward.trashes == null) {
            ward.trashes = new ArrayList<>();
        }
        for (int i = 0; i < ward.trashes.size(); i++) {
            WasteModel bean = ward.trashes.get(i);
            JSONObject obj = new JSONObject();
            try {
                obj.put("trashNo", bean.mId);
                obj.put("weight", bean.mWeight);
//                if(bean.mWasteTypeBean2!=null){
                obj.put("trashTypeCode", bean.mWasteTypeBean2.mCode);
//                }else {
//                    obj.put("trashTypeCode", bean.mWasteTypeBean.mCode);
//                }
                obj.put("count", bean.count);
                obj.put("placenta", bean.placenta);
                obj.put("specialType", bean.specialType);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }
        try {
            param.put("wardId", ward.wardId);
            param.put("nfcCode", ward.nfcCode);
            param.put("nurseId", ward.nurseId);
            param.put("nurseSignPic", ward.nurseSignPic);
            param.put("trashes", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doPost(url, param, null, listener);
    }

    // 垃圾入库-汇总
    public static void getBagInSummary(JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/bag/in/summary";
        JSONObject params = new JSONObject();
        doPost(url, params, getHeaders(), listener);
    }

    // 垃圾入库-入库
    /**
     * V2入库条件 {
     * exceptionMessage (string, optional): 重量比对异常时的异常信息 ,
     * nfccode (string, optional),
     * status (integer, optional): 重量比对状态：1 正常；2 异常 ,
     * weight (string, optional): 入库总重量
     * }
     * @param exceptionMessage
     * @param nfccode
     * @param status
     * @param weight
     * @param listener
     */
    public static void doBagIn(String exceptionMessage, String nfccode,  int status,  String weight, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/bag/in";
        JSONObject params = new JSONObject();
        try {
            params.put("exceptionMessage", exceptionMessage);
            params.put("nfccode", nfccode);
            params.put("status", status);
            params.put("weight", weight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        doPost(url, params, null, listener);
    }

    /**
     * 医废列表查询条件
     * end (string, optional): (必填)结束日期，格式：yyyy-MM-dd ,
     * inStatus (integer, optional): (必填)医废入库状态: 0-待入库 1-已入库 2-已出库 ,
     * page (integer, optional): 页码 ,
     * size (integer, optional): 每页大小 ,
     * start (string, optional): (必填)开始日期，格式：yyyy-MM-dd
     * @param start
     * @param end
     * @param page
     * @param inStatus
     * @param listener
     */
    public static void getHistoryByPage(String start, String end, int size, int page, int inStatus, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/history/findByPage";
        JSONObject params = new JSONObject();
        try {
            params.put("start", start);
            params.put("end", end);
            params.put("size", size);
            params.put("page", page);
            params.put("inStatus", inStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doPost(url, params, null, listener);
    }

    // 历史垃圾-Tab页数量统计
    public static void getHistoryStat(String start, String end, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/history/stat";
        Map<String, String> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        doGet(url, params, null, listener);
    }

    // 称重上传 - 扫描扎带
    public static void checkSupplyUuid(String uuid, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/bag/check";
        Map<String, String> params = new HashMap<>();
        params.put("sourceCode", uuid);
        doGet(url, params, getHeaders(), listener);
    }

    // 称重上传
    public static void doSupply(String bagNo, String weight, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/bag/supply";
        JSONObject params = new JSONObject();
        try {
            params.put("bagNo", bagNo);
            params.put("weight", weight);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doPost(url, params, getHeaders(), listener);
    }

    // 待称重列表
    public static void getSupplyList(int size, int page, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/bag/supply/list";
        JSONObject params = new JSONObject();
        try {
            params.put("size", size);
            params.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doPost(url, params, getHeaders(), listener);
    }
//    出库Condition {
//        companyId (integer, optional): 回收公司ID ,
//                nfcCode (string, optional): 回收公司的 NFC Code ,
//                specialType (integer, optional): 特殊类型：0 非新冠 1 新冠 ,
//                typeCodes (Array[string], optional): 如果是按类型入库，则需要传入该字段
//    }
    // 出库详情
    public static void getOutDetail(int companyId, String nfcCode, int specialType, JSONArray checkedTypeCodes, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/bag/out/detail";
        JSONObject params = new JSONObject();
        try {
//            params.put("companyId", companyId);
//            params.put("nfcCode", nfcCode);
            params.put("specialType", specialType);
            params.put("typeCodes", checkedTypeCodes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doPost(url, params, getHeaders(), listener);
    }
    // 出库
    public static void bagOut(int companyId, String nfcCode, int specialType, JSONArray checkedTypeCodes, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/bag/out";
        JSONObject params = new JSONObject();
        try {
//            params.put("companyId", companyId);
            params.put("nfcCode", nfcCode);
            params.put("specialType", specialType);
            params.put("typeCodes", checkedTypeCodes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doPost(url, params, getHeaders(), listener);
    }
    // 新冠出库详情
    public static void getOutDetailNew(JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/detailOutForCOVID";
        JSONObject params = new JSONObject();
        try {
//            params.put("companyId", companyId);
//            params.put("nfcCode", nfcCode);
//            params.put("specialType", specialType);
//            params.put("typeCodes", checkedTypeCodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        doPost(url, params, getHeaders(), listener);
    }
    // 出库
    public static void bagOutNew(String nfcCode, JsonApiBase.OnJsonResponseListener listener) {
        String url = getDomainName() + "/hw/car/v2/bag/out";
        JSONObject params = new JSONObject();
        try {
//            params.put("companyId", companyId);
            params.put("nfcCode", nfcCode);
            params.put("specialType", 1);
//            params.put("typeCodes", checkedTypeCodes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doPost(url, params, getHeaders(), listener);
    }
}
