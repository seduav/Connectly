package com.connectly.mapper;

import com.connectly.dto.response.LikeResponse;
import com.connectly.entity.PostLike;
import org.springframework.stereotype.Component;

@Component
public class LikeMapper {

    public LikeResponse toLikeResponse(
            Long postId,
            boolean liked,
            long likesCount
    ) {

        return new LikeResponse(
                postId,
                liked,
                likesCount
        );
    }
}
