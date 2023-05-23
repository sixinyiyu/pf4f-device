package com.github.device.app;

import com.github.device.DeviceReg;
import com.github.device.RegRequest;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DeviceRegService implements IDeviceRegFacade {

    private Logger logger = LoggerFactory.getLogger(getClass());


    private PluginManager pluginManager;

    public DeviceRegService(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public void persistenceDevice(Device device) {
        logger.info("持久化设备[{}]..........", device.getDeviceNo());
    }

    @Override
    public RegResponse deviceReg(Map<String, String> headers, byte[] body) {
        RegResponse regResponse = new RegResponse();
        regResponse.setSuccess(false);
        try {
            if (preBeforeDeviceReg(headers, body)) {
                String deviceType = headers.get("device-type");
                List<DeviceReg> extensions = pluginManager.getExtensions(DeviceReg.class);
                logger.info("DeviceRegExtension 长度: {}", extensions.size());
                Optional<DeviceReg> deviceRegExtension = extensions.stream().filter(extension -> extension.deviceType().equals(deviceType)).findFirst();
                if (deviceRegExtension.isPresent()) {
                    persistenceDevice(extractModel(deviceRegExtension.get().extractRegRequest(headers, body)));
                    regResponse.setSuccess(true);
                } else {
                    regResponse.setMessage(String.format("系统当前版本不支持[%s]设备类型登录", deviceType));
                }
            }
        } finally {
            postAfterDeviceReg(regResponse.isSuccess());
        }
        return regResponse;
    }


    @Override
    public Device extractModel(RegRequest request) {
        logger.info("设备[{}]信息....", request.getDeviceNo());
        request.getExtras().forEach((k,v) -> logger.info("{}={}", k,v));
        Device model = new Device();
        model.setDeviceNo(request.getDeviceNo());
        return model;
    }
}
