package MIOSM.post_service.service;

import MIOSM.post_service.dto.PostCreateRequestDto;
import MIOSM.post_service.dto.PostResponseDto;
import MIOSM.post_service.dto.PostUpdateRequestDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostService {
    PostResponseDto createPost(PostCreateRequestDto requestDto);
    Optional<PostResponseDto> getPostById(UUID postId);
    Optional<PostResponseDto> updatePost(UUID postId, PostUpdateRequestDto dto);
    boolean deletePost(UUID postId);
    List<PostResponseDto> getPostsByUser(UUID userId);
    
    /**
     * Get all posts by username
     * @param username 
     * @return 
     */
    List<PostResponseDto> getPostsByUsername(String username);
    
    /**
     * Get latest posts from all users for dashboard feed
     * @param limit 
     * @return 
     */
    List<PostResponseDto> getLatestPosts(int limit);
} 