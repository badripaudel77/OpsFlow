package com.miu.flowops.service;

import com.miu.flowops.model.Release;
import com.miu.flowops.model.Task;

public interface IReleaseService {
    Release createRelease(Release release);
    Release getRelease(String id);
    Task assignDeveloper(String releaseId, String taskId, String developerId);
    void addHotfixTask(String releaseId, Task newTask);
    Release completeRelease(String releaseId);
    void deleteRelease(String releaseId);
}
