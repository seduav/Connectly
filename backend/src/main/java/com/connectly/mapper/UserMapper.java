package com.connectly.mapper;

import com.connectly.dto.response.*;
import com.connectly.entity.Profile;
import com.connectly.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    // Profile mapping
    ProfileResponse toProfileResponse(Profile profile);


    // User mapping
    UserResponse toUserResponse(User user);


    // Registration mapping
    @Mapping(
            target = "message",
            constant = "Registration successful"
    )
    RegistrationResponse toRegistrationResponse(User user);


    // Current authenticated user mapping
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "bio", source = "profile.bio")
    @Mapping(
            target = "profilePictureUrl",
            source = "profile.profilePictureUrl"
    )
    @Mapping(target = "dateOfBirth", source = "profile.dateOfBirth")
    CurrentUserResponse toCurrentUserResponse(User user);


    // User suggestions mapping
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(
            target = "profilePictureUrl",
            source = "profile.profilePictureUrl"
    )
    UserSuggestionResponse toUserSuggestionResponse(User user);
}