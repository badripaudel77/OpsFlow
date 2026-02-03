package com.miu.flowops.service;

import com.miu.flowops.dto.*;
import com.miu.flowops.model.Comment;
import com.miu.flowops.model.Discussion;
import com.miu.flowops.model.DiscussionStatus;

import java.util.List;

public interface IDiscussionService {
    
    Discussion createDiscussion(CreateDiscussionRequest request);
    
    Discussion getDiscussion(String discussionId);
    
    DiscussionResponse getDiscussionWithDetails(String discussionId);
    
    Discussion updateDiscussion(String discussionId, UpdateDiscussionRequest request, String userId);
    
    void deleteDiscussion(String discussionId, String userId);
    
    List<Discussion> getDiscussionsByRelease(String releaseId);
    
    List<Discussion> getDiscussionsByTask(String taskId);
    
    List<Discussion> getDiscussionsByAuthor(String authorId);
    
    List<Discussion> getDiscussionsByStatus(DiscussionStatus status);
    
    Discussion resolveDiscussion(String discussionId, String userId);
    
    Discussion reopenDiscussion(String discussionId, String userId);
    
    Discussion closeDiscussion(String discussionId, String userId);
}
