package com.github.device.app;

import java.io.Serializable;

public class Device implements Serializable {

    private String deviceNo;

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }
}
