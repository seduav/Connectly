package com.connectly.service.impl;

import com.connectly.dto.request.PostCreateRequest;
import com.connectly.dto.request.PostUpdateRequest;
import com.connectly.dto.response.PostResponse;
import com.connectly.entity.Post;
import com.connectly.entity.User;
import com.connectly.exception.PostNotFoundException;
import com.connectly.exception.UserNotFoundException;
import com.connectly.mapper.PostMapper;
import com.connectly.repository.PostRepository;
import com.connectly.repository.UserRepository;
import com.connectly.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public PostResponse createPost(
            String authorEmail,
            PostCreateRequest request
    ) {

        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "Authenticated user not found"
                ));

        Post post = new Post();

        post.setContent(request.getContent().trim());
        post.setAuthor(author);

        Post savedPost = postRepository.save(post);

        return postMapper.toPostResponse(savedPost);
    }

    @Override
    public PostResponse getPostById(Long postId) {

        Post post = findPostById(postId);

        return postMapper.toPostResponse(post);
    }

    @Override
    public List<PostResponse> getAllPosts() {

        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(postMapper::toPostResponse)
                .toList();
    }

    @Override
    public List<PostResponse> getPostsByUser(Long userId) {

        return postRepository
                .findByAuthorIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(postMapper::toPostResponse)
                .toList();
    }

    @Override
    @Transactional
    public PostResponse updatePost(
            Long postId,
            String authorEmail,
            PostUpdateRequest request
    ) {

        Post post = findPostByIdAndAuthor(postId, authorEmail);

        post.setContent(request.getContent().trim());

        Post updatedPost = postRepository.save(post);

        return postMapper.toPostResponse(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(
            Long postId,
            String authorEmail
    ) {

        Post post = findPostByIdAndAuthor(postId, authorEmail);

        postRepository.delete(post);
    }

    private Post findPostById(Long postId) {

        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(
                        "Post not found with ID: " + postId
                ));
    }

    private Post findPostByIdAndAuthor(
            Long postId,
            String authorEmail
    ) {

        return postRepository.findByIdAndAuthorEmail(
                        postId,
                        authorEmail
                )
                .orElseThrow(() -> new PostNotFoundException(
                        "Post not found"
                ));
    }
}
