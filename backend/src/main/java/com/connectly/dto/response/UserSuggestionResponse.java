package com.connectly.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSuggestionResponse {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String profilePictureUrl;
}