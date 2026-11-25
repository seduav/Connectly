package com.connectly.mapper;

import com.connectly.dto.response.LikeResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class LikeMapperTest {

    private final LikeMapper mapper =
            Mappers.getMapper(LikeMapper.class);

    @Test
    void toLikeResponse_shouldMapLikedResponse() {
        LikeResponse response = mapper.toLikeResponse(10L, true, 25L);

        assertNotNull(response);
        assertEquals(10L, response.getPostId());
        assertTrue(response.isLiked());
        assertEquals(25L, response.getLikesCount());
    }

    @Test
    void toLikeResponse_shouldMapUnlikedResponse() {
        LikeResponse response = mapper.toLikeResponse(10L, false, 24L);

        assertNotNull(response);
        assertEquals(10L, response.getPostId());
        assertFalse(response.isLiked());
        assertEquals(24L, response.getLikesCount());
    }

    @Test
    void toLikeResponse_shouldSupportZeroLikes() {
        LikeResponse response = mapper.toLikeResponse(10L, false, 0L);

        assertNotNull(response);
        assertEquals(0L, response.getLikesCount());
    }

    @Test
    void toLikeResponse_shouldSupportNullPostId() {
        LikeResponse response = mapper.toLikeResponse(null, false, 0L);

        assertNotNull(response);
        assertNull(response.getPostId());
    }
}
