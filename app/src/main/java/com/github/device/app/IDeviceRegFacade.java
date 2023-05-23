package com.github.device.app;

import com.github.device.RegRequest;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

public interface IDeviceRegFacade {

    /**设备注册前置逻辑*/
    default boolean preBeforeDeviceReg(Map<String, String> headers, byte[] body) {
        System.out.println("preBeforeDeviceReg invoked");
        // 1.请求头中有标识此设备类型的值
        String deviceType = Objects.isNull(headers) ? "" : headers.get("device-type");
        if (!StringUtils.hasText(deviceType)) {
            System.out.println("未知设备类型注册");
            return false;
        }
        System.out.println("设备类型:" + deviceType);
        return true;
    }

    /**持久化设备注册操作*/
    default void persistenceDevice(Device device) {
        System.out.println("persistenceDevice invoked");
    }

    /**设备注册后置逻辑*/
    default void postAfterDeviceReg(boolean success) {
        System.out.println("postAfterDeviceReg invoked");
    }

    RegResponse deviceReg(Map<String, String> headers, byte[] body);

    /**转换为通用模型*/
    Device extractModel(RegRequest request);
}
