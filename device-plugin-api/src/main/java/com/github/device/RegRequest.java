package com.github.device;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RegRequest implements Serializable {

    /**设备类型*/
    private String deviceType;

    /**设备编号*/
    private String deviceNo;

    /**额外信息*/
    private Map<String, String> extras = new HashMap<>();

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }
}
