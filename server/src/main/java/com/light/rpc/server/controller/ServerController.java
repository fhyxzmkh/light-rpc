package com.light.rpc.server.controller;

import com.light.rpc.server.config.ServerConfig;
import com.light.rpc.server.rpcServer.RpcServer;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/server")
public class ServerController {

    @Resource
    private RpcServer rpcServer;
    
    @Resource
    private ServerConfig serverConfig;
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("host", serverConfig.getHost());
        status.put("port", serverConfig.getPort());
        status.put("registryAddress", serverConfig.getRegistryAddress());
        status.put("registryPort", serverConfig.getRegistryPort());
        status.put("autoStart", serverConfig.isAutoStart());
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startServer() {
        try {
            rpcServer.start();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "服务器已启动");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "服务器启动失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/stop")
    public ResponseEntity<Map<String, String>> stopServer() {
        try {
            rpcServer.stop();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "服务器已停止");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "服务器停止失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PutMapping("/config")
    public ResponseEntity<Map<String, Object>> updateConfig(@RequestBody ServerConfig config) {
        // 更新配置
        serverConfig.setHost(config.getHost());
        serverConfig.setPort(config.getPort());
        serverConfig.setRegistryAddress(config.getRegistryAddress());
        serverConfig.setRegistryPort(config.getRegistryPort());
        serverConfig.setAutoStart(config.isAutoStart());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "配置已更新");
        response.put("config", serverConfig);
        return ResponseEntity.ok(response);
    }
}
