package com.light.rpc.example.api;

import com.light.rpc.example.model.JobInfo;

import java.util.List;

/**
 * 招聘服务接口
 */
public interface JobService {

    /**
     * 查询所有招聘信息
     * 
     * @return 招聘信息列表
     */
    List<JobInfo> listJobs();
    
    /**
     * 根据ID查询招聘信息
     * 
     * @param id 招聘信息ID
     * @return 招聘信息
     */
    JobInfo getJobById(Long id);
    
    /**
     * 添加招聘信息
     * 
     * @param jobInfo 招聘信息
     * @return 添加后的招聘信息（含ID）
     */
    JobInfo addJob(JobInfo jobInfo);
}
