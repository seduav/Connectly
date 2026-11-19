package com.connectly.mapper;

import com.connectly.dto.response.CommentResponse;
import com.connectly.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentResponse toCommentResponse(Comment comment) {

        if (comment == null) {
            return null;
        }

        CommentResponse response = new CommentResponse();

        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        if (comment.getAuthor() != null) {
            response.setAuthorId(comment.getAuthor().getId());
            response.setAuthorUsername(
                    comment.getAuthor().getUsername()
            );
        }

        if (comment.getPost() != null) {
            response.setPostId(comment.getPost().getId());
        }

        return response;
    }
}
