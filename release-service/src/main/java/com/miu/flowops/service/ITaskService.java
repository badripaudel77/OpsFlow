package com.miu.flowops.service;

public interface ITaskService {
    void startTask(String releaseId, String taskId, String developerId);
    void completeTask(String releaseId, String taskId, String developerId);
}
