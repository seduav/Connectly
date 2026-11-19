package com.connectly.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "Post content is required")
    @Size(
            max = 5000,
            message = "Post content cannot exceed 5000 characters"
    )
    private String content;
}
