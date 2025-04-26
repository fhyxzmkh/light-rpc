package com.light.rpc.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 招聘信息模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 职位ID
     */
    private Long id;
    
    /**
     * 职位名称
     */
    private String title;
    
    /**
     * 公司名称
     */
    private String company;
    
    /**
     * 工作地点
     */
    private String location;
    
    /**
     * 薪资范围
     */
    private String salary;
    
    /**
     * 职位描述
     */
    private String description;
    
    /**
     * 联系方式
     */
    private String contact;
    
    /**
     * 发布时间
     */
    private Date publishTime;
}
