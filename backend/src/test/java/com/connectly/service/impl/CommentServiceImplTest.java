package com.connectly.service.impl;

import com.connectly.dto.request.CommentCreateRequest;
import com.connectly.dto.request.CommentUpdateRequest;
import com.connectly.dto.response.CommentResponse;
import com.connectly.entity.Comment;
import com.connectly.entity.Post;
import com.connectly.entity.User;
import com.connectly.exception.PostNotFoundException;
import com.connectly.exception.UserNotFoundException;
import com.connectly.mapper.CommentMapper;
import com.connectly.repository.CommentRepository;
import com.connectly.repository.PostRepository;
import com.connectly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private User anotherUser;
    private Post post;
    private Post anotherPost;
    private Comment comment;
    private CommentResponse commentResponse;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");

        post = new Post();
        post.setId(1L);
        post.setContent("Test post content");
        post.setAuthor(user);

        anotherPost = new Post();
        anotherPost.setId(2L);
        anotherPost.setContent("Another post");
        anotherPost.setAuthor(anotherUser);

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test comment");
        comment.setAuthor(user);
        comment.setPost(post);

        commentResponse = new CommentResponse();
        commentResponse.setId(1L);
        commentResponse.setContent("Test comment");
        commentResponse.setAuthorId(1L);
        commentResponse.setAuthorUsername("testuser");
        commentResponse.setPostId(1L);
    }

    @Test
    void createComment_shouldCreateCommentSuccessfully() {

        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("  Test comment  ");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        when(commentMapper.toCommentResponse(comment))
                .thenReturn(commentResponse);

        CommentResponse result = commentService.createComment(
                1L,
                "test@example.com",
                request
        );

        assertNotNull(result);
        assertEquals("Test comment", result.getContent());

        verify(commentRepository).save(argThat(saved ->
                saved.getContent().equals("Test comment")
                        && saved.getAuthor().equals(user)
                        && saved.getPost().equals(post)
        ));

        verify(commentMapper).toCommentResponse(comment);
    }

    @Test
    void createComment_shouldThrowExceptionWhenUserDoesNotExist() {

        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("Test comment");

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> commentService.createComment(
                        1L,
                        "unknown@example.com",
                        request
                )
        );

        verify(commentRepository, never()).save(any());
        verifyNoInteractions(commentMapper);
    }

    @Test
    void createComment_shouldThrowExceptionWhenPostDoesNotExist() {

        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("Test comment");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                PostNotFoundException.class,
                () -> commentService.createComment(
                        99L,
                        "test@example.com",
                        request
                )
        );

        verify(commentRepository, never()).save(any());
        verifyNoInteractions(commentMapper);
    }

    @Test
    void getCommentsByPost_shouldReturnPostComments() {

        when(postRepository.existsById(1L))
                .thenReturn(true);

        when(commentRepository.findByPostIdOrderByCreatedAtAsc(1L))
                .thenReturn(List.of(comment));

        when(commentMapper.toCommentResponse(comment))
                .thenReturn(commentResponse);

        List<CommentResponse> result =
                commentService.getCommentsByPost(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(postRepository).existsById(1L);

        verify(commentRepository)
                .findByPostIdOrderByCreatedAtAsc(1L);

        verify(commentMapper).toCommentResponse(comment);
    }

    @Test
    void getCommentsByPost_shouldThrowExceptionWhenPostDoesNotExist() {

        when(postRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                PostNotFoundException.class,
                () -> commentService.getCommentsByPost(99L)
        );

        verify(commentRepository, never())
                .findByPostIdOrderByCreatedAtAsc(any());

        verifyNoInteractions(commentMapper);
    }

    @Test
    void getCommentsByUser_shouldReturnUserComments() {

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(commentRepository.findByAuthorIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(comment));

        when(commentMapper.toCommentResponse(comment))
                .thenReturn(commentResponse);

        List<CommentResponse> result =
                commentService.getCommentsByUser(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(userRepository).existsById(1L);

        verify(commentRepository)
                .findByAuthorIdOrderByCreatedAtDesc(1L);

        verify(commentMapper).toCommentResponse(comment);
    }

    @Test
    void getCommentsByUser_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                UserNotFoundException.class,
                () -> commentService.getCommentsByUser(99L)
        );

        verify(commentRepository, never())
                .findByAuthorIdOrderByCreatedAtDesc(any());

        verifyNoInteractions(commentMapper);
    }

    @Test
    void updateComment_shouldUpdateCommentSuccessfully() {

        CommentUpdateRequest request = new CommentUpdateRequest();
        request.setContent("Updated comment");

        when(commentRepository.findByIdAndPostIdAndAuthorEmail(
                1L,
                1L,
                "test@example.com"
        )).thenReturn(Optional.of(comment));

        when(commentRepository.save(comment))
                .thenReturn(comment);

        CommentResponse updatedResponse = new CommentResponse();
        updatedResponse.setId(1L);
        updatedResponse.setContent("Updated comment");

        when(commentMapper.toCommentResponse(comment))
                .thenReturn(updatedResponse);

        CommentResponse result = commentService.updateComment(
                1L,
                1L,
                "test@example.com",
                request
        );

        assertEquals("Updated comment", comment.getContent());
        assertEquals("Updated comment", result.getContent());

        verify(commentRepository)
                .findByIdAndPostIdAndAuthorEmail(
                        1L,
                        1L,
                        "test@example.com"
                );

        verify(commentRepository).save(comment);
        verify(commentMapper).toCommentResponse(comment);
    }

    @Test
    void updateComment_shouldThrowExceptionWhenUserDoesNotOwnComment() {

        CommentUpdateRequest request = new CommentUpdateRequest();
        request.setContent("Updated comment");

        when(commentRepository.findByIdAndPostIdAndAuthorEmail(
                1L,
                1L,
                "another@example.com"
        )).thenReturn(Optional.empty());

        assertThrows(
                PostNotFoundException.class,
                () -> commentService.updateComment(
                        1L,
                        1L,
                        "another@example.com",
                        request
                )
        );

        verify(commentRepository, never()).save(any());
        verifyNoInteractions(commentMapper);
    }

    @Test
    void updateComment_shouldThrowExceptionWhenPostDoesNotMatch() {

        CommentUpdateRequest request = new CommentUpdateRequest();
        request.setContent("Updated comment");

        when(commentRepository.findByIdAndPostIdAndAuthorEmail(
                1L,
                99L,
                "test@example.com"
        )).thenReturn(Optional.empty());

        assertThrows(
                PostNotFoundException.class,
                () -> commentService.updateComment(
                        99L,
                        1L,
                        "test@example.com",
                        request
                )
        );

        verify(commentRepository, never()).save(any());
        verifyNoInteractions(commentMapper);
    }

    @Test
    void deleteComment_shouldDeleteOwnCommentSuccessfully() {

        when(commentRepository.findByIdAndPostId(1L, 1L))
                .thenReturn(Optional.of(comment));

        commentService.deleteComment(
                1L,
                1L,
                "test@example.com"
        );

        verify(commentRepository).findByIdAndPostId(1L, 1L);
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_shouldAllowPostOwnerToDeleteAnotherUsersComment() {

        Comment anotherUsersComment = new Comment();
        anotherUsersComment.setId(2L);
        anotherUsersComment.setContent("Another user's comment");
        anotherUsersComment.setAuthor(anotherUser);
        anotherUsersComment.setPost(post);

        when(commentRepository.findByIdAndPostId(2L, 1L))
                .thenReturn(Optional.of(anotherUsersComment));

        commentService.deleteComment(
                1L,
                2L,
                "test@example.com"
        );

        verify(commentRepository).delete(anotherUsersComment);
    }

    @Test
    void deleteComment_shouldThrowExceptionWhenUserDoesNotOwnComment() {

        Comment anotherUsersComment = new Comment();
        anotherUsersComment.setId(2L);
        anotherUsersComment.setContent("Another user's comment");
        anotherUsersComment.setAuthor(anotherUser);
        anotherUsersComment.setPost(post);

        when(commentRepository.findByIdAndPostId(2L, 1L))
                .thenReturn(Optional.of(anotherUsersComment));

        assertThrows(
                PostNotFoundException.class,
                () -> commentService.deleteComment(
                        1L,
                        2L,
                        "third@example.com"
                )
        );

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_shouldThrowExceptionWhenCommentBelongsToAnotherPost() {

        when(commentRepository.findByIdAndPostId(2L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                PostNotFoundException.class,
                () -> commentService.deleteComment(
                        1L,
                        2L,
                        "test@example.com"
                )
        );

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void getCommentByIdAndPost_shouldReturnCommentSuccessfully() {

        when(commentRepository.findByIdAndPostId(1L, 1L))
                .thenReturn(Optional.of(comment));

        when(commentMapper.toCommentResponse(comment))
                .thenReturn(commentResponse);

        CommentResponse result =
                commentService.getCommentByIdAndPost(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getPostId());

        verify(commentRepository).findByIdAndPostId(1L, 1L);
        verify(commentMapper).toCommentResponse(comment);
    }

    @Test
    void getCommentByIdAndPost_shouldThrowExceptionWhenCommentDoesNotBelongToPost() {

        when(commentRepository.findByIdAndPostId(1L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(
                PostNotFoundException.class,
                () -> commentService.getCommentByIdAndPost(1L, 99L)
        );

        verifyNoInteractions(commentMapper);
    }
}
