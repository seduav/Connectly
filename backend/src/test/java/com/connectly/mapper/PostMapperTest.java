package com.connectly.mapper;

import com.connectly.dto.response.PostResponse;
import com.connectly.entity.Post;
import com.connectly.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostMapperTest {

    private final PostMapper mapper = new PostMapper();

    @Test
    void toPostResponse_shouldReturnNullWhenPostIsNull() {
        assertNull(mapper.toPostResponse(null));
    }

    @Test
    void toPostResponse_shouldMapPostWithAuthor() {
        User author = new User();
        author.setId(1L);
        author.setUsername("john");

        Post post = new Post();
        post.setId(10L);
        post.setContent("Hello Connectly!");
        post.setAuthor(author);

        PostResponse response = mapper.toPostResponse(post);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Hello Connectly!", response.getContent());
        assertEquals(1L, response.getAuthorId());
        assertEquals("john", response.getAuthorUsername());
    }

    @Test
    void toPostResponse_shouldMapPostWithoutAuthor() {
        Post post = new Post();
        post.setId(20L);
        post.setContent("Post without author");
        post.setAuthor(null);

        PostResponse response = mapper.toPostResponse(post);

        assertNotNull(response);
        assertEquals(20L, response.getId());
        assertEquals("Post without author", response.getContent());
        assertNull(response.getAuthorId());
        assertNull(response.getAuthorUsername());
    }
}
