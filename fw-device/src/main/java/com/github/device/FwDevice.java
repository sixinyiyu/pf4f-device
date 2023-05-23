package com.github.device;

import org.pf4j.Extension;

import java.util.HashMap;
import java.util.Map;

@Extension
public class FwDevice implements DeviceReg {

    private static final  String FW = "fw";

    @Override
    public String deviceType() {
        return FW;
    }

    @Override
    public RegRequest extractRegRequest(Map<String, String> headers, byte[] body) {
        RegRequest request = new RegRequest();
        request.setDeviceType(FW);
        request.setDeviceNo("0011223344");
        Map<String, String> extra = new HashMap<>();
        extra.put("name", "智慧防火墙");
        extra.put("version", "V20230111_8483@hotfix");
        request.setExtras(extra);
        return request;
    }
}
