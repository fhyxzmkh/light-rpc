package com.light.rpc.registry.controller;

import com.light.rpc.registry.config.RegistryCenter;
import com.light.rpc.registry.config.RegistryConfig;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/registry")
public class RegistryController {

    @Resource
    private RegistryCenter registryCenter;
    
    @Resource
    private RegistryConfig registryConfig;
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("address", registryConfig.getAddress());
        status.put("port", registryConfig.getPort());
        status.put("rootPath", registryConfig.getRootPath());
        status.put("sessionTimeout", registryConfig.getSessionTimeout());
        status.put("autoStart", registryConfig.isAutoStart());
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startRegistry() {
        try {
            registryCenter.start();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "注册中心已启动");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "注册中心启动失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/stop")
    public ResponseEntity<Map<String, String>> stopRegistry() {
        try {
            registryCenter.stop();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "注册中心已停止");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "注册中心停止失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PutMapping("/config")
    public ResponseEntity<Map<String, Object>> updateConfig(@RequestBody RegistryConfig config) {
        // 更新配置
        registryConfig.setAddress(config.getAddress());
        registryConfig.setPort(config.getPort());
        registryConfig.setRootPath(config.getRootPath());
        registryConfig.setSessionTimeout(config.getSessionTimeout());
        registryConfig.setAutoStart(config.isAutoStart());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "配置已更新");
        response.put("config", registryConfig);
        return ResponseEntity.ok(response);
    }
}
