package com.miu.flowops.service;

import com.miu.flowops.dto.ReleaseContextDTO;
import com.miu.flowops.dto.TaskContextDTO;
import com.miu.flowops.repository.ReleaseContextRepository;
import com.miu.flowops.repository.TaskContextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReleaseContextService {
    
    private final ReleaseContextRepository releaseRepository;
    private final TaskContextRepository taskRepository;
    
    public String buildContextForUser(String userId) {
        StringBuilder context = new StringBuilder();
        
        // Get user's tasks
        List<TaskContextDTO> userTasks = taskRepository.findByAssignedTo(userId);
        if (!userTasks.isEmpty()) {
            context.append("Your current tasks:\n");
            userTasks.forEach(task -> 
                context.append(String.format("- %s (Status: %s)\n", task.getTitle(), task.getStatus())));
            context.append("\n");
        }
        
        // Get active releases summary
        List<ReleaseContextDTO> activeReleases = releaseRepository.findByCompletedFalse();
        if (!activeReleases.isEmpty()) {
            context.append("Active releases: ");
            context.append(activeReleases.size());
            context.append("\n");
        }
        
        return context.toString();
    }
    
    public String buildContextForRelease(String releaseId) {
        Optional<ReleaseContextDTO> release = releaseRepository.findById(releaseId);
        if (release.isEmpty()) {
            return "Release not found.\n";
        }
        
        StringBuilder context = new StringBuilder();
        ReleaseContextDTO releaseDTO = release.get();
        
        context.append(String.format("Release: %s\n", releaseDTO.getName()));
        context.append(String.format("Status: %s\n", releaseDTO.getStatus()));
        context.append(String.format("Completed: %s\n", releaseDTO.isCompleted() ? "Yes" : "No"));
        
        // Get tasks for this release
        List<TaskContextDTO> tasks = taskRepository.findByReleaseIdOrderByOrderIndexAsc(releaseId);
        if (!tasks.isEmpty()) {
            context.append(String.format("\nTasks (%d total):\n", tasks.size()));
            tasks.forEach(task -> 
                context.append(String.format("%d. %s - %s (Assigned to: %s)\n", 
                    task.getOrderIndex(), task.getTitle(), task.getStatus(), 
                    task.getAssignedTo() != null ? task.getAssignedTo() : "Unassigned")));
        }
        
        return context.toString();
    }
    
    public String buildContextForTask(String taskId) {
        Optional<TaskContextDTO> task = taskRepository.findById(taskId);
        if (task.isEmpty()) {
            return "Task not found.\n";
        }
        
        TaskContextDTO taskDTO = task.get();
        StringBuilder context = new StringBuilder();
        
        context.append(String.format("Task: %s\n", taskDTO.getTitle()));
        context.append(String.format("Status: %s\n", taskDTO.getStatus()));
        context.append(String.format("Description: %s\n", taskDTO.getDescription()));
        context.append(String.format("Assigned to: %s\n", 
            taskDTO.getAssignedTo() != null ? taskDTO.getAssignedTo() : "Unassigned"));
        context.append(String.format("Order: %d\n", taskDTO.getOrderIndex()));
        
        return context.toString();
    }
}
