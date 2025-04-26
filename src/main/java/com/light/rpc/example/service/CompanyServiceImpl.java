package com.light.rpc.example.service;

import com.light.rpc.common.annotation.RpcService;
import com.light.rpc.example.api.CompanyService;
import com.light.rpc.example.model.CompanyInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 公司信息服务实现类
 */
@Service
@RpcService(value = CompanyService.class, version = "1.0")
public class CompanyServiceImpl implements CompanyService {

    // 模拟数据库存储
    private static final Map<Long, CompanyInfo> COMPANY_DATA = new ConcurrentHashMap<>();
    
    // ID生成器
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    
    // 初始化一些测试数据
    static {
        CompanyInfo company1 = new CompanyInfo(
                ID_GENERATOR.getAndIncrement(),
                "ABC科技有限公司",
                "北京市海淀区中关村软件园",
                "500-1000人",
                "互联网/IT",
                "ABC科技是一家专注于人工智能和大数据技术的创新型科技公司，致力于为企业提供智能化解决方案。",
                "010-12345678",
                "https://www.abc-tech.com"
        );
        
        CompanyInfo company2 = new CompanyInfo(
                ID_GENERATOR.getAndIncrement(),
                "XYZ互联网公司",
                "上海市浦东新区张江高科技园区",
                "1000-5000人",
                "互联网/电子商务",
                "XYZ互联网是中国领先的电子商务平台，为用户提供全面的在线购物体验。",
                "021-87654321",
                "https://www.xyz.com"
        );
        
        CompanyInfo company3 = new CompanyInfo(
                ID_GENERATOR.getAndIncrement(),
                "数据智能科技公司",
                "深圳市南山区科技园",
                "100-500人",
                "大数据/云计算",
                "数据智能科技专注于大数据分析和人工智能技术，为企业提供数据驱动的决策支持。",
                "0755-56781234",
                "https://www.data-intel.com"
        );
        
        COMPANY_DATA.put(company1.getId(), company1);
        COMPANY_DATA.put(company2.getId(), company2);
        COMPANY_DATA.put(company3.getId(), company3);
    }

    @Override
    public List<CompanyInfo> listCompanies() {
        return new ArrayList<>(COMPANY_DATA.values());
    }

    @Override
    public CompanyInfo getCompanyById(Long id) {
        return COMPANY_DATA.get(id);
    }

    @Override
    public List<CompanyInfo> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return listCompanies();
        }
        
        String keyword = name.toLowerCase();
        return COMPANY_DATA.values().stream()
                .filter(company -> company.getName().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }
}
