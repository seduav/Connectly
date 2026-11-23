package com.connectly.mapper;

import com.connectly.dto.response.CommentResponse;
import com.connectly.entity.Comment;
import com.connectly.entity.Post;
import com.connectly.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private final CommentMapper mapper = new CommentMapper();

    @Test
    void toCommentResponse_shouldReturnNullWhenCommentIsNull() {
        assertNull(mapper.toCommentResponse(null));
    }

    @Test
    void toCommentResponse_shouldMapCommentWithAuthorAndPost() {
        User author = new User();
        author.setId(1L);
        author.setUsername("john");

        Post post = new Post();
        post.setId(10L);

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setContent("Great post!");
        comment.setAuthor(author);
        comment.setPost(post);

        CommentResponse response = mapper.toCommentResponse(comment);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Great post!", response.getContent());
        assertEquals(1L, response.getAuthorId());
        assertEquals("john", response.getAuthorUsername());
        assertEquals(10L, response.getPostId());
    }

    @Test
    void toCommentResponse_shouldMapCommentWithoutAuthor() {
        Comment comment = new Comment();
        comment.setId(100L);
        comment.setContent("Anonymous comment");
        comment.setAuthor(null);

        Post post = new Post();
        post.setId(10L);
        comment.setPost(post);

        CommentResponse response = mapper.toCommentResponse(comment);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertNull(response.getAuthorId());
        assertNull(response.getAuthorUsername());
        assertEquals(10L, response.getPostId());
    }

    @Test
    void toCommentResponse_shouldMapCommentWithoutPost() {
        User author = new User();
        author.setId(1L);
        author.setUsername("john");

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setContent("Comment without post");
        comment.setAuthor(author);
        comment.setPost(null);

        CommentResponse response = mapper.toCommentResponse(comment);

        assertNotNull(response);
        assertEquals(1L, response.getAuthorId());
        assertEquals("john", response.getAuthorUsername());
        assertNull(response.getPostId());
    }

    @Test
    void toCommentResponse_shouldMapCommentWithoutAuthorAndPost() {
        Comment comment = new Comment();
        comment.setId(100L);
        comment.setAuthor(null);
        comment.setPost(null);

        CommentResponse response = mapper.toCommentResponse(comment);

        assertNotNull(response);
        assertNull(response.getAuthorId());
        assertNull(response.getAuthorUsername());
        assertNull(response.getPostId());
    }
}
