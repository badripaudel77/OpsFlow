package com.miu.flowops.controller;

import com.miu.flowops.model.Release;
import com.miu.flowops.model.Task;
import com.miu.flowops.service.impl.ReleaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/releases")
@RequiredArgsConstructor
public class ReleaseController {

    private final ReleaseService releaseService;

    @PostMapping
    public ResponseEntity<Release> createRelease(@RequestBody Release release) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(releaseService.createRelease(release));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Release> getRelease(@PathVariable String id) {
        return ResponseEntity
                .ok(releaseService.getRelease(id));
    }

    @PostMapping("/{releaseId}/tasks/{taskId}/assign/{developerId}")
    public ResponseEntity<Task> assignDeveloper(@PathVariable String releaseId,
                                                @PathVariable String taskId,
                                                @PathVariable String developerId) {
        Task response = releaseService.assignDeveloper(releaseId, taskId, developerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{releaseId}/hotfix")
    public ResponseEntity<String> addHotfixTask(@PathVariable String releaseId,
                                                @RequestBody Task task) {
        releaseService.addHotfixTask(releaseId, task);
        return ResponseEntity.ok("Hotfix task added successfully");
    }
}
