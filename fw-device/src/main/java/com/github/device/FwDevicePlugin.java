package com.github.device;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class FwDevicePlugin extends Plugin {

    public FwDevicePlugin(PluginWrapper pluginWrapper) {
        super(pluginWrapper);
    }

    @Override
    public void start() {
        super.start();
        System.out.println("fw plugin start.......");
    }

    @Override
    public void stop() {
        super.stop();
        System.out.println("fw plugin stop......");
    }

    @Override
    public void delete() {
        super.delete();
        System.out.println("fw plugin delete......");
    }
}
