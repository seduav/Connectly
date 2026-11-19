package com.connectly.service.impl;

import com.connectly.dto.response.LikeResponse;
import com.connectly.entity.Post;
import com.connectly.entity.PostLike;
import com.connectly.entity.User;
import com.connectly.exception.PostNotFoundException;
import com.connectly.exception.UserNotFoundException;
import com.connectly.mapper.LikeMapper;
import com.connectly.repository.PostLikeRepository;
import com.connectly.repository.PostRepository;
import com.connectly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LikeMapper likeMapper;

    @InjectMocks
    private LikeServiceImpl likeService;

    private User user;
    private Post post;
    private PostLike postLike;

    private static final String USER_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail(USER_EMAIL);

        post = new Post();
        post.setId(1L);
        post.setContent("Test post content");
        post.setAuthor(user);

        postLike = new PostLike();
        postLike.setId(1L);
        postLike.setUser(user);
        postLike.setPost(post);
    }

    @Test
    void toggleLike_shouldCreateLikeSuccessfully() {

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(postLikeRepository.findByUserIdAndPostId(1L, 1L))
                .thenReturn(Optional.empty());

        when(postLikeRepository.save(any(PostLike.class)))
                .thenReturn(postLike);

        when(postLikeRepository.countByPostId(1L))
                .thenReturn(1L);

        LikeResponse expectedResponse =
                new LikeResponse(1L, true, 1L);

        when(likeMapper.toLikeResponse(1L, true, 1L))
                .thenReturn(expectedResponse);

        LikeResponse result = likeService.toggleLike(
                1L,
                USER_EMAIL
        );

        assertNotNull(result);
        assertEquals(1L, result.getPostId());
        assertTrue(result.isLiked());
        assertEquals(1L, result.getLikesCount());

        ArgumentCaptor<PostLike> captor =
                ArgumentCaptor.forClass(PostLike.class);

        verify(postLikeRepository).save(captor.capture());

        assertEquals(user, captor.getValue().getUser());
        assertEquals(post, captor.getValue().getPost());

        verify(postLikeRepository, never()).delete(any());
        verify(likeMapper).toLikeResponse(1L, true, 1L);
    }

    @Test
    void toggleLike_shouldRemoveExistingLikeSuccessfully() {

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(postLikeRepository.findByUserIdAndPostId(1L, 1L))
                .thenReturn(Optional.of(postLike));

        when(postLikeRepository.countByPostId(1L))
                .thenReturn(0L);

        LikeResponse expectedResponse =
                new LikeResponse(1L, false, 0L);

        when(likeMapper.toLikeResponse(1L, false, 0L))
                .thenReturn(expectedResponse);

        LikeResponse result = likeService.toggleLike(
                1L,
                USER_EMAIL
        );

        assertNotNull(result);
        assertEquals(1L, result.getPostId());
        assertFalse(result.isLiked());
        assertEquals(0L, result.getLikesCount());

        verify(postLikeRepository).delete(postLike);
        verify(postLikeRepository, never()).save(any());
        verify(likeMapper).toLikeResponse(1L, false, 0L);
    }

    @Test
    void toggleLike_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> likeService.toggleLike(
                        1L,
                        "unknown@example.com"
                )
        );

        verify(postRepository, never()).findById(any());
        verify(postLikeRepository, never()).save(any());
        verifyNoInteractions(likeMapper);
    }

    @Test
    void toggleLike_shouldThrowExceptionWhenPostDoesNotExist() {

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                PostNotFoundException.class,
                () -> likeService.toggleLike(99L, USER_EMAIL)
        );

        verify(postLikeRepository, never())
                .findByUserIdAndPostId(any(), any());

        verify(postLikeRepository, never()).save(any());
        verifyNoInteractions(likeMapper);
    }

    @Test
    void getLikesCount_shouldReturnCorrectCount() {

        when(postRepository.existsById(1L))
                .thenReturn(true);

        when(postLikeRepository.countByPostId(1L))
                .thenReturn(5L);

        long result = likeService.getLikesCount(1L);

        assertEquals(5L, result);

        verify(postRepository).existsById(1L);
        verify(postLikeRepository).countByPostId(1L);
    }

    @Test
    void getLikesCount_shouldThrowExceptionWhenPostDoesNotExist() {

        when(postRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                PostNotFoundException.class,
                () -> likeService.getLikesCount(99L)
        );

        verify(postLikeRepository, never()).countByPostId(any());
    }

    @Test
    void hasUserLikedPost_shouldReturnTrueWhenLikeExists() {

        when(postRepository.existsById(1L))
                .thenReturn(true);

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        when(postLikeRepository.existsByUserIdAndPostId(1L, 1L))
                .thenReturn(true);

        boolean result = likeService.hasUserLikedPost(
                1L,
                USER_EMAIL
        );

        assertTrue(result);

        verify(postLikeRepository)
                .existsByUserIdAndPostId(1L, 1L);
    }

    @Test
    void hasUserLikedPost_shouldReturnFalseWhenLikeDoesNotExist() {

        when(postRepository.existsById(1L))
                .thenReturn(true);

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        when(postLikeRepository.existsByUserIdAndPostId(1L, 1L))
                .thenReturn(false);

        boolean result = likeService.hasUserLikedPost(
                1L,
                USER_EMAIL
        );

        assertFalse(result);
    }

    @Test
    void hasUserLikedPost_shouldThrowExceptionWhenUserDoesNotExist() {

        when(postRepository.existsById(1L))
                .thenReturn(true);

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> likeService.hasUserLikedPost(
                        1L,
                        "unknown@example.com"
                )
        );

        verify(postLikeRepository, never())
                .existsByUserIdAndPostId(any(), any());
    }

    @Test
    void hasUserLikedPost_shouldThrowExceptionWhenPostDoesNotExist() {

        when(postRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                PostNotFoundException.class,
                () -> likeService.hasUserLikedPost(
                        99L,
                        USER_EMAIL
                )
        );

        verifyNoInteractions(userRepository);
        verify(postLikeRepository, never())
                .existsByUserIdAndPostId(any(), any());
    }
}
