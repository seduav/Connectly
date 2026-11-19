package com.connectly.controller;

import com.connectly.entity.Post;
import com.connectly.entity.User;
import com.connectly.enums.Role;
import com.connectly.repository.PostRepository;
import com.connectly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private User author;
    private User anotherUser;

    private static final String AUTHOR_EMAIL =
            "postauthor@example.com";

    private static final String ANOTHER_USER_EMAIL =
            "anotheruser@example.com";

    @BeforeEach
    void setUp() {

        postRepository.deleteAll();
        userRepository.deleteAll();

        author = createUser(
                "postauthor",
                AUTHOR_EMAIL
        );

        anotherUser = createUser(
                "anotheruser",
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

    private org.springframework.test.web.servlet.request
            .RequestPostProcessor authenticatedAs(String email) {

        return jwt()
                .jwt(jwt -> jwt.subject(email))
                .authorities(
                        new SimpleGrantedAuthority("ROLE_USER")
                );
    }

    @Test
    void createPost_shouldReturnCreated() throws Exception {

        String request = """
                {
                    "content": "My first Connectly post"
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .with(authenticatedAs(AUTHOR_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.content")
                        .value("My first Connectly post"))
                .andExpect(jsonPath("$.authorId")
                        .value(author.getId()))
                .andExpect(jsonPath("$.authorUsername")
                        .value("postauthor"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void getPostById_shouldReturnPost() throws Exception {

        Post post = createPost(
                author,
                "Post to retrieve"
        );

        mockMvc.perform(get("/api/posts/{postId}", post.getId())
                        .with(authenticatedAs(AUTHOR_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(post.getId()))
                .andExpect(jsonPath("$.content")
                        .value("Post to retrieve"))
                .andExpect(jsonPath("$.authorUsername")
                        .value("postauthor"));
    }

    @Test
    void getAllPosts_shouldReturnPosts() throws Exception {

        createPost(author, "First post");
        createPost(anotherUser, "Second post");

        mockMvc.perform(get("/api/posts")
                        .with(authenticatedAs(AUTHOR_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getPostsByUser_shouldReturnUserPosts() throws Exception {

        createPost(author, "Author first post");
        createPost(author, "Author second post");
        createPost(anotherUser, "Another user's post");

        mockMvc.perform(
                        get("/api/posts/user/{userId}", author.getId())
                                .with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updatePost_shouldReturnUpdatedPost() throws Exception {

        Post post = createPost(
                author,
                "Original content"
        );

        String request = """
                {
                    "content": "Updated content"
                }
                """;

        mockMvc.perform(put("/api/posts/{postId}", post.getId())
                        .with(authenticatedAs(AUTHOR_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(post.getId()))
                .andExpect(jsonPath("$.content")
                        .value("Updated content"));
    }

    @Test
    void deletePost_shouldReturnNoContent() throws Exception {

        Post post = createPost(
                author,
                "Post to delete"
        );

        mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                        .with(authenticatedAs(AUTHOR_EMAIL)))
                .andExpect(status().isNoContent());

        org.junit.jupiter.api.Assertions.assertFalse(
                postRepository.existsById(post.getId())
        );
    }

    @Test
    void createPost_shouldReturnBadRequestWhenContentIsInvalid()
            throws Exception {

        String request = """
                {
                    "content": "   "
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .with(authenticatedAs(AUTHOR_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPostById_shouldReturnNotFoundWhenPostDoesNotExist()
            throws Exception {

        mockMvc.perform(get("/api/posts/{postId}", 999999L)
                        .with(authenticatedAs(AUTHOR_EMAIL)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updatePost_shouldReturnNotFoundWhenUserDoesNotOwnPost()
            throws Exception {

        Post post = createPost(
                anotherUser,
                "Another user's post"
        );

        String request = """
                {
                    "content": "Attempted unauthorized update"
                }
                """;

        mockMvc.perform(put("/api/posts/{postId}", post.getId())
                        .with(authenticatedAs(AUTHOR_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deletePost_shouldReturnNotFoundWhenUserDoesNotOwnPost()
            throws Exception {

        Post post = createPost(
                anotherUser,
                "Another user's post"
        );

        mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                        .with(authenticatedAs(AUTHOR_EMAIL)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getAllPosts_shouldRejectUnauthenticatedRequest()
            throws Exception {

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPost_shouldRejectUnauthenticatedRequest()
            throws Exception {

        String request = """
                {
                    "content": "Unauthenticated post"
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized());
    }
}
