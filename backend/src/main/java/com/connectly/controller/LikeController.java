package com.connectly.controller;

import com.connectly.dto.response.LikeResponse;
import com.connectly.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        LikeResponse response = likeService.toggleLike(
                postId,
                jwt.getSubject()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getLikesCount(
            @PathVariable Long postId
    ) {

        long count = likeService.getLikesCount(postId);

        return ResponseEntity.ok(
                Map.of("postId", postId, "likesCount", count)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Boolean>> hasUserLikedPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        boolean liked = likeService.hasUserLikedPost(
                postId,
                jwt.getSubject()
        );

        return ResponseEntity.ok(
                Map.of("liked", liked)
        );
    }
}
