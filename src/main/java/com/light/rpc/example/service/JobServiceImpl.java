package com.light.rpc.example.service;

import com.light.rpc.common.annotation.RpcService;
import com.light.rpc.example.api.JobService;
import com.light.rpc.example.model.JobInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 招聘服务实现类
 */
@Service
@RpcService(value = JobService.class, version = "1.0")
public class JobServiceImpl implements JobService {

    // 模拟数据库存储
    private static final Map<Long, JobInfo> JOB_DATA = new ConcurrentHashMap<>();
    
    // ID生成器
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    
    // 初始化一些测试数据
    static {
        JobInfo job1 = new JobInfo(
                ID_GENERATOR.getAndIncrement(),
                "Java高级开发工程师",
                "ABC科技有限公司",
                "北京市海淀区",
                "25k-35k",
                "1. 负责公司核心业务系统的设计和开发\n2. 解决系统中的技术难题\n3. 优化系统性能",
                "hr@abc.com",
                new Date()
        );
        
        JobInfo job2 = new JobInfo(
                ID_GENERATOR.getAndIncrement(),
                "前端开发工程师",
                "XYZ互联网公司",
                "上海市浦东新区",
                "20k-30k",
                "1. 负责公司Web前端开发\n2. 优化用户体验\n3. 与后端团队协作开发",
                "hr@xyz.com",
                new Date()
        );
        
        JobInfo job3 = new JobInfo(
                ID_GENERATOR.getAndIncrement(),
                "大数据工程师",
                "数据智能科技公司",
                "深圳市南山区",
                "30k-45k",
                "1. 设计和开发大数据处理平台\n2. 实现数据挖掘和分析算法\n3. 优化数据处理流程",
                "hr@data.com",
                new Date()
        );
        
        JOB_DATA.put(job1.getId(), job1);
        JOB_DATA.put(job2.getId(), job2);
        JOB_DATA.put(job3.getId(), job3);
    }

    @Override
    public List<JobInfo> listJobs() {
        // 返回所有招聘信息列表
        return new ArrayList<>(JOB_DATA.values());
    }

    @Override
    public JobInfo getJobById(Long id) {
        // 根据ID查询招聘信息
        return JOB_DATA.get(id);
    }

    @Override
    public JobInfo addJob(JobInfo jobInfo) {
        // 设置ID和发布时间
        jobInfo.setId(ID_GENERATOR.getAndIncrement());
        if (jobInfo.getPublishTime() == null) {
            jobInfo.setPublishTime(new Date());
        }
        
        // 保存招聘信息
        JOB_DATA.put(jobInfo.getId(), jobInfo);
        
        return jobInfo;
    }
}
