package com.light.rpc.example.controller;

import com.light.rpc.common.annotation.RpcReference;
import com.light.rpc.example.api.CompanyService;
import com.light.rpc.example.model.CompanyInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公司信息控制器
 */
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    /**
     * 使用RPC引用注解注入CompanyService
     * 可以根据配置实现本地调用或远程调用
     */
    @RpcReference(version = "1.0")
    private CompanyService companyService;

    /**
     * 获取所有公司信息
     */
    @GetMapping
    public List<CompanyInfo> listAllCompanies() {
        return companyService.listCompanies();
    }

    /**
     * 根据ID获取公司信息
     */
    @GetMapping("/{id}")
    public CompanyInfo getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id);
    }

    /**
     * 根据名称搜索公司
     */
    @GetMapping("/search")
    public List<CompanyInfo> searchCompanies(@RequestParam(required = false) String name) {
        return companyService.searchByName(name);
    }
}
