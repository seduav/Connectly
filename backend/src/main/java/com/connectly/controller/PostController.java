package com.connectly.controller;

import com.connectly.dto.request.PostCreateRequest;
import com.connectly.dto.request.PostUpdateRequest;
import com.connectly.dto.response.PostResponse;
import com.connectly.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PostCreateRequest request
    ) {

        PostResponse response = postService.createPost(
                jwt.getSubject(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long postId
    ) {

        PostResponse response =
                postService.getPostById(postId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {

        List<PostResponse> posts =
                postService.getAllPosts();

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(
            @PathVariable Long userId
    ) {

        List<PostResponse> posts =
                postService.getPostsByUser(userId);

        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PostUpdateRequest request
    ) {

        PostResponse response = postService.updatePost(
                postId,
                jwt.getSubject(),
                request
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        postService.deletePost(
                postId,
                jwt.getSubject()
        );

        return ResponseEntity.noContent().build();
    }
}
