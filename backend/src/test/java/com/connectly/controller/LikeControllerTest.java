package com.connectly.controller;

import com.connectly.entity.Post;
import com.connectly.entity.PostLike;
import com.connectly.entity.User;
import com.connectly.enums.Role;
import com.connectly.repository.PostLikeRepository;
import com.connectly.repository.PostRepository;
import com.connectly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    private User author;
    private User anotherUser;

    private static final String AUTHOR_EMAIL =
            "likeauthor@example.com";

    private static final String ANOTHER_USER_EMAIL =
            "anotherlikeuser@example.com";

    @BeforeEach
    void setUp() {

        postLikeRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        author = createUser(
                "likeauthor",
                AUTHOR_EMAIL
        );

        anotherUser = createUser(
                "anotherlikeuser",
                ANOTHER_USER_EMAIL
        );
    }

    private User createUser(String username, String email) {

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    private Post createPost(User postAuthor, String content) {

        Post post = new Post();

        post.setContent(content);
        post.setAuthor(postAuthor);

        return postRepository.save(post);
    }

    private RequestPostProcessor authenticatedAs(String email) {

        return jwt()
                .jwt(jwt -> jwt.subject(email))
                .authorities(
                        new SimpleGrantedAuthority("ROLE_USER")
                );
    }

    @Test
    void toggleLike_shouldReturnLikedWhenPostIsNotAlreadyLiked()
            throws Exception {

        Post post = createPost(
                author,
                "Post to like"
        );

        mockMvc.perform(
                        post("/api/posts/{postId}/likes", post.getId())
                                .with(authenticatedAs(ANOTHER_USER_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId")
                        .value(post.getId()))
                .andExpect(jsonPath("$.liked")
                        .value(true))
                .andExpect(jsonPath("$.likesCount")
                        .value(1));
    }

    @Test
    void toggleLike_shouldRemoveLikeWhenPostIsAlreadyLiked()
            throws Exception {

        Post post = createPost(
                author,
                "Post to unlike"
        );

        // First request creates the like.
        mockMvc.perform(
                        post("/api/posts/{postId}/likes", post.getId())
                                .with(authenticatedAs(ANOTHER_USER_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true));

        // Second request removes the existing like.
        mockMvc.perform(
                        post("/api/posts/{postId}/likes", post.getId())
                                .with(authenticatedAs(ANOTHER_USER_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId")
                        .value(post.getId()))
                .andExpect(jsonPath("$.liked")
                        .value(false))
                .andExpect(jsonPath("$.likesCount")
                        .value(0));
    }

    @Test
    void toggleLike_shouldKeepOtherUsersLike() throws Exception {

        Post post = createPost(
                author,
                "Post with multiple likes"
        );

        // First user likes the post.
        mockMvc.perform(
                        post("/api/posts/{postId}/likes", post.getId())
                                .with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likesCount").value(1));

        // Second user likes the same post.
        mockMvc.perform(
                        post("/api/posts/{postId}/likes", post.getId())
                                .with(authenticatedAs(ANOTHER_USER_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likesCount").value(2));

        // Second user removes their like.
        mockMvc.perform(
                        post("/api/posts/{postId}/likes", post.getId())
                                .with(authenticatedAs(ANOTHER_USER_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.likesCount").value(1));
    }

    @Test
    void getLikesCount_shouldReturnCorrectCount() throws Exception {

        Post post = createPost(
                author,
                "Post with likes"
        );

        mockMvc.perform(
                        post("/api/posts/{postId}/likes", post.getId())
                                .with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/posts/{postId}/likes", post.getId())
                                .with(authenticatedAs(ANOTHER_USER_EMAIL))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/posts/{postId}/likes/count", post.getId())
                                .with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId")
                        .value(post.getId()))
                .andExpect(jsonPath("$.likesCount")
                        .value(2));
    }

    @Test
    void hasUserLikedPost_shouldReturnTrueWhenUserLikedPost()
            throws Exception {

        Post post = createPost(
                author,
                "Post liked by user"
        );

        mockMvc.perform(
                        post("/api/posts/{postId}/likes", post.getId())
                                .with(authenticatedAs(ANOTHER_USER_EMAIL))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/posts/{postId}/likes/me", post.getId())
                                .with(authenticatedAs(ANOTHER_USER_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true));
    }

    @Test
    void hasUserLikedPost_shouldReturnFalseWhenUserHasNotLikedPost()
            throws Exception {

        Post post = createPost(
                author,
                "Post not liked by user"
        );

        mockMvc.perform(
                        get("/api/posts/{postId}/likes/me", post.getId())
                                .with(authenticatedAs(ANOTHER_USER_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false));
    }

    @Test
    void getLikesCount_shouldReturnNotFoundWhenPostDoesNotExist()
            throws Exception {

        mockMvc.perform(
                        get("/api/posts/{postId}/likes/count", 999999L)
                                .with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void toggleLike_shouldRejectUnauthenticatedRequest()
            throws Exception {

        Post post = createPost(
                author,
                "Post requiring authentication"
        );

        mockMvc.perform(
                        post("/api/posts/{postId}/likes", post.getId())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getLikesCount_shouldRejectUnauthenticatedRequest()
            throws Exception {

        Post post = createPost(
                author,
                "Another protected post"
        );

        mockMvc.perform(
                        get("/api/posts/{postId}/likes/count", post.getId())
                )
                .andExpect(status().isUnauthorized());
    }
}
