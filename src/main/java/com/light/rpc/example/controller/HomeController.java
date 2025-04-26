package com.light.rpc.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 主页控制器
 */
@Controller
public class HomeController {

    /**
     * 重定向到静态首页
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
}
