package com.light.rpc.server.controller;

import com.light.rpc.server.config.ServerConfig;
import com.light.rpc.server.rpcServer.RpcServer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 服务器Web控制器，提供Thymeleaf页面展示
 */
@Controller
@RequestMapping("/server")
public class ServerWebController {

    @Resource
    private RpcServer rpcServer;
    
    @Resource
    private ServerConfig serverConfig;
    
    /**
     * 显示服务器配置页面
     */
    @GetMapping
    public String showServerPage(Model model) {
        model.addAttribute("config", serverConfig);
        model.addAttribute("isRunning", rpcServer.isRunning());
        return "server/index";
    }
    
    /**
     * 更新服务器配置
     */
    @PostMapping("/update")
    public String updateConfig(
            @RequestParam("host") String host,
            @RequestParam("port") int port,
            @RequestParam("registryAddress") String registryAddress,
            @RequestParam("registryPort") int registryPort,
            @RequestParam(value = "autoStart", required = false, defaultValue = "false") boolean autoStart,
            RedirectAttributes redirectAttributes) {
        
        serverConfig.setHost(host);
        serverConfig.setPort(port);
        serverConfig.setRegistryAddress(registryAddress);
        serverConfig.setRegistryPort(registryPort);
        serverConfig.setAutoStart(autoStart);
        
        redirectAttributes.addFlashAttribute("message", "服务器配置已更新");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        
        return "redirect:/server";
    }
    
    /**
     * 启动服务器
     */
    @PostMapping("/start")
    public String startServer(RedirectAttributes redirectAttributes) {
        try {
            rpcServer.start();
            redirectAttributes.addFlashAttribute("message", "服务器已启动");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "服务器启动失败: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        
        return "redirect:/server";
    }
    
    /**
     * 停止服务器
     */
    @PostMapping("/stop")
    public String stopServer(RedirectAttributes redirectAttributes) {
        try {
            rpcServer.stop();
            redirectAttributes.addFlashAttribute("message", "服务器已停止");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "服务器停止失败: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        
        return "redirect:/server";
    }
}
