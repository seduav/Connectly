package com.connectly.service.impl;

import com.connectly.dto.response.LikeResponse;
import com.connectly.entity.Post;
import com.connectly.entity.PostLike;
import com.connectly.entity.User;
import com.connectly.exception.PostNotFoundException;
import com.connectly.exception.UserNotFoundException;
import com.connectly.mapper.LikeMapper;
import com.connectly.repository.PostLikeRepository;
import com.connectly.repository.PostRepository;
import com.connectly.repository.UserRepository;
import com.connectly.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeMapper likeMapper;

    @Override
    @Transactional
    public LikeResponse toggleLike(Long postId, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(
                                "Post not found"
                        )
                );

        var existingLike = postLikeRepository
                .findByUserIdAndPostId(user.getId(), postId);

        boolean liked;

        if (existingLike.isPresent()) {

            postLikeRepository.delete(existingLike.get());
            liked = false;

        } else {

            PostLike postLike = new PostLike();
            postLike.setUser(user);
            postLike.setPost(post);

            postLikeRepository.save(postLike);
            liked = true;
        }

        long likesCount = postLikeRepository.countByPostId(postId);

        return likeMapper.toLikeResponse(
                postId,
                liked,
                likesCount
        );
    }

    @Override
    public long getLikesCount(Long postId) {

        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException(
                    "Post not found"
            );
        }

        return postLikeRepository.countByPostId(postId);
    }

    @Override
    public boolean hasUserLikedPost(Long postId, String userEmail) {

        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException(
                    "Post not found"
            );
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        )
                );

        return postLikeRepository.existsByUserIdAndPostId(
                user.getId(),
                postId
        );
    }
}
