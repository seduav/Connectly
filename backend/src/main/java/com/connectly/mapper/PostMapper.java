package com.connectly.mapper;

import com.connectly.dto.response.PostResponse;
import com.connectly.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostResponse toPostResponse(Post post) {

        if (post == null) {
            return null;
        }

        PostResponse response = new PostResponse();

        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());

        if (post.getAuthor() != null) {
            response.setAuthorId(post.getAuthor().getId());
            response.setAuthorUsername(
                    post.getAuthor().getUsername()
            );
        }

        return response;
    }
}
