package com.connectly.service;

import com.connectly.dto.request.CommentCreateRequest;
import com.connectly.dto.request.CommentUpdateRequest;
import com.connectly.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    CommentResponse createComment(
            Long postId,
            String authorEmail,
            CommentCreateRequest request
    );

    CommentResponse getCommentByIdAndPost(
            Long commentId,
            Long postId
    );

    List<CommentResponse> getCommentsByPost(Long postId);

    List<CommentResponse> getCommentsByUser(Long userId);

    CommentResponse updateComment(
            Long postId,
            Long commentId,
            String authorEmail,
            CommentUpdateRequest request
    );

    void deleteComment(
            Long postId,
            Long commentId,
            String authorEmail
    );
}
