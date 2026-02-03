package com.miu.flowops.controller;

import com.miu.flowops.service.impl.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releases/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    // @PreAuthorize("hasRole('DEVELOPER')")
    @PostMapping("/{releaseId}/devs/{developerId}/start/{taskId}")
    public ResponseEntity<String> startTask(@PathVariable String releaseId,
                                            @PathVariable String developerId,
                                            @PathVariable String taskId
                                           ) {
        taskService.startTask(releaseId, taskId, developerId);
        return ResponseEntity.ok("Task started successfully");
    }

    // @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    @PostMapping("/{releaseId}/devs/{developerId}/complete/{taskId}")
    public ResponseEntity<String> completeTask(@PathVariable String releaseId,
                                               @PathVariable String developerId,
                                               @PathVariable String taskId
                                               ) {
        taskService.completeTask(releaseId, taskId, developerId);
        return ResponseEntity.ok("Task completed successfully");
    }
}
