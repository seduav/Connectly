package com.connectly.mapper;

import com.connectly.dto.response.LikeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface LikeMapper {

    LikeResponse toLikeResponse(
            Long postId,
            boolean liked,
            long likesCount
    );
}