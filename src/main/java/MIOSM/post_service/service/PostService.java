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
     * Get all posts by username with current user context for like status
     * @param username 
     * @param currentUserId 
     * @return 
     */
    List<PostResponseDto> getPostsByUsername(String username, UUID currentUserId);
    
    /**
     * Get latest posts from all users for dashboard feed
     * @param limit 
     * @return 
     */
    List<PostResponseDto> getLatestPosts(int limit);
    
    /**
     * Get latest posts from all users for dashboard feed with current user context
     * @param limit 
     * @param currentUserId 
     * @return 
     */
    List<PostResponseDto> getLatestPosts(int limit, UUID currentUserId);
    
    /**
     * Like a post
     * @param postId
     * @param userId
     * @param username
     * @return 
     */
    boolean likePost(UUID postId, UUID userId, String username);
    
    /**
     * Unlike a post
     * @param postId
     * @param userId
     * @return 
     */
    boolean unlikePost(UUID postId, UUID userId);
    
    /**
     * Check if user liked a post
     * @param postId
     * @param userId
     * @return 
     */
    boolean isPostLikedByUser(UUID postId, UUID userId);
    
    /**
     * Get posts liked by user
     * @param userId
     * @return 
     */
    List<PostResponseDto> getLikedPostsByUser(UUID userId);
    
    /**
     * Get total likes count for all posts by a user
     * @param userId
     * @return 
     */
    long getTotalLikesForUserPosts(UUID userId);
    
    /**
     * Get liked posts by username with current user context for like status
     * @param username
     * @param currentUserId
     * @return 
     */
    List<PostResponseDto> getLikedPostsByUsername(String username, UUID currentUserId);
    
    /**
     * Get total likes count for all posts by username
     * @param username
     * @return 
     */
    long getTotalLikesForUserPostsByUsername(String username);
}