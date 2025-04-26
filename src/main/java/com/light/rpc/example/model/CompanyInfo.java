package com.light.rpc.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 公司信息模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 公司ID
     */
    private Long id;
    
    /**
     * 公司名称
     */
    private String name;
    
    /**
     * 公司地址
     */
    private String address;
    
    /**
     * 公司规模（人数）
     */
    private String scale;
    
    /**
     * 公司类型（如：互联网、金融等）
     */
    private String industry;
    
    /**
     * 公司简介
     */
    private String description;
    
    /**
     * 联系方式
     */
    private String contact;
    
    /**
     * 公司网站
     */
    private String website;
}
