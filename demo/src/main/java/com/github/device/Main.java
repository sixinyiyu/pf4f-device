package com.github.device;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        PluginManager pluginManager = new DefaultPluginManager(Paths.get("D://var//plugins"));
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        System.out.println("--------------");
//        String deviceNo = "WY93840439502";
        List<DeviceReg> extensions = pluginManager.getExtensions(DeviceReg.class);
        System.out.println("extensions数:" + extensions.size());
        extensions.forEach(e -> System.out.println(e.deviceType()));

        pluginManager.stopPlugins();
        pluginManager.unloadPlugins();
    }
}
