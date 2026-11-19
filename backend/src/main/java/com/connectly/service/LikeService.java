package com.connectly.service;

import com.connectly.dto.response.LikeResponse;

public interface LikeService {

    LikeResponse toggleLike(Long postId, String userEmail);

    long getLikesCount(Long postId);

    boolean hasUserLikedPost(Long postId, String userEmail);

}
