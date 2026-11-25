package com.connectly.service.impl;

import com.connectly.dto.request.CommentCreateRequest;
import com.connectly.dto.request.CommentUpdateRequest;
import com.connectly.dto.response.CommentResponse;
import com.connectly.entity.Comment;
import com.connectly.entity.Post;
import com.connectly.entity.User;
import com.connectly.exception.PostNotFoundException;
import com.connectly.exception.UserNotFoundException;
import com.connectly.mapper.CommentMapper;
import com.connectly.repository.CommentRepository;
import com.connectly.repository.PostRepository;
import com.connectly.repository.UserRepository;
import com.connectly.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentResponse createComment(
            Long postId,
            String authorEmail,
            CommentCreateRequest request
    ) {

        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "Authenticated user not found"
                ));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(
                        "Post not found"
                ));

        Comment comment = new Comment();

        comment.setContent(request.getContent().trim());
        comment.setAuthor(author);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toCommentResponse(savedComment);
    }

    @Override
    public List<CommentResponse> getCommentsByPost(Long postId) {

        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException(
                    "Post not found"
            );
        }

        return commentRepository
                .findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(commentMapper::toCommentResponse)
                .toList();
    }

    @Override
    public List<CommentResponse> getCommentsByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "User not found"
            );
        }

        return commentRepository
                .findByAuthorIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(commentMapper::toCommentResponse)
                .toList();
    }

    @Override
    @Transactional
    public CommentResponse updateComment(
            Long postId,
            Long commentId,
            String authorEmail,
            CommentUpdateRequest request
    ) {

        Comment comment = commentRepository
                .findByIdAndPostIdAndAuthorEmail(
                        commentId,
                        postId,
                        authorEmail
                )
                .orElseThrow(() -> new PostNotFoundException(
                        "Comment not found"
                ));

        comment.setContent(request.getContent().trim());

        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toCommentResponse(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(
            Long postId,
            Long commentId,
            String authorEmail
    ) {

        Comment comment = commentRepository
                .findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new PostNotFoundException(
                        "Comment not found"
                ));

        boolean isCommentAuthor = comment.getAuthor()
                .getEmail()
                .equals(authorEmail);

        boolean isPostAuthor = comment.getPost()
                .getAuthor()
                .getEmail()
                .equals(authorEmail);

        if (!isCommentAuthor && !isPostAuthor) {
            throw new PostNotFoundException("Comment not found");
        }

        commentRepository.delete(comment);
    }

    private Comment findCommentById(Long commentId) {

        return commentRepository.findById(commentId)
                .orElseThrow(() -> new PostNotFoundException(
                        "Comment not found"
                ));
    }

    private Comment findCommentByIdAndAuthor(
            Long commentId,
            String authorEmail
    ) {

        return commentRepository.findByIdAndAuthorEmail(
                        commentId,
                        authorEmail
                )
                .orElseThrow(() -> new PostNotFoundException(
                        "Comment not found"
                ));
    }

    @Override
    public CommentResponse getCommentByIdAndPost(
            Long commentId,
            Long postId
    ) {

        Comment comment = commentRepository
                .findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new PostNotFoundException(
                        "Comment not found"
                ));

        return commentMapper.toCommentResponse(comment);
    }
}
