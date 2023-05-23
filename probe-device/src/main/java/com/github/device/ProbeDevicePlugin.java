package com.github.device;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class ProbeDevicePlugin extends Plugin {

    public ProbeDevicePlugin(PluginWrapper pluginWrapper) {
        super(pluginWrapper);
    }

    @Override
    public void start() {
        super.start();
        System.out.println("probe plugin start.......");
    }

    @Override
    public void stop() {
        super.stop();
        System.out.println("probe plugin stop......");
    }

    @Override
    public void delete() {
        super.delete();
        System.out.println("probe plugin delete......");
    }
}
