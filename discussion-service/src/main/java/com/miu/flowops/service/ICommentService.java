package com.miu.flowops.service;

import com.miu.flowops.dto.AddCommentRequest;
import com.miu.flowops.dto.UpdateCommentRequest;
import com.miu.flowops.model.Comment;

import java.util.List;

public interface ICommentService {
    
    Comment addComment(String discussionId, AddCommentRequest request);
    
    Comment updateComment(String discussionId, String commentId, UpdateCommentRequest request);
    
    void deleteComment(String discussionId, String commentId, String userId);
    
    List<Comment> getComments(String discussionId);
    
    Comment getComment(String discussionId, String commentId);
}
