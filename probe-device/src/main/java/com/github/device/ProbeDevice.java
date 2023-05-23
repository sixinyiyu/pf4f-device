package com.github.device;

import org.pf4j.Extension;

import java.util.HashMap;
import java.util.Map;

@Extension
public class ProbeDevice implements DeviceReg {

    private static final String PROBE = "probe";

    @Override
    public String deviceType() {
        return PROBE;
    }

    @Override
    public RegRequest extractRegRequest(Map<String, String> headers, byte[] body) {
        RegRequest request = new RegRequest();
        request.setDeviceType(PROBE);
        request.setDeviceNo("201107010450");
        Map<String, String> extra = new HashMap<>();
        extra.put("name", "探针");
        extra.put("version", "20201112_10.1.5.102902@release");
        request.setExtras(extra);
        return request;
    }
}
