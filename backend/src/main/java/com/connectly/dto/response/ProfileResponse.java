package com.connectly.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private String firstName;

    private String lastName;

    private String bio;

    private String profilePictureUrl;

    private LocalDate dateOfBirth;
}
