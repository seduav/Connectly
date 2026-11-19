package com.connectly.service.impl;

import com.connectly.dto.request.PostCreateRequest;
import com.connectly.dto.request.PostUpdateRequest;
import com.connectly.dto.response.PostResponse;
import com.connectly.entity.Post;
import com.connectly.entity.User;
import com.connectly.exception.PostNotFoundException;
import com.connectly.exception.UserNotFoundException;
import com.connectly.mapper.PostMapper;
import com.connectly.repository.PostRepository;
import com.connectly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post post;
    private PostResponse postResponse;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        post = new Post();
        post.setId(1L);
        post.setContent("Test post content");
        post.setAuthor(user);

        postResponse = new PostResponse();
        postResponse.setId(1L);
        postResponse.setContent("Test post content");
        postResponse.setAuthorId(1L);
        postResponse.setAuthorUsername("testuser");
    }

    @Test
    void createPost_shouldCreatePostSuccessfully() {

        PostCreateRequest request = new PostCreateRequest();
        request.setContent("  Test post content  ");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.save(any(Post.class)))
                .thenReturn(post);

        when(postMapper.toPostResponse(post))
                .thenReturn(postResponse);

        PostResponse result = postService.createPost(
                "test@example.com",
                request
        );

        assertNotNull(result);
        assertEquals("Test post content", result.getContent());

        ArgumentCaptor<Post> captor =
                ArgumentCaptor.forClass(Post.class);

        verify(postRepository).save(captor.capture());

        assertEquals("Test post content",
                captor.getValue().getContent());

        assertEquals(user, captor.getValue().getAuthor());

        verify(postMapper).toPostResponse(post);
    }

    @Test
    void createPost_shouldThrowExceptionWhenUserDoesNotExist() {

        PostCreateRequest request = new PostCreateRequest();
        request.setContent("Test content");

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> postService.createPost(
                        "unknown@example.com",
                        request
                )
        );

        verify(postRepository, never()).save(any());
        verifyNoInteractions(postMapper);
    }

    @Test
    void getPostById_shouldReturnPostSuccessfully() {

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(postMapper.toPostResponse(post))
                .thenReturn(postResponse);

        PostResponse result = postService.getPostById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(postRepository).findById(1L);
        verify(postMapper).toPostResponse(post);
    }

    @Test
    void getPostById_shouldThrowExceptionWhenPostDoesNotExist() {

        when(postRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                PostNotFoundException.class,
                () -> postService.getPostById(99L)
        );

        verifyNoInteractions(postMapper);
    }

    @Test
    void getAllPosts_shouldReturnAllPosts() {

        when(postRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(post));

        when(postMapper.toPostResponse(post))
                .thenReturn(postResponse);

        List<PostResponse> result = postService.getAllPosts();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(postRepository).findAllByOrderByCreatedAtDesc();
        verify(postMapper).toPostResponse(post);
    }

    @Test
    void getPostsByUser_shouldReturnUserPosts() {

        when(postRepository.findByAuthorIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(post));

        when(postMapper.toPostResponse(post))
                .thenReturn(postResponse);

        List<PostResponse> result = postService.getPostsByUser(1L);

        assertEquals(1, result.size());

        verify(postRepository)
                .findByAuthorIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void updatePost_shouldUpdatePostSuccessfully() {

        PostUpdateRequest request = new PostUpdateRequest();
        request.setContent("Updated content");

        when(postRepository.findByIdAndAuthorEmail(
                1L,
                "test@example.com"
        )).thenReturn(Optional.of(post));

        when(postRepository.save(post)).thenReturn(post);

        PostResponse updatedResponse = new PostResponse();
        updatedResponse.setId(1L);
        updatedResponse.setContent("Updated content");

        when(postMapper.toPostResponse(post))
                .thenReturn(updatedResponse);

        PostResponse result = postService.updatePost(
                1L,
                "test@example.com",
                request
        );

        assertEquals("Updated content", post.getContent());
        assertEquals("Updated content", result.getContent());

        verify(postRepository).save(post);
        verify(postMapper).toPostResponse(post);
    }

    @Test
    void updatePost_shouldThrowExceptionWhenUserDoesNotOwnPost() {

        PostUpdateRequest request = new PostUpdateRequest();
        request.setContent("Updated content");

        when(postRepository.findByIdAndAuthorEmail(
                1L,
                "another@example.com"
        )).thenReturn(Optional.empty());

        assertThrows(
                PostNotFoundException.class,
                () -> postService.updatePost(
                        1L,
                        "another@example.com",
                        request
                )
        );

        verify(postRepository, never()).save(any());
        verifyNoInteractions(postMapper);
    }

    @Test
    void deletePost_shouldDeletePostSuccessfully() {

        when(postRepository.findByIdAndAuthorEmail(
                1L,
                "test@example.com"
        )).thenReturn(Optional.of(post));

        postService.deletePost(1L, "test@example.com");

        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_shouldThrowExceptionWhenUserDoesNotOwnPost() {

        when(postRepository.findByIdAndAuthorEmail(
                1L,
                "another@example.com"
        )).thenReturn(Optional.empty());

        assertThrows(
                PostNotFoundException.class,
                () -> postService.deletePost(
                        1L,
                        "another@example.com"
                )
        );

        verify(postRepository, never()).delete(any());
    }
}
