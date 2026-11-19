package com.connectly.service;

import com.connectly.dto.request.PostCreateRequest;
import com.connectly.dto.request.PostUpdateRequest;
import com.connectly.dto.response.PostResponse;

import java.util.List;

public interface PostService {

    PostResponse createPost(
            String authorEmail,
            PostCreateRequest request
    );

    PostResponse getPostById(Long postId);

    List<PostResponse> getAllPosts();

    List<PostResponse> getPostsByUser(Long userId);

    PostResponse updatePost(
            Long postId,
            String authorEmail,
            PostUpdateRequest request
    );

    void deletePost(
            Long postId,
            String authorEmail
    );
}
