package com.minorfish.car.twoth.ui.main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WardsBean implements Serializable {

    /**
     *   public String hospitalName; //医院名称
     *     public String keShiName; //科室名称
     *     public String nurseName; //护士名称
     * "wardId": "50",
     * 	"nfcCode": "",
     * 	"nurseId": "22930",
     * 	"nurseSignPic": "",
     */

    public String hospitalName;
    public String keShiName;
    public String nurseName;

    public String wardId;
    private String nfcCode;
    private String nurseId;
    private String nurseSignPic;
    private List<Trashe> trashes;

    public String getWardId() {
        return wardId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public String getNfcCode() {
        return nfcCode;
    }

    public void setNfcCode(String nfcCode) {
        this.nfcCode = nfcCode;
    }

    public String getNurseId() {
        return nurseId;
    }

    public void setNurseId(String nurseId) {
        this.nurseId = nurseId;
    }

    public String getNurseSignPic() {
        return nurseSignPic;
    }

    public void setNurseSignPic(String nurseSignPic) {
        this.nurseSignPic = nurseSignPic;
    }

    public List<Trashe> getTrashes() {
        return trashes;
    }

    public void setTrashes(List<Trashe> trashes) {
        this.trashes = trashes;
    }

    public static class Trashe implements Serializable{
        /**
         *  obj.put("trashNo", bean.mId);
         *                 obj.put("weight", bean.mWeight);
         *                 obj.put("trashTypeCode", bean.mWasteTypeBean2.mCode);
         *                 obj.put("count", bean.count);
         *                 obj.put("placenta", bean.placenta);
         *                 obj.put("specialType", bean.specialType);
         */
        private String trashNo;
        private String weight;
        private String trashTypeCode;
        private long mCreateTime;
        private int count;
        private int placenta;
        private int specialType;


        public String getTrashNo() {
            return trashNo;
        }

        public void setTrashNo(String trashNo) {
            this.trashNo = trashNo;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public long getmCreateTime() {
            return mCreateTime;
        }

        public void setmCreateTime(long mCreateTime) {
            this.mCreateTime = mCreateTime;
        }

        public String getTrashTypeCode() {
            return trashTypeCode;
        }

        public void setTrashTypeCode(String trashTypeCode) {
            this.trashTypeCode = trashTypeCode;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getPlacenta() {
            return placenta;
        }

        public void setPlacenta(int placenta) {
            this.placenta = placenta;
        }

        public int getSpecialType() {
            return specialType;
        }

        public void setSpecialType(int specialType) {
            this.specialType = specialType;
        }
    }

    public static WardsBean parse(Object data){
        JSONObject obj;
        try {
            obj = (JSONObject) data;
            WardsBean wardsBean = new WardsBean();
            wardsBean.setWardId(obj.optString("wardId"));
            wardsBean.setNfcCode(obj.optString("nfcCode"));
            wardsBean.setNurseId(obj.optString("nurseId"));
            wardsBean.setNurseSignPic(obj.optString("nurseSignPic"));

            List<Trashe> trasheList = new ArrayList<Trashe>();
            JSONArray jsonArray = obj.optJSONArray("trashes");
            for (int i=0;i<jsonArray.length();i++){
                Trashe trashe = new Trashe();
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                trashe.setTrashNo(jsonObject.optString("trashNo"));
                trashe.setWeight(jsonObject.optString("weight"));
                trashe.setTrashTypeCode(jsonObject.optString("trashTypeCode"));
                trashe.setCount(jsonObject.optInt("count"));
                trashe.setPlacenta(jsonObject.optInt("placenta"));
                trashe.setSpecialType(jsonObject.optInt("specialType"));

                trasheList.add(trashe);
            }

            wardsBean.setTrashes(trasheList);

            return wardsBean;

        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

}
