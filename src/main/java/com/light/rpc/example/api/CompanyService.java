package com.light.rpc.example.api;

import com.light.rpc.example.model.CompanyInfo;

import java.util.List;

/**
 * 公司信息服务接口
 */
public interface CompanyService {

    /**
     * 获取所有公司信息
     * 
     * @return 公司信息列表
     */
    List<CompanyInfo> listCompanies();
    
    /**
     * 根据ID获取公司信息
     * 
     * @param id 公司ID
     * @return 公司信息
     */
    CompanyInfo getCompanyById(Long id);
    
    /**
     * 根据名称查询公司
     * 
     * @param name 公司名称（支持模糊查询）
     * @return 匹配的公司列表
     */
    List<CompanyInfo> searchByName(String name);
}
