package com.github.device;

import org.pf4j.ExtensionPoint;

import java.util.Map;

public interface DeviceReg extends ExtensionPoint {

    /**支持的设备类型*/
    String deviceType();

    /**提取设备注册请求*/
    RegRequest extractRegRequest(Map<String, String> headers, byte[] body);
}
