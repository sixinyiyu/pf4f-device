package com.github.device.app;

import com.github.device.DeviceReg;
import org.apache.commons.io.FileUtils;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class Index {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String PLUGIN_DIR = "D://var//plugins";

    private final IDeviceRegFacade deviceRegFacade;
    private final PluginManager pluginManager;

    public Index() {
        pluginManager = new DefaultPluginManager(Paths.get(PLUGIN_DIR));
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        deviceRegFacade = new DeviceRegService(pluginManager);
    }

    // 列出所有插件
    @GetMapping("/plugins")
    public Object listPlugins() {
        List<DeviceReg> extensions = pluginManager.getExtensions(DeviceReg.class);
        logger.info("匹配到 {}个满足'{}'的扩展点", extensions.size(), DeviceReg.class.getName());
        List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
        logger.info("当前已启动插件数: {}", startedPlugins.size());
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
            logger.info("Extensions added by plugin '{}':", pluginId);
            List pluginManagerExtensions = pluginManager.getExtensions(pluginId);
            for (Object extensionClass: pluginManagerExtensions) {
                logger.info("   Class: {}", extensionClass.getClass().getCanonicalName());
            }
            Set<String> extensionClassNames = pluginManager.getExtensionClassNames(pluginId);
            for (String extension : extensionClassNames) {
                logger.info("{}", extension);
            }
        }
        return pluginManager.getPlugins().stream().map(this::toPluginInfoMap).collect(Collectors.toList());
    }

    private Map<String,String> toPluginInfoMap(PluginWrapper wrapper) {
        Map<String, String> pluginInfo = new HashMap<>();
        pluginInfo.put("id", wrapper.getPluginId());
        pluginInfo.put("version", wrapper.getDescriptor().getVersion());
        pluginInfo.put("path", wrapper.getPluginPath().toAbsolutePath().toString());
        pluginInfo.put("state", wrapper.getPluginState().name());
        pluginInfo.put("provider", wrapper.getDescriptor().getProvider());
        pluginInfo.put("class", wrapper.getDescriptor().getPluginClass());
        pluginInfo.put("description", wrapper.getDescriptor().getPluginDescription());
        return pluginInfo;
    }

    // 新增插件
    @PostMapping("/plugin/add")
    public Object addPlugin(@RequestParam("file") MultipartFile file) {
        // 解析插件
        logger.info("新增插件文件（{}）", file.getOriginalFilename());
        try {
            Path pluginPath = Paths.get(PLUGIN_DIR, file.getOriginalFilename());
            File pluginFile = pluginPath.toFile();
            /**
             * 文件存在了判断下是否同一个, 判断插件是否已经加载过了
             *
             */
            byte[] fileBody = file.getBytes();
            Optional<PluginWrapper> existPlugin = pluginManager.getPlugins().stream().filter(pluginWrapper -> pluginWrapper.getPluginPath().equals(pluginPath)).findAny();

            if (existPlugin.isPresent()) {
                if (DigestUtils.md5DigestAsHex(new FileInputStream(pluginFile)).equals(DigestUtils.md5DigestAsHex(fileBody))) {
                    return toPluginInfoMap(existPlugin.get());
                }
                pluginManager.unloadPlugin(existPlugin.get().getPluginId());
                pluginManager.deletePlugin(existPlugin.get().getPluginId());
                FileUtils.forceDeleteOnExit(pluginFile);
            }
            FileUtils.writeByteArrayToFile(pluginFile, fileBody);
            // 重复上传会异常
            String pluginId = pluginManager.loadPlugin(pluginPath);
            pluginManager.startPlugin(pluginId);
            logger.info("新加载插件（{}）", pluginId);
            return toPluginInfoMap(pluginManager.getPlugin(pluginId));
        } catch (Exception e) {
            logger.error("解析插件文件异常" + e.getMessage(), e);
        }
        return "上传插件异常";
    }

    // 删除插件
    @DeleteMapping("plugin/{pluginId}")
    public void removePlugin(@PathVariable String pluginId) {
        PluginWrapper plugin = pluginManager.getPlugin(pluginId);
        if (Objects.isNull(plugin)) {
            logger.warn("插件id（{}）不存在", pluginId);
            return;
        }
        logger.warn("删除插件（{}）", pluginId);
        pluginManager.unloadPlugin(pluginId);
        pluginManager.deletePlugin(pluginId);
        FileUtils.deleteQuietly(plugin.getPluginPath().toFile());
    }

    // 启停插件
    @PutMapping("plugin/{pluginId}/start")
    public void startPlugin(@PathVariable String pluginId) {
        PluginWrapper plugin = pluginManager.getPlugin(pluginId);
        if (Objects.isNull(plugin)) {
            logger.warn("插件id（{}）不存在", pluginId);
            return;
        }
        logger.info("插件（{}）当前状态: {}", pluginId, plugin.getPluginState().name());
        if (PluginState.STOPPED.equals(plugin.getPluginState())) {
            logger.info("启动插件（{}）", pluginId);
            plugin.setPluginState(PluginState.STARTED);
        } else {
            logger.info("插件状态已经是已启动,忽略本次操作");
        }
    }

    @PutMapping("/plugin/{pluginId}/stop")
    public void stopPlugin(@PathVariable String pluginId) {
        PluginWrapper plugin = pluginManager.getPlugin(pluginId);
        if (Objects.isNull(plugin)) {
            logger.warn("插件id（{}）不存在", pluginId);
            return;
        }
        logger.info("插件（{}）当前状态: {}", pluginId, plugin.getPluginState().name());
        if (PluginState.STOPPED.equals(plugin.getPluginState())) {
            logger.info("插件状态已经是已停止,忽略本次操作");
        } else {
            logger.info("停止插件（{}）", pluginId);
            plugin.setPluginState(PluginState.STOPPED);
        }
    }

    // test plugin
    @PostMapping("/plugin/test")
    public Object testPlugin(HttpServletRequest request, @RequestBody byte[] body) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return deviceRegFacade.deviceReg(headers, body);
    }


}
