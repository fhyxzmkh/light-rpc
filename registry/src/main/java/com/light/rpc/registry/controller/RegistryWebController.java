package com.light.rpc.registry.controller;

import com.light.rpc.registry.config.RegistryCenter;
import com.light.rpc.registry.config.RegistryConfig;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 注册中心Web控制器，提供Thymeleaf页面展示
 */
@Controller
@RequestMapping("/registry")
public class RegistryWebController {

    @Resource
    private RegistryCenter registryCenter;
    
    @Resource
    private RegistryConfig registryConfig;
    
    /**
     * 显示注册中心配置页面
     */
    @GetMapping
    public String showRegistryPage(Model model) {
        model.addAttribute("config", registryConfig);
        model.addAttribute("isRunning", registryCenter.isRunning());
        return "registry/index";
    }
    
    /**
     * 更新注册中心配置
     */
    @PostMapping("/update")
    public String updateConfig(
            @RequestParam("address") String address,
            @RequestParam("port") int port,
            @RequestParam("rootPath") String rootPath,
            @RequestParam("sessionTimeout") int sessionTimeout,
            @RequestParam(value = "autoStart", required = false, defaultValue = "false") boolean autoStart,
            RedirectAttributes redirectAttributes) {
        
        registryConfig.setAddress(address);
        registryConfig.setPort(port);
        registryConfig.setRootPath(rootPath);
        registryConfig.setSessionTimeout(sessionTimeout);
        registryConfig.setAutoStart(autoStart);
        
        redirectAttributes.addFlashAttribute("message", "注册中心配置已更新");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        
        return "redirect:/registry";
    }
    
    /**
     * 启动注册中心
     */
    @PostMapping("/start")
    public String startRegistry(RedirectAttributes redirectAttributes) {
        try {
            registryCenter.start();
            redirectAttributes.addFlashAttribute("message", "注册中心已启动");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "注册中心启动失败: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        
        return "redirect:/registry";
    }
    
    /**
     * 停止注册中心
     */
    @PostMapping("/stop")
    public String stopRegistry(RedirectAttributes redirectAttributes) {
        try {
            registryCenter.stop();
            redirectAttributes.addFlashAttribute("message", "注册中心已停止");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "注册中心停止失败: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        
        return "redirect:/registry";
    }
}
