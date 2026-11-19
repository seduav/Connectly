package com.connectly.repository;

import com.connectly.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository
        extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    List<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    Optional<Comment> findByIdAndAuthorEmail(
            Long commentId,
            String email
    );

    Optional<Comment> findByIdAndPostId(
            Long commentId,
            Long postId
    );

    Optional<Comment> findByIdAndPostIdAndAuthorEmail(
            Long commentId,
            Long postId,
            String email
    );
}
