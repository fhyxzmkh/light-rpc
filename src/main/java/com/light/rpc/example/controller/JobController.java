package com.light.rpc.example.controller;

import com.light.rpc.common.annotation.RpcReference;
import com.light.rpc.example.api.JobService;
import com.light.rpc.example.model.JobInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 招聘信息控制器
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    /**
     * 使用RPC引用注解注入JobService
     * 可以根据配置实现本地调用或远程调用
     */
    @RpcReference(version = "1.0")
    private JobService jobService;

    /**
     * 获取所有招聘信息
     */
    @GetMapping
    public List<JobInfo> listAllJobs() {
        return jobService.listJobs();
    }

    /**
     * 根据ID获取招聘信息
     */
    @GetMapping("/{id}")
    public JobInfo getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    /**
     * 添加招聘信息
     */
    @PostMapping
    public JobInfo addJob(@RequestBody JobInfo jobInfo) {
        return jobService.addJob(jobInfo);
    }
}
