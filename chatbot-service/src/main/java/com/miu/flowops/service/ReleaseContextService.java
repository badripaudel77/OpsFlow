package com.miu.flowops.service;

import com.miu.flowops.dto.ReleaseContextDTO;
import com.miu.flowops.dto.TaskContextDTO;
import com.miu.flowops.repository.ReleaseContextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReleaseContextService {
    
    private final ReleaseContextRepository releaseRepository;
    
    public String buildContextForUser(String userId) {
        StringBuilder context = new StringBuilder();
        
        // Get all releases and find tasks assigned to this user
        List<ReleaseContextDTO> allReleases = releaseRepository.findAll();
        log.info("Found {} total releases in database", allReleases.size());
        
        List<TaskContextDTO> userTasks = new ArrayList<>();
        
        for (ReleaseContextDTO release : allReleases) {
            log.debug("Processing release: {} with {} tasks", release.getTitle(), 
                release.getTasks() != null ? release.getTasks().size() : 0);
            if (release.getTasks() != null) {
                for (ReleaseContextDTO.EmbeddedTask task : release.getTasks()) {
                    if (userId.equals(task.getDeveloperId())) {
                        TaskContextDTO taskDTO = new TaskContextDTO();
                        taskDTO.setId(task.getId());
                        taskDTO.setTitle(task.getTitle());
                        taskDTO.setDescription(task.getDescription());
                        taskDTO.setStatus(task.getStatus());
                        taskDTO.setDeveloperId(task.getDeveloperId());
                        taskDTO.setOrderIndex(task.getOrderIndex());
                        taskDTO.setReleaseId(release.getId());
                        userTasks.add(taskDTO);
                    }
                }
            }
        }
        
        if (!userTasks.isEmpty()) {
            context.append("Your current tasks:\n");
            userTasks.forEach(task -> 
                context.append(String.format("- %s (Status: %s)\n", task.getTitle(), task.getStatus())));
            context.append("\n");
        }
        
        // Get active releases summary
        List<ReleaseContextDTO> activeReleases = releaseRepository.findByIsCompletedFalse();
        if (!activeReleases.isEmpty()) {
            context.append("Active releases:\n");
            for (ReleaseContextDTO release : activeReleases) {
                int totalTasks = release.getTasks() != null ? release.getTasks().size() : 0;
                long completedTasks = release.getTasks() != null ? 
                    release.getTasks().stream().filter(t -> "COMPLETED".equals(t.getStatus())).count() : 0;
                context.append(String.format("- %s (%d/%d tasks completed)\n", 
                    release.getTitle(), completedTasks, totalTasks));
            }
            context.append("\n");
        }
        
        // Also include completed releases for context
        List<ReleaseContextDTO> completedReleases = releaseRepository.findByIsCompletedTrue();
        if (!completedReleases.isEmpty()) {
            context.append(String.format("Completed releases: %d\n", completedReleases.size()));
        }
        
        log.info("Built context for user {}: {} chars", userId, context.length());
        log.debug("Context content: {}", context.toString());
        
        return context.toString();
    }
    
    public String buildContextForRelease(String releaseId) {
        Optional<ReleaseContextDTO> release = releaseRepository.findById(releaseId);
        if (release.isEmpty()) {
            return "Release not found.\n";
        }
        
        StringBuilder context = new StringBuilder();
        ReleaseContextDTO releaseDTO = release.get();
        
        context.append(String.format("Release: %s\n", releaseDTO.getTitle()));
        context.append(String.format("Completed: %s\n", releaseDTO.isCompleted() ? "Yes" : "No"));
        
        // Get tasks for this release
        if (releaseDTO.getTasks() != null && !releaseDTO.getTasks().isEmpty()) {
            context.append(String.format("\nTasks (%d total):\n", releaseDTO.getTasks().size()));
            releaseDTO.getTasks().forEach(task -> 
                context.append(String.format("%d. %s - %s (Assigned to: %s)\n", 
                    task.getOrderIndex(), task.getTitle(), task.getStatus(), 
                    task.getDeveloperId() != null ? task.getDeveloperId() : "Unassigned")));
        }
        
        return context.toString();
    }
}
