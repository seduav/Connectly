package com.connectly.controller;

import com.connectly.dto.request.CommentCreateRequest;
import com.connectly.dto.request.CommentUpdateRequest;
import com.connectly.dto.response.CommentResponse;
import com.connectly.service.CommentService;
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
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CommentCreateRequest request
    ) {

        CommentResponse response = commentService.createComment(
                postId,
                jwt.getSubject(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(
            @PathVariable Long postId
    ) {

        List<CommentResponse> comments =
                commentService.getCommentsByPost(postId);

        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {

        CommentResponse response =
                commentService.getCommentByIdAndPost(
                        commentId,
                        postId
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CommentUpdateRequest request
    ) {

        CommentResponse response = commentService.updateComment(
                postId,
                commentId,
                jwt.getSubject(),
                request
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        commentService.deleteComment(
                postId,
                commentId,
                jwt.getSubject()
        );

        return ResponseEntity.noContent().build();
    }
}
