package com.connectly.controller;

import com.connectly.entity.Comment;
import com.connectly.entity.Post;
import com.connectly.entity.User;
import com.connectly.enums.Role;
import com.connectly.repository.CommentRepository;
import com.connectly.repository.PostRepository;
import com.connectly.repository.UserRepository;
import com.connectly.util.TestJsonLoader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User author;
    private User anotherUser;
    private Post post;

    private static final String AUTHOR_EMAIL =
            "commentauthor@example.com";

    private static final String ANOTHER_USER_EMAIL =
            "anothercommentuser@example.com";

    @BeforeEach
    void setUp() {

        author = createUser(
                "commentauthor",
                AUTHOR_EMAIL
        );

        anotherUser = createUser(
                "anothercommentuser",
                ANOTHER_USER_EMAIL
        );

        post = createPost(
                author,
                "Post for comment testing"
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

        Post newPost = new Post();

        newPost.setContent(content);
        newPost.setAuthor(postAuthor);

        return postRepository.save(newPost);
    }

    private Comment createComment(
            User commentAuthor,
            Post targetPost,
            String content
    ) {

        Comment comment = new Comment();

        comment.setContent(content);
        comment.setAuthor(commentAuthor);
        comment.setPost(targetPost);

        return commentRepository.save(comment);
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
    void createComment_shouldReturnCreated() throws Exception {

        String request = TestJsonLoader.load(
                "testdata/comments/create-comment.json"
        );

        mockMvc.perform(
                        post("/api/posts/{postId}/comments", post.getId())
                                .with(authenticatedAs(AUTHOR_EMAIL))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.content")
                        .value("My first comment"))
                .andExpect(jsonPath("$.authorId")
                        .value(author.getId()))
                .andExpect(jsonPath("$.authorUsername")
                        .value("commentauthor"))
                .andExpect(jsonPath("$.postId")
                        .value(post.getId()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void getCommentsByPost_shouldReturnComments() throws Exception {

        createComment(author, post, "First comment");
        createComment(anotherUser, post, "Second comment");

        mockMvc.perform(
                        get("/api/posts/{postId}/comments", post.getId())
                                .with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content")
                        .value("First comment"))
                .andExpect(jsonPath("$[1].content")
                        .value("Second comment"));
    }

    @Test
    void getCommentById_shouldReturnComment() throws Exception {

        Comment comment = createComment(
                author,
                post,
                "Comment to retrieve"
        );

        mockMvc.perform(
                        get(
                                "/api/posts/{postId}/comments/{commentId}",
                                post.getId(),
                                comment.getId()
                        ).with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(comment.getId()))
                .andExpect(jsonPath("$.content")
                        .value("Comment to retrieve"))
                .andExpect(jsonPath("$.postId")
                        .value(post.getId()));
    }

    @Test
    void updateComment_shouldReturnUpdatedComment() throws Exception {

        Comment comment = createComment(
                author,
                post,
                "Original comment"
        );

        String request = TestJsonLoader.load(
                "testdata/comments/update-comment.json"
        );

        mockMvc.perform(
                        put(
                                "/api/posts/{postId}/comments/{commentId}",
                                post.getId(),
                                comment.getId()
                        )
                                .with(authenticatedAs(AUTHOR_EMAIL))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(comment.getId()))
                .andExpect(jsonPath("$.content")
                        .value("Updated comment"));
    }

    @Test
    void deleteComment_shouldReturnNoContent() throws Exception {

        Comment comment = createComment(
                author,
                post,
                "Comment to delete"
        );

        mockMvc.perform(
                        delete(
                                "/api/posts/{postId}/comments/{commentId}",
                                post.getId(),
                                comment.getId()
                        ).with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isNoContent());

        assertFalse(
                commentRepository.existsById(comment.getId())
        );
    }

    @Test
    void createComment_shouldReturnBadRequestWhenContentIsInvalid()
            throws Exception {

        String request = """
                {
                    "content": "   "
                }
                """;

        mockMvc.perform(
                        post("/api/posts/{postId}/comments", post.getId())
                                .with(authenticatedAs(AUTHOR_EMAIL))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_shouldReturnBadRequestWhenContentExceedsLimit()
            throws Exception {

        String content = "a".repeat(2001);

        String request = """
                {
                    "content": "%s"
                }
                """.formatted(content);

        mockMvc.perform(
                        post("/api/posts/{postId}/comments", post.getId())
                                .with(authenticatedAs(AUTHOR_EMAIL))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCommentsByPost_shouldReturnNotFoundWhenPostDoesNotExist()
            throws Exception {

        mockMvc.perform(
                        get("/api/posts/{postId}/comments", 999999L)
                                .with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getCommentById_shouldReturnNotFoundWhenCommentDoesNotExist()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/posts/{postId}/comments/{commentId}",
                                post.getId(),
                                999999L
                        ).with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateComment_shouldReturnNotFoundWhenUserDoesNotOwnComment()
            throws Exception {

        Comment comment = createComment(
                anotherUser,
                post,
                "Another user's comment"
        );

        String request = TestJsonLoader.load(
                "testdata/comments/update-comment.json"
        );

        mockMvc.perform(
                        put(
                                "/api/posts/{postId}/comments/{commentId}",
                                post.getId(),
                                comment.getId()
                        )
                                .with(authenticatedAs(AUTHOR_EMAIL))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getCommentsByPost_shouldRejectUnauthenticatedRequest()
            throws Exception {

        mockMvc.perform(
                        get("/api/posts/{postId}/comments", post.getId())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createComment_shouldRejectUnauthenticatedRequest()
            throws Exception {

        String request = TestJsonLoader.load(
                "testdata/comments/create-comment.json"
        );

        mockMvc.perform(
                        post("/api/posts/{postId}/comments", post.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCommentById_shouldReturnNotFoundWhenCommentBelongsToAnotherPost()
            throws Exception {

        Post anotherPost = createPost(
                anotherUser,
                "Another post"
        );

        Comment comment = createComment(
                author,
                anotherPost,
                "Comment on another post"
        );

        mockMvc.perform(
                        get(
                                "/api/posts/{postId}/comments/{commentId}",
                                post.getId(),
                                comment.getId()
                        ).with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateComment_shouldReturnBadRequestWhenContentIsBlank()
            throws Exception {

        Comment comment = createComment(
                author,
                post,
                "Original comment"
        );

        String request = """
                {
                    "content": "   "
                }
                """;

        mockMvc.perform(
                        put(
                                "/api/posts/{postId}/comments/{commentId}",
                                post.getId(),
                                comment.getId()
                        )
                                .with(authenticatedAs(AUTHOR_EMAIL))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteComment_shouldAllowPostOwnerToDeleteAnotherUsersComment()
            throws Exception {

        Comment comment = createComment(
                anotherUser,
                post,
                "Another user's comment"
        );

        mockMvc.perform(
                        delete(
                                "/api/posts/{postId}/comments/{commentId}",
                                post.getId(),
                                comment.getId()
                        ).with(authenticatedAs(AUTHOR_EMAIL))
                )
                .andExpect(status().isNoContent());

        assertFalse(
                commentRepository.existsById(comment.getId())
        );
    }

    @Test
    void deleteComment_shouldReturnNotFoundWhenUserDoesNotOwnComment()
            throws Exception {

        createUser(
                "thirduser",
                "thirduser@example.com"
        );

        Comment comment = createComment(
                anotherUser,
                post,
                "Another user's comment"
        );

        mockMvc.perform(
                        delete(
                                "/api/posts/{postId}/comments/{commentId}",
                                post.getId(),
                                comment.getId()
                        ).with(authenticatedAs("thirduser@example.com"))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        assertFalse(
                !commentRepository.existsById(comment.getId())
        );
    }
}
